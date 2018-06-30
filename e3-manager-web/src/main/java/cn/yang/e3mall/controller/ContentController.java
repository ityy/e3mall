package cn.yang.e3mall.controller;

import cn.yang.e3mall.common.pojo.EasyUIDataGridResult;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.content.service.ContentService;
import cn.yang.e3mall.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 后台管理系统-内容管理-表现层代码
 * 页面展示的广告等内容， 这些内容也是分类管理和维护的
 */
@Controller
public class ContentController {
    @Autowired
    private ContentService contentService;

    /**
     * 添加
     * @param tbContent
     * @return
     */
    @RequestMapping("/content/save")
    @ResponseBody
    public E3Result addContent(TbContent tbContent) {
        return contentService.addContent(tbContent);

    }

    /**
     * 编辑
     * @param tbContent
     * @return
     */
    @RequestMapping("/rest/content/edit")
    @ResponseBody
    public E3Result editContent(TbContent tbContent) {
        return contentService.editContent(tbContent);

    }
    /**
     * 删除
     * @param
     * @return
     */
    @RequestMapping("/content/delete")
    @ResponseBody
    public E3Result deleteContentById(String ids) {
        String idss[] = ids.split(",");
        for (String s : idss) {
            System.out.println("正在删除的内容ID为:" + s);
            contentService.deleteContentById(Long.valueOf(s));
        }

        return E3Result.ok();

    }


    /**
     * 内容列表-根据页面展示
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/content/query/list")
    @ResponseBody
    public EasyUIDataGridResult getContentList(long categoryId,Integer page, Integer rows) {
        EasyUIDataGridResult result = contentService.getContentList(categoryId,page, rows);
        return result;
    }
}
