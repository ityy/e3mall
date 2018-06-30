package cn.yang.e3mall.cart.service;

import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.pojo.TbItem;

import java.util.List;

public interface CartService {
    E3Result addCart(long userId, long itemId, int num);

    List<TbItem> getCartList(long userId);

    E3Result mergeCart(long userId, List<TbItem> itemList);
    E3Result clearCartItem(long userId);

}
