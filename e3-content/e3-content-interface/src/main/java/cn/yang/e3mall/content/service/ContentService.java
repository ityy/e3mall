package cn.yang.e3mall.content.service;

import cn.yang.e3mall.common.pojo.EasyUIDataGridResult;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.pojo.TbContent;

import java.util.List;

/**
 * 后台管理系统-内容管理
 * 相关业务接口
 */
public interface ContentService {
    E3Result addContent(TbContent content);
    E3Result editContent(TbContent content);
    E3Result deleteContentById(long id);
    EasyUIDataGridResult getContentList(long categoryId, Integer page, Integer rows);

    List<TbContent> getContentListByCid(long cid);
}
