package cn.yang.e3mall.order.service;

import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.order.pojo.OrderInfo;

public interface OrderService {
    E3Result createOrder(OrderInfo orderInfo);

}
