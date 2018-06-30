package cn.yang.e3mall.controller;

import cn.yang.e3mall.common.pojo.EasyUIDataGridResult;
import cn.yang.e3mall.pojo.TbItem;
import cn.yang.e3mall.pojo.TbItemExample;
import cn.yang.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 后台管理系统-页面放行及商品列表
 * 表现层代码
 */
@Controller
public class PageController {
    @Autowired
    private ItemService itemService;

    @RequestMapping("/")
    public String showIndex() {
        return "index";
    }


    /**
     * 页面放行
     * jsp页面都放在了隐藏文件夹内,通过此方法访问jsp页面
     *
     * @param page
     * @return
     */
    @RequestMapping("/{page}")
    public String showPage(@PathVariable String page) {
        //视图解释器中配置了前缀和后缀 这里只给出名字即可
        return page;

    }

    /**
     * 商品列表-根据页面展示
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/item/list")
    @ResponseBody
    public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
        EasyUIDataGridResult result = itemService.getItemList(page, rows);
        return result;
    }


}
