package cn.yang.e3mall.sso.controller;

import cn.yang.e3mall.common.utils.CookieUtils;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.sso.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录处理
 */
@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Value("${COOKIE_TOKEN_KEY}")
    private String COOKIE_TOKEN_KEY;

    /**
     * 展示登陆页面
     *
     * @return
     */
    @RequestMapping("/page/login")
    public String showLogin(String redirect,Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }

    /**
     * 登陆业务处理
     *
     * @param username
     * @param password
     * @param request  用于取cookie
     * @param response 用于写cookie
     * @return
     */
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public E3Result login(String username, String password,
                          HttpServletRequest request, HttpServletResponse response) {
        // 1、接收两个参数。
        // 2、调用Service进行登录。
        E3Result result = loginService.login(username, password);
        //登陆正确则将信息写入cookie
        if (result.getStatus() == 200) {
            // 3、从返回结果中取token，写入cookie。Cookie要跨域。
            String token = result.getData().toString();
            //使用提供的cookie工具类。 含有解决cookie跨域的问题等（一级域名必须相同）。 过期时间不设置则默认为浏览器关闭cookie失效。
            CookieUtils.setCookie(request, response, COOKIE_TOKEN_KEY, token);
        }
        // 4、响应数据。Json数据。e3Result，其中包含Token。
        return result;

    }


}
