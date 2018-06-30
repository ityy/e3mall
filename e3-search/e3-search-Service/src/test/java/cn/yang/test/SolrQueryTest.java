package cn.yang;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;

/**
 * API简单操作 查询演示
 *
 * @author Administrator
 */
public class SolrQueryTest {
    // solr 部署的url
    private static final String url = "http://192.168.25.131:8983/solr";
    // home
    private static final String uri = "e3mall_core";

    //条件查询
    private static void selectDoc() throws SolrServerException, IOException {
        //得到查询连接
        SolrClient sc = getSolrClient();
        SolrQuery query = new SolrQuery();

        /*一、 查询参数说明
        在做solr查询的时候，solr提供了很多参数来扩展它自身的强大功能！以下是使用频率最高的一些参数！ */

        /*1、常用
        q :查询字符串，这个是必须的。如果查询所有*:* ，根据指定字段查询（Name:张三 AND Address:北京）  注意：AND要大写 否则会被当做默认OR*/
        query.setQuery("*:*");//id:id AND title:solr

        /*fq :
         * （filter query）过虑查询，作用：在q查询符合结果中同时是fq查询符合的，
         * 例如：q=查询全部&fq=只要title得值为:伟帅你是我偶吧*/
//        query.setFilterQueries("title:伟帅你是我偶吧");

        /*fl :
         *  指定返回那些字段内容，用逗号或空格分隔多个。 */
//        query.setFields("id,title"); //只返回id 和title

        /*start :
         * 返回第一条记录在完整找到结果中的偏移位置，0开始，一般分页用。*/
        query.setStart(0);

        /*rows :
         *  指定返回结果最多有多少条记录，配合start来实现分页。*/
        query.setRows(10);

        /*sort :
         *  排序，格式：sort=<field name>+<desc|asc>[,<field name>+<desc|asc>]… 。
         *  示例：（score desc, price asc）表示先 “score” 降序, 再 “price” 升序，默认是相关性降序。 */
//        SortClause sort = new SortClause("id", ORDER.desc);
//        query.setSort(sort);//可以添加集合

        /*hl
         *  是否高亮 ,如hl=true*/
        query.setHighlight(true);
        /*【注：以上是比较常用的参数，当然具体的参数使用还是多看Solr官方的技术文档以及一些大神的博文日志，这里只是抛砖引玉】*/
        /*二、 Solr运算符

        1. “:” 指定字段查指定值，如返回所有值*:*

        2. “?” 表示单个任意字符的通配

        3. “*” 表示多个任意字符的通配（不能在检索的项开始使用*或者?符号）

        4. “~” 表示模糊检索，如检索拼写类似于”roam”的项这样写：roam~将找到形如foam和roams的单词；roam~0.8，检索返回相似度在0.8以上的记录。

        5. 邻近检索，如检索相隔10个单词的”apache”和”jakarta”，”jakarta apache”~10

        6. “^” 控制相关度检索，如检索jakarta apache，同时希望去让”jakarta”的相关度更加好，那么在其后加上”^”符号和增量值，即jakarta^4 apache

        7. 布尔操作符AND、||

        8. 布尔操作符OR、&&

        9. 布尔操作符NOT、!、- （排除操作符不能单独与项使用构成查询）
        10. “+” 存在操作符，要求符号”+”后的项必须在文档相应的域中存在
        11. ( ) 用于构成子查询
        12. [] 包含范围检索，如检索某时间段记录，包含头尾，date:[200707 TO 200710]
        13. {} 不包含范围检索，如检索某时间段记录，不包含头尾
        date:{200707 TO 200710}
        14. / 转义操作符，特殊字符包括+ - && || ! ( ) { } [ ] ^ ” ~ * ? : /

         注：①“+”和”-“表示对单个查询单元的修饰，and 、or 、 not 是对两个查询单元是否做交集或者做差集还是取反的操作的符号

        　　 比如:AB:china +AB:america ,表示的是AB:china忽略不计可有可无，必须满足第二个条件才是对的,而不是你所认为的必须满足这两个搜索条件

        　　 如果输入:AB:china AND AB:america ,解析出来的结果是两个条件同时满足，即+AB:china AND +AB:america或+AB:china +AB:america

        　　总而言之，查询语法：  修饰符 字段名:查询关键词 AND/OR/NOT 修饰符 字段名:查询关键词
*/
        //查询
        SolrDocumentList solrDocumentList = sc.query(query).getResults();
        //遍历结果集
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println(solrDocument); //打印为：SolrDocument{id=id, content_test=[我深深地爱着你，solr !], _version_=1560544234369449984}
//            System.out.println(solrDocument.get("id"));
//            System.out.println(solrDocument.get("title"));
//            System.out.println(solrDocument.get("_version_"));
        }
    }

    public static void main(String[] args) throws SolrServerException, IOException {
        System.out.println("测试开始：===================");
        selectDoc();
        System.out.println("测试结束：===================");
    }

    /**
     * 该对象有两个可以使用，都是线程安全的 1、CommonsHttpSolrServer：启动web服务器使用的，通过http请求的 2、
     * EmbeddedSolrServer：内嵌式的，导入solr的jar包就可以使用了 3、solr
     * 4.0之后好像添加了不少东西，其中CommonsHttpSolrServer这个类改名为HttpSolrClient
     *
     * @return
     */
    public static SolrClient getSolrClient() {
        return new HttpSolrClient.Builder(url + "/" + uri).build();
    }
}