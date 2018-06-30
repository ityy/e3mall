package cn.yang.e3mall.sso.controller;

import cn.yang.e3mall.common.utils.CookieUtils;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.common.utils.JsonUtils;
import cn.yang.e3mall.sso.service.TokenService;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 根据token查询用户信息
 */
@Controller
public class TokenController {
    @Autowired
    private TokenService tokenService;


    @RequestMapping("/user/token/{token}")
    @ResponseBody   //返回结果是String也不能缺少ResponseBody，否则浏览器会解析成一个404页面
    public String getUserByToken(@PathVariable String token,String callback) {
        E3Result result = tokenService.getUserByToken(token);
        /*
            在e3mall.js中有ajax请求，类型为jsonp。
            F12拦截下的请求链接为：
            http://localhost:8088/user/token/2a61a40a-c0a1-4c22-9d10-63672567ba47?callback=jsonp1529550721098
         */
        if (StringUtils.isNotBlank(callback)) {
            //将结果拼装为一个js语句，规避浏览器的js不能跨区ajax的问题。
            String js = callback + "(" + JsonUtils.objectToJson(result) + ");";
            return js;
        }
        return JsonUtils.objectToJson(result);
    }

    @RequestMapping("/user/logout/{token}")
    public String logout(@PathVariable String token) {
        E3Result result = tokenService.deleteUserByToken(token);
        return "redirect:http://localhost:8082/";

    }

}
