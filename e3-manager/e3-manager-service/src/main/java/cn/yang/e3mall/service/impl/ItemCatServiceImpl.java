package cn.yang.e3mall.service.impl;

import cn.yang.e3mall.common.pojo.EasyUITreeNode;
import cn.yang.e3mall.mapper.TbItemCatMapper;
import cn.yang.e3mall.pojo.TbItemCat;
import cn.yang.e3mall.pojo.TbItemCatExample;
import cn.yang.e3mall.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Override
    public List<EasyUITreeNode> getCatList(long parentId) {
        //创建example
        TbItemCatExample example = new TbItemCatExample();
        //设置查询规则
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //使用查询规则进行查询 并接收结果
        List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(example);

        //创建一个list 存放EasyUITreeNode
        List<EasyUITreeNode> easyUITreeNodes = new ArrayList<>();
        for (TbItemCat tbItemCat : tbItemCats) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbItemCat.getId());
            node.setText(tbItemCat.getName());
            node.setState(tbItemCat.getIsParent() ? "closed" : "open");
            easyUITreeNodes.add(node);
        }
        return easyUITreeNodes;
    }
}
