package cn.yang.e3mall.search.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GlobalExceptionReslover implements HandlerExceptionResolver {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionReslover.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        //打印控制台 查看异常信息
        ex.printStackTrace();

        //写日志文件 三个级别
        logger.debug("测试输出的日志...");
        logger.info("系统发生异常_info");
        logger.error("系统发生异常_error", ex);
        //发邮件、发短信
        //Jmail：可以查找相关的资料
        //需要购买短信服务，调用第三方接口即可。
        //展示错误页面
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "系统发生异常，请稍后重试");
        modelAndView.setViewName("error/exception");
        return modelAndView;
    }

}
