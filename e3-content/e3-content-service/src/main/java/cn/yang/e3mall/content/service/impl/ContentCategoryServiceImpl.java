package cn.yang.e3mall.content.service.impl;

import cn.yang.e3mall.common.pojo.EasyUITreeNode;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.content.service.ContentCategoryService;
import cn.yang.e3mall.mapper.TbContentCategoryMapper;
import cn.yang.e3mall.pojo.TbContentCategory;
import cn.yang.e3mall.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 后台管理系统-内容分类管理
 * 相关业务处理
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    @Override
    public List<EasyUITreeNode> getContentCatList(long parentId) {
        //1 根据parentId查询子节点列表 因为不是主键 所以使用example设置条件来查询
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //2 执行查询
        List<TbContentCategory> categoryList = contentCategoryMapper.selectByExample(example);
        //3 创建EasyUITreeNode
        List<EasyUITreeNode> nodeList = new ArrayList<>();
        for (TbContentCategory category : categoryList) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(category.getId());
            node.setText(category.getName());
            node.setState(category.getIsParent() ? "closed" : "open");
            //加入list
            nodeList.add(node);
        }
        return nodeList;
    }


    @Override
    public E3Result addContentCategory(long parentId, String name) {
        //1，创建pojo
        TbContentCategory contentCategory = new TbContentCategory();
        //2，给pojo赋值
        contentCategory.setParentId(parentId);
        contentCategory.setName(name);
        contentCategory.setStatus(1);//1正常，2删除
        contentCategory.setSortOrder(1);//默认排序为1
        contentCategory.setIsParent(false);//新添加的节点一定是叶子节点
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(new Date());
        //3，插入数据库 由于配置了返回ID的sql语句，插入后会把自增长的ID返回到pojo的属性内
        contentCategoryMapper.insert(contentCategory);
        //4，判断父节点的isparent属性, 如果父节点原来是一个叶子节点，则将其修改为父节点
        TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
        if (parent.getIsParent() == false) {
            parent.setIsParent(true);
            contentCategoryMapper.updateByPrimaryKey(parent);
        }
        //5，返回结果
        return E3Result.ok(contentCategory);
    }


    @Override
    public E3Result updateContentCategory(long id, String name) {
        //1，创建pojo
        TbContentCategory contentCategory = new TbContentCategory();
        //2，设置要更新的值
        contentCategory.setId(id);
        contentCategory.setName(name);
        contentCategory.setUpdated(new Date());
        //3，更新到数据库
        contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        //4，返回成功
        return E3Result.ok();
    }

    @Override
    public E3Result deleteContentCategory(long id) {
        //1，判断是否为父节点，是则直接返回，并提示禁止删除
        TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
        if (contentCategory.getIsParent() == true) {
            //这种判断前台也可以做，但是在后台做才是最安全的，防止前台被越过直接发来删除的命令，后台做判断还是最安全的！
            E3Result result = new E3Result(400, "您只能删除子节点！",null);
            return result;
        }
        //2，删除之前保存父节点
        Long parentId = contentCategory.getParentId();
        contentCategoryMapper.deleteByPrimaryKey(id);
        //3，父节点下还有子节点，则不变动。否则修改父节点为叶子节点。
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        if (contentCategoryMapper.countByExample(example) == 0) {
            //创建需要更新的信息，并更新到数据库
            TbContentCategory parent = new TbContentCategory();
            parent.setId(parentId);
            parent.setIsParent(false);
            parent.setUpdated(new Date());
            contentCategoryMapper.updateByPrimaryKeySelective(parent);

        }
        return E3Result.ok();
    }
}
