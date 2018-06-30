package cn.yang.e3mall.controller;

import cn.yang.e3mall.mapper.TbItemMapper;
import cn.yang.e3mall.pojo.TbItem;
import cn.yang.e3mall.pojo.TbItemExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class PageTest {
//    @Test
//    public void testPageHelper() throws Exception {
//        //初始化spring容器
//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
//        //获得Mapper的代理对象
//        TbItemMapper itemMapper = applicationContext.getBean(TbItemMapper.class);
//        //设置分页信息 只对紧跟的一条sql有效
//        PageHelper.startPage(1, 30);
//        //执行查询
//        TbItemExample example = new TbItemExample();
//        List<TbItem> list = itemMapper.selectByExample(example);
//        //取分页信息
//        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
//        System.out.println("总记录数:"+pageInfo.getTotal());
//        System.out.println("总页数:"+pageInfo.getPages());
//        System.out.println("当前页:"+pageInfo.getPageNum());
//        System.out.println("页记录数:"+pageInfo.getPageSize());
//    }

}
