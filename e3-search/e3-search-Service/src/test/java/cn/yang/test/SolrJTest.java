package cn.yang.test;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrJTest {

    // 单点
    public static final String SOLR_URL = "http://192.168.25.131:8983/solr/e3mall_core";

    public void addDocument() throws Exception {

        //1，创建一个SolrClient对象 创建一个连接，参数solr服务的url

        //2，创建一个文档对象SolrInputDocument
        //3，向文档对象添加域
        //4，把文档写入索引库
        //5，提交
    }


    @Test
    public void AddDocs() {
        try {
            SolrClient solr = new HttpSolrClient.Builder(SOLR_URL).build();
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", "doc01");
            document.addField("item_title", "测试商品01");
            document.addField("item_price", "1000");
            UpdateResponse response = solr.add(document);
            solr.commit();

        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("addsuccess");
    }


}
