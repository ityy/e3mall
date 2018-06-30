package cn.yang.e3mall.cart.interceptor;

import cn.yang.e3mall.common.utils.CookieUtils;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.pojo.TbUser;
import cn.yang.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 判断是否登陆的拦截器
 * 不登陆直接放行
 * 登陆则取用户信息再放行
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //前处理,执行handler之前执行此方法
        //1 从cookie取token
        String token = CookieUtils.getCookieValue(httpServletRequest, "e3token");
        //2 如果没token 没有登陆 直接放行
        if (StringUtils.isBlank(token)) {
            return true;
        }
        //3 取到token 调用sso获取用户信息
        E3Result e3Result = tokenService.getUserByToken(token);
        //4 没有取到用户信息, redis过期, 直接放行
        if (e3Result.getStatus() != 200) {
            return true;
        }
        //5 取到用户信息 登陆状态
        TbUser tbUser= (TbUser) e3Result.getData();
        //6 把用户信息放入request 放行给controller
        httpServletRequest.setAttribute("user", tbUser);
        //返回ture放行 false拦截
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
