package cn.yang.e3mall.search.message;

import cn.yang.e3mall.common.pojo.SearchItem;
import cn.yang.e3mall.search.mapper.ItemMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 监听商品添加消息，收到消息后，将对应商品信息同步到索引库
 */
public class ItemAddMessageListener implements MessageListener {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrClient solrClient;


    @Override
    public void onMessage(Message message) {
        try {


            //从消息中取出商品id
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            Long itemId = new Long(text);
            //等待消息发送端的事务提交后 再查询数据库
            Thread.sleep(500);
            //根据商品ID查询商品信息
            SearchItem searchItem = itemMapper.getItemById(itemId);
            //创建文档对象 并添加对应的域
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", searchItem.getId());
            document.addField("item_title", searchItem.getTitle());
            document.addField("item_price", searchItem.getPrice());
            document.addField("item_sell_point", searchItem.getSell_point());
            document.addField("item_image", searchItem.getImage());
            document.addField("item_category_name", searchItem.getCategory_name());
            //加入索引库并提交
            solrClient.add(document);
            solrClient.commit();
            System.out.println("ItemAddMessageListener：索引库中新加入了一个商品信息！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
