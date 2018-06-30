package cn.yang.e3mall.content.service;

import cn.yang.e3mall.common.pojo.EasyUITreeNode;
import cn.yang.e3mall.common.utils.E3Result;

import java.util.List;
/**
 * 后台管理系统-内容分类管理
 * 相关业务接口
 */
public interface ContentCategoryService {
    List<EasyUITreeNode> getContentCatList(long parentId);

    //增加的节点 放在哪个父节点之下 其id是由数据库自增产生的
    E3Result addContentCategory(long parentId, String name);
    //更新和删除节点 提供id即可
    E3Result updateContentCategory(long id, String name);
    E3Result deleteContentCategory(long id);
}
