package cn.yang.e3mall.search.controller;

import cn.yang.e3mall.common.pojo.SearchResult;
import cn.yang.e3mall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品搜索（从索引库）
 */
@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;

    @Value("${SEARCH_RESULT_ROWS}")
    private Integer SEARCH_RESULT_ROWS;

    @RequestMapping("/search")
    public String searchItemList(String keyword, @RequestParam(defaultValue = "1") Integer page, Model model) throws Exception{
        //解决乱码
        keyword = new String(keyword.getBytes("iso-8859-1"), "utf-8");
        //查询商品列表
        SearchResult result = searchService.search(keyword, page, SEARCH_RESULT_ROWS);
        //传递给页面
        model.addAttribute("query", keyword);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("recordCount", result.getRecordCount());
        model.addAttribute("itemList", result.getItemlist());

        return "search";


    }

}
