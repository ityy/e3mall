package cn.yang.e3mall.search.service.impl;

import cn.yang.e3mall.common.pojo.SearchResult;
import cn.yang.e3mall.search.dao.SearchDao;
import cn.yang.e3mall.search.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品搜索Service
 */
@Service
public class SearchServiceImpl implements SearchService  {
    @Autowired
    private SearchDao searchDao;


    @Override
    public SearchResult search(String keyword, int page, int rows) throws Exception{
        //1，创建SolrQuery对象
        SolrQuery query = new SolrQuery();
        //2，设置查询条件
        query.setQuery(keyword);
        //3，设置分页条件
        if (page <= 0) {
            page = 1;
        }
        query.setStart((page - 1) * rows);
        query.setRows(rows);
        //4，设置默认搜索域
        query.set("df", "item_title");
        //5，开启高亮
        query.setHighlight(true);
        query.addHighlightField("item_title");
        query.setHighlightSimplePre("<em style=\"color:red\">");
        query.setHighlightSimplePost("</em>");
        //6，执行查询
        SearchResult result = searchDao.search(query);
        //7，计算总页数
        int totalPage = (int) (result.getRecordCount() / rows);
        if (result.getRecordCount() % rows > 0) {
            totalPage++;
        }
        result.setTotalPages(totalPage);
        //8，返回结果
        return result;
    }
}
