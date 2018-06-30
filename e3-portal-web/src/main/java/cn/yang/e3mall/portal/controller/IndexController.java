package cn.yang.e3mall.portal.controller;

import cn.yang.e3mall.content.service.ContentService;
import cn.yang.e3mall.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {
    @Value("${CONTENT_LUNBO_ID}")
    private Long CONTENT_LUNBO_ID;

    @Autowired
    private ContentService contentService;

    @RequestMapping("/index")
    public String showIndex(Model model) {
        //查询内容列表
        List<TbContent> contentList = contentService.getContentListByCid(CONTENT_LUNBO_ID);
        //向页面传递数据
        model.addAttribute("ad1List",contentList);
        return "index";
    }
}
