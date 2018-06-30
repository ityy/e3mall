package cn.yang.test;


import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @Description: 高级查询
 */

public class SolrQueryProTest {
    final private String SOLR_URL = "http://192.168.25.131:8983/solr/e3mall_core";

    @Test
    public void testQuery() throws Exception {
        SolrClient solrClient = new HttpSolrClient.Builder(this.SOLR_URL).build();
        SolrQuery query = new SolrQuery();
        //设置查询条件
        query.setQuery("钻石");
        //过滤条件
        query.setFilterQueries("product_catalog_name:幽默杂货");
        //排序条件
        query.setSort("product_price", SolrQuery.ORDER.asc);
        //分页处理
        query.setStart(0);
        query.setRows(10);
        //结果中域的列表
        query.setFields("id", "product_name", "product_price", "product_catalog_name", "product_picture");
        //设置默认搜索域
        query.set("df", "product_keywords");
        //打开高亮显示
        query.setHighlight(true);
        //高亮显示的域
        query.addHighlightField("product_name");
        //高亮显示的前缀  使用red红色显示
        query.setHighlightSimplePre("<span style='color:red'>");
        //高亮显示的后缀
        query.setHighlightSimplePost("</span>");
        //执行查询
        QueryResponse queryResponse = solrClient.query(query);
        //取查询结果  高亮显示和普通显示不在一个结果集中
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        //共查询到商品数量
        System.out.println("共查询到商品数量:" + solrDocumentList.getNumFound());
        //遍历查询的结果
        int i = 0;
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println("--------第 " + i++ + " 条--------");

            System.out.println(solrDocument.get("id"));
            //取高亮显示  单独使用queryResponse.getHighlighting()取高亮显示的结果集
            String productName = "";
            /*
                因为getHighlighting返回的是一个MAP套MAP的集合 所以这么接收
                外Map是以ID为KEY MAP为值 表示一个ID有一个高亮集合
                内Map是以设置的高亮的域名为key  以List为值  表示一个域名里有多个高亮的字符串
                List存储的就是高亮的字符串  只设置一个则get(0)即可取出
            */
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("product_name");
            //判断是否有高亮内容  由于只设置了一个高亮 所以直接get(0)即可
            if (null != list) {
                productName = list.get(0);
            } else {
                productName = (String) solrDocument.get("product_name");
            }

            System.out.println(productName);
            System.out.println(solrDocument.get("product_price"));
            System.out.println(solrDocument.get("product_catalog_name"));
            System.out.println(solrDocument.get("product_picture"));

        }

        System.out.println("---------结 束---------");
    }

}