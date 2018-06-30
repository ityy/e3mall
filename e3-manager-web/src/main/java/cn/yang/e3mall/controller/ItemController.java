package cn.yang.e3mall.controller;

import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.pojo.TbItem;
import cn.yang.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 后台管理系统-商品相关
 * 表现层代码
 */
@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;


    /**
     * 添加商品
     *
     * @param item
     * @param desc
     * @return
     */
    //指定url映射同时指定请求方法
    @RequestMapping(value = "/item/save", method = RequestMethod.POST)
    //返回json格式
    @ResponseBody
    public E3Result addItem(TbItem item, String desc) {
        E3Result result = itemService.addItem(item, desc);
        return result;
    }

    /**
     * 获取商品描述
     *
     * @param id
     * @return
     */
    //指定url映射
    @RequestMapping("/item/desc/{id}")
    //返回json格式
    @ResponseBody
    public E3Result getDescById(@PathVariable long id) {
        E3Result result = itemService.getDescById(id);
        return result;
    }


    /**
     * 查询商品-根据id
     *
     * @param id
     * @return
     */
    //指定url映射
    @RequestMapping("/item/query/{id}")
    //返回json格式
    @ResponseBody
    public E3Result queryItemById(@PathVariable long id) {
        E3Result result = itemService.queryItemById(id);
        return result;
    }

    /**
     * 更新商品
     *
     * @param item
     * @param desc
     * @return
     */
    //指定url映射同时指定请求方法
    @RequestMapping(value = "/item/update", method = RequestMethod.POST)
    //返回json格式
    @ResponseBody
    public E3Result updateItem(TbItem item, String desc) {
        E3Result result = itemService.updateItem(item, desc);
        return result;
    }

    /**
     * 下架商品
     *
     * @param ids
     * @return
     */
    //指定url映射同时指定请求方法
    @RequestMapping(value = "/item/instock", method = RequestMethod.POST)
    //返回json格式
    @ResponseBody
    public E3Result instockItem(String ids) {
        String idss[] = ids.split(",");
        for (String s : idss) {
            System.out.println("正在下架的商品ID为:" + s);
            itemService.instockAndReshelfItem(Long.valueOf(s), (byte) 2);
        }
        return E3Result.ok();
    }


    /**
     * 上架商品
     *
     * @param ids
     * @return
     */
    //指定url映射同时指定请求方法
    @RequestMapping(value = "/item/reshelf", method = RequestMethod.POST)
    //返回json格式
    @ResponseBody
    public E3Result reshelfItem(String ids) {
        String idss[] = ids.split(",");
        for (String s : idss) {
            System.out.println("正在上架的商品ID为:" + s);
            itemService.instockAndReshelfItem(Long.valueOf(s), (byte) 1);
        }
        return E3Result.ok();
    }


    /**
     * 删除商品
     *
     * @param ids
     * @return
     */
    //指定url映射同时指定请求方法
    @RequestMapping("/item/delete")
    //返回json格式
    @ResponseBody
    public E3Result deleteItem(String ids) {
        String idss[] = ids.split(",");
        for (String s : idss) {
            System.out.println("正在删除的商品ID为:" + s);
            itemService.deleteItemById(Long.valueOf(s));
        }

        return E3Result.ok();
    }

}
