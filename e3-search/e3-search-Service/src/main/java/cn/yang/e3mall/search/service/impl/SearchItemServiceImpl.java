package cn.yang.e3mall.search.service.impl;

import cn.yang.e3mall.common.pojo.SearchItem;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.search.mapper.ItemMapper;
import cn.yang.e3mall.search.service.SearchItemService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 索引库维护servcie
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrClient solrClient;


    @Override
    public E3Result importAllItems() {
        //1，从数据库查询商品列表
        List<SearchItem> itemList = itemMapper.getItemList();
        try {
            //2，遍历商品列表，逐个加入索引库
            for (SearchItem searchItem : itemList) {
                //创建文档对象 并添加对应的域
                SolrInputDocument document = new SolrInputDocument();
                document.addField("id", searchItem.getId());
                document.addField("item_title", searchItem.getTitle());
                document.addField("item_price", searchItem.getPrice());
                document.addField("item_sell_point", searchItem.getSell_point());
                document.addField("item_image", searchItem.getImage());
                document.addField("item_category_name", searchItem.getCategory_name());
                solrClient.add(document);
            }
            //3，提交
            solrClient.commit();
            //4，返回成功
            return E3Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return E3Result.build(500,"导入索引库失败！");
        }

    }
}
