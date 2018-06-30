package cn.yang.e3mall.order.controller;

import cn.yang.e3mall.cart.service.CartService;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.order.pojo.OrderInfo;
import cn.yang.e3mall.order.service.OrderService;
import cn.yang.e3mall.pojo.TbItem;
import cn.yang.e3mall.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    @RequestMapping("/order/order-cart")
    public String showOrderCart(HttpServletRequest request) {
        //取用户id
        TbUser tbUser = (TbUser) request.getAttribute("user");
        //根据用户id取收获地址列表 支付方式
        //根据用户id取购物车列表
        List<TbItem> cartList = cartService.getCartList(tbUser.getId());
        //把购物车列表传递给jsp
        request.setAttribute("cartList", cartList);
        return "order-cart";
    }


    @RequestMapping(value = "/order/create", method = RequestMethod.POST)
    public String createOrder(OrderInfo orderInfo, HttpServletRequest request) {
        //取用户信息并放入orderInfo
        TbUser user = (TbUser) request.getAttribute("user");
        orderInfo.setUserId(user.getId());
        orderInfo.setBuyerNick(user.getUsername());
        //调用service生成订单
        E3Result result = orderService.createOrder(orderInfo);
        //成功则清空购物车
        if (result.getStatus() == 200) {
            cartService.clearCartItem(user.getId());
        }
        //返回逻辑视图 订单号传递给页面
        request.setAttribute("orderId", result.getData());
        request.setAttribute("payment", orderInfo.getPayment());
        return "success";

    }

}
