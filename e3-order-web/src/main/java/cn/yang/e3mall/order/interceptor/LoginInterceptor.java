package cn.yang.e3mall.order.interceptor;

import cn.yang.e3mall.cart.service.CartService;
import cn.yang.e3mall.common.utils.CookieUtils;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.common.utils.JsonUtils;
import cn.yang.e3mall.pojo.TbItem;
import cn.yang.e3mall.pojo.TbUser;
import cn.yang.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Value("${SSO_URL}")
    private String SSO_URL;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private CartService cartService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //前处理,执行handler之前执行此方法
        //1 从cookie取token
        String token = CookieUtils.getCookieValue(httpServletRequest, "e3token");
        //2 如果没token 没有登陆 则跳转到登陆页面使其登陆
        if (StringUtils.isBlank(token)) {
            //跳转到登陆页面 并将原url作为参数传递,使登陆之后可以跳回来
            httpServletResponse.sendRedirect(SSO_URL + "/page/loin?redirect=" + httpServletRequest.getRequestURL());
            return false;
        }
        //3 取到token 调用sso获取用户信息
        E3Result e3Result = tokenService.getUserByToken(token);
        //判断是否取到用户信息
        if (e3Result.getStatus() == 200) {
            //5 取到用户信息 登陆状态
            TbUser tbUser = (TbUser) e3Result.getData();
            //6 把用户信息放入request 放行给controller
            httpServletRequest.setAttribute("user", tbUser);
            //7 判断并合并cookie购物车
            String json = CookieUtils.getCookieValue(httpServletRequest, "cart", true);
            if (StringUtils.isNotBlank(json)) {
                cartService.mergeCart(tbUser.getId(), JsonUtils.jsonToList(json, TbItem.class));
            }
            //返回ture放行 false拦截
            return true;

        } else {
            //没有取到用户信息, redis过期, 重新登陆
            //跳转到登陆页面 并将原url作为参数传递,使登陆之后可以跳回来
            httpServletResponse.sendRedirect(SSO_URL + "/page/login?redirect=" + httpServletRequest.getRequestURL());
            return false;
        }

    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
