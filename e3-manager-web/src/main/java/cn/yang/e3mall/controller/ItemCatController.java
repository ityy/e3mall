package cn.yang.e3mall.controller;

import cn.yang.e3mall.common.pojo.EasyUITreeNode;
import cn.yang.e3mall.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 后台管理系统-商品分类列表
 * 表现层代码
 */
@Controller
public class ItemCatController {
    @Autowired
    private ItemCatService itemCatService;

    /**
     * 获得商品分类列表
     * @param parentId
     * @return
     */
    @RequestMapping("/item/cat/list")
    @ResponseBody
    public List<EasyUITreeNode> getItemCatList(@RequestParam(value = "id", defaultValue = "0") long parentId) {
        List<EasyUITreeNode> catList = itemCatService.getCatList(parentId);
        return catList;
    }


}
