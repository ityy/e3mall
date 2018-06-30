package cn.yang.e3mall.item.controller;

import cn.yang.e3mall.item.pojo.Item;
import cn.yang.e3mall.pojo.TbItem;
import cn.yang.e3mall.pojo.TbItemDesc;
import cn.yang.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 商品详情页面展示
 */
@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;


    @RequestMapping("/item/{itemId}")
    public String showItemInfo(@PathVariable Long itemId, Model model) {
        //调用服务获取商品信息
        TbItem tbItem = itemService.getItemById(itemId);
        TbItemDesc itemDesc = (TbItemDesc) itemService.getDescById(itemId).getData();
        //封装到Item
        Item item = new Item(tbItem);
        //传递到页面
        model.addAttribute("item", item);
        model.addAttribute("itemDesc", itemDesc);
        //返回逻辑视图
        return "item";


    }
}
