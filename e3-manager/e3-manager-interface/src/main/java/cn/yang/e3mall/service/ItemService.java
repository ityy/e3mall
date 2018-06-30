package cn.yang.e3mall.service;

import cn.yang.e3mall.common.pojo.EasyUIDataGridResult;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.pojo.TbItem;

public interface ItemService {
    TbItem getItemById(long itemId);
    EasyUIDataGridResult getItemList(int page, int rows);

    //添加商品 存入两个表 一个表存放商品信息 一个表存放商品描述
    E3Result addItem(TbItem item, String desc);

    E3Result getDescById(long itemId);
    E3Result queryItemById(long itemId);
    E3Result updateItem(TbItem item, String desc);
    E3Result deleteItemById(long itemId);
    E3Result instockAndReshelfItem(long itemId,byte status);

}
