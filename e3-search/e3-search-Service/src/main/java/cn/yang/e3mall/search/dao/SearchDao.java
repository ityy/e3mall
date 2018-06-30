package cn.yang.e3mall.search.dao;

import cn.yang.e3mall.common.pojo.SearchItem;
import cn.yang.e3mall.common.pojo.SearchResult;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 索引库搜索dao
 */
@Repository
public class SearchDao {

    @Autowired
    private SolrClient solrClient;

    /**
     * 根据查询条件查询索引
     *
     * @param query
     * @return
     */
    public SearchResult search(SolrQuery query) throws Exception {
        //1，根据query查询索引库
        QueryResponse queryResponse = solrClient.query(query);
        //2，取查询结果
        SolrDocumentList results = queryResponse.getResults();
        //3，取结果总数
        long numFound = results.getNumFound();
        //4，取商品列表，需要取高亮显示
        List<SearchItem> searchItemList = new ArrayList<>();
            /*
                因为getHighlighting返回的是一个MAP套MAP的集合 所以这么接收
                外Map是以ID为KEY MAP为值 表示一个ID有一个高亮集合
                内Map是以设置的高亮的域名为key  以List为值  表示一个域名里有多个高亮的字符串
                List存储的就是高亮的字符串  只设置一个则get(0)即可取出
            */
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();

        for (SolrDocument solrDocument : results) {
            //新建SearchItem
            SearchItem item = new SearchItem();
            //设置属性
            item.setId((String) solrDocument.get("id"));
            item.setCategory_name((String) solrDocument.get("item_category_name"));
            item.setPrice((Long) solrDocument.get("item_price"));
            item.setSell_point((String) solrDocument.get("item_sell_point"));
            item.setImage((String) solrDocument.get("item_image"));
            //设置title属性前，处理高亮显示
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            String title = "";
            //判断是否有高亮内容  由于只设置了一个高亮 所以直接get(0)即可
            if (list != null && list.size() > 0) {
                title = list.get(0);
            } else {
                title = (String) solrDocument.get("item_title");
            }
            item.setTitle(title);
            //添加到list
            searchItemList.add(item);

        }

        //5，创建结果对象并返回
        SearchResult searchResult = new SearchResult();
        searchResult.setRecordCount(numFound);
        searchResult.setItemlist(searchItemList);
        return searchResult;
    }
}
