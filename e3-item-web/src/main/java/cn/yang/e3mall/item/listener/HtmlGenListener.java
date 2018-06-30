package cn.yang.e3mall.item.listener;

import cn.yang.e3mall.common.pojo.SearchItem;
import cn.yang.e3mall.item.pojo.Item;
import cn.yang.e3mall.pojo.TbItem;
import cn.yang.e3mall.pojo.TbItemDesc;
import cn.yang.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * 监听商品添加消息，生成对应的静态页面
 */
public class HtmlGenListener implements MessageListener {
    @Autowired
    private ItemService itemService;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${HTML_GEN_PATH}")
    private String HTML_GEN_PATH;

    @Override
    public void onMessage(Message message) {
        try {
            //创建一个模板
            //从消息中取出商品id
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            Long itemId = new Long(text);
            //等待消息发送端的事务提交后 再查询数据库
            Thread.sleep(500);
            //根据商品ID查询商品信息
            TbItem tbItem = itemService.getItemById(itemId);
            Item item = new Item(tbItem);
            //取商品描述
            TbItemDesc itemDesc = (TbItemDesc)itemService.getDescById(itemId).getData();
            //创建一个数据集，把商品数据封装
            Map data = new HashMap<>();
            data.put("item", item);
            data.put("itemDesc", itemDesc);
            //加载模板对象
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            //创建输出流，指定输出的路径及文件名
            Writer out = new FileWriter(HTML_GEN_PATH + itemId + ".html");
            //使用模板生成静态页面
            template.process(data, out);
            //关闭流
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
