package cn.yang.e3mall.cart.controller;

import cn.yang.e3mall.cart.service.CartService;
import cn.yang.e3mall.common.utils.CookieUtils;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.common.utils.JsonUtils;
import cn.yang.e3mall.pojo.TbItem;
import cn.yang.e3mall.pojo.TbUser;
import cn.yang.e3mall.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车处理web层
 */
@Controller
public class CartController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private CartService cartService;

    @Value("${COOKIE_CART_EXPIRE}")
    private int COOKIE_CART_EXPIRE;

    /**
     * 从cookie取购物车列表
     *
     * @param request
     * @return
     */
    private List<TbItem> getCartListFromCookie(HttpServletRequest request) {
        //整个购物车数据是一个json字符串
        String json = CookieUtils.getCookieValue(request, "cart", true);
        //判空
        if (StringUtils.isBlank(json)) {
            //空则返回一个空list
            return new ArrayList<>();
        }
        //把json转换成商品列表
        List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
        return list;
    }


    /**
     * 添加到购物车
     *
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/cart/add/{itemId}")
    //第一个是链接带的参数  第二个是?后面的传统提交的参数,设一个默认值
    public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue = "1") Integer num, HttpServletRequest request, HttpServletResponse response) {
        //判断用户是否登陆, 登陆则写入redis, 未登陆则写入cookie
        TbUser tbUser = (TbUser) request.getAttribute("user");
        if (tbUser != null) {
            //用户登陆 走redis
            //保存到服务器 判断是否已存在的逻辑由service层完成
            cartService.addCart(tbUser.getId(), itemId, num);
        } else {
            //用户未登陆 走cookies
            //由于购物车处理是cookie的处理 表现层可以取到请求和响应 所以在表现层处理
            //从cookie取购物车列表, 判断itemId是否在购物车已存在. 存在则数量加1, 不存在则加入cookie
            //1 从cookie取购物车列表
            List<TbItem> cartListFromCookie = getCartListFromCookie(request);
//        List<TbItem> cartListFromCookie = new ArrayList<>();
            //2 判断是否已存在
            boolean flag = false;
            for (TbItem tbItem : cartListFromCookie) {
                //对象直接比较,对比的是对象的引用地址. 所以itemId这个包装数据类型需要使用longValue方法转为简单数据类型
                if (tbItem.getId() == itemId.longValue()) {
                    //找到商品
                    flag = true;
                    //找到商品 数量相加
                    tbItem.setNum(tbItem.getNum() + num);
                    //跳出循环
                    break;
                }
            }
            //3 不存在则加入
            if (flag == false) {
                TbItem tbItem = itemService.getItemById(itemId);
                tbItem.setNum(num);
                //取一张图片,作为购物车里显示的图片
                String image = tbItem.getImage();
                if (StringUtils.isNotBlank(image)) {
                    tbItem.setImage(image.split(",")[0]);
                }
                //加入到列别
                cartListFromCookie.add(tbItem);

            }
            //4 写入cookie
            CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartListFromCookie), COOKIE_CART_EXPIRE, true);
        }
        //返回逻辑视图
        return "cartSuccess";


    }


    /**
     * 展示购物车
     *
     * @param request
     * @return
     */
    @RequestMapping("/cart/cart")
    public String showCatList(HttpServletRequest request, HttpServletResponse response) {
        //从cookie中取购物车
        List<TbItem> cartList = getCartListFromCookie(request);

        //判断用户是否登陆, 登陆则写入redis, 未登陆则写入cookie
        TbUser tbUser = (TbUser) request.getAttribute("user");
        if (tbUser != null) {
            //不为空 表示用户已登陆
            //将cookie中存在的购物车合并到redis并删除
            cartService.mergeCart(tbUser.getId(), cartList);
            CookieUtils.deleteCookie(request, response, "cart");
            //从redis取购物车
            cartList = cartService.getCartList(tbUser.getId());
        }
        //放入请求域内
        request.setAttribute("cartList", cartList);
        //返回逻辑视图
        return "cart";


    }

    /**
     * 更新购物车商品数量
     *
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public E3Result updateCartNum(@PathVariable Long itemId, @PathVariable Integer num, HttpServletRequest request, HttpServletResponse response) {
        //从cookie取购物车
        List<TbItem> cartListFromCookie = getCartListFromCookie(request);
        for (TbItem tbItem : cartListFromCookie) {
            if (tbItem.getId().longValue() == itemId) {
                //更新数量
                tbItem.setNum(num);
                break;
            }
        }
        //写回cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartListFromCookie), COOKIE_CART_EXPIRE, true);
        //返回成功
        return E3Result.ok();
    }

    /**
     * 删除购物车商品
     */
    @RequestMapping("/cart/delete/{itemID}")
    public String deleteCartItem(@PathVariable Long itemID, HttpServletRequest request, HttpServletResponse response) {
        //从cookie中取购物车列表
        List<TbItem> cartListFromCookie = getCartListFromCookie(request);
        //遍历列表, 找到要删除的商品
        for (TbItem tbItem : cartListFromCookie) {
            if (tbItem.getId().longValue() == itemID) {
                //删除
                cartListFromCookie.remove(tbItem);
                break;
            }
        }
        //写回cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartListFromCookie), COOKIE_CART_EXPIRE, true);
        //返回成功
        return "redirect:/cart/cart.html";

    }

    // TODO: 2018/6/26
    // 登陆状态下的修改删除购物车内商品
}
