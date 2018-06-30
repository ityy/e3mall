package cn.yang.e3mall.controller;

import cn.yang.e3mall.common.pojo.EasyUITreeNode;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.content.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 后台管理系统-内容分类管理
 * 表现层代码
 */
@Controller
public class ContentCatController {
    @Autowired
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/content/category/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentCatList(@RequestParam(name = "id", defaultValue = "0") Long ParentId) {
        List<EasyUITreeNode> contentCatList = contentCategoryService.getContentCatList(ParentId);
        return contentCatList;
    }

    @RequestMapping("/content/category/create")
    @ResponseBody
    public E3Result createContentCategory(long parentId, String name) {
        E3Result result = contentCategoryService.addContentCategory(parentId, name);
        return result;
    }

    @RequestMapping("/content/category/update")
    @ResponseBody
    public E3Result updateContentCategory(long id, String name) {
        E3Result result = contentCategoryService.updateContentCategory(id, name);
        return result;
    }

    @RequestMapping("/content/category/delete")
    @ResponseBody
    public E3Result deleteContentCategory(long id) {
        E3Result result = contentCategoryService.deleteContentCategory(id);
        return result;
    }


}
