package cn.yang.e3mall.content.service.impl;

import cn.yang.e3mall.common.jedis.JedisClient;
import cn.yang.e3mall.common.pojo.EasyUIDataGridResult;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.common.utils.JsonUtils;
import cn.yang.e3mall.content.service.ContentService;
import cn.yang.e3mall.mapper.TbContentMapper;
import cn.yang.e3mall.pojo.TbContent;
import cn.yang.e3mall.pojo.TbContentExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.jboss.netty.util.internal.StringUtil;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 后台管理系统-内容管理
 * 相关业务处理
 */
@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private TbContentMapper contentMapper;
    @Autowired
    private JedisClient jedisClient;

    @Value("${CONTENT_LIST}")
    private String CONTENT_LIST;


    @Override
    public E3Result addContent(TbContent content) {
        //1，补全信息
        content.setCreated(new Date());
        content.setUpdated(new Date());
        //2，插入数据库
        contentMapper.insert(content);

        //3，缓存同步 清空旧的缓存信息，避免缓存和数据库不一致
        jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());

        return E3Result.ok();
    }

    @Override
    public E3Result editContent(TbContent content) {
        //1，补全信息
        content.setUpdated(new Date());
        //2，更新到数据库
        contentMapper.updateByPrimaryKey(content);
        //3，缓存同步 清空旧的缓存信息，避免缓存和数据库不一致
        jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
        return E3Result.ok();
    }


    @Override
    public EasyUIDataGridResult getContentList(long categoryId,Integer page, Integer rows) {
        //设置分页信息 开始页数,页内大小 只对之后的一条查询语句有效
        PageHelper.startPage(page, rows);
        //执行查询
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> list = contentMapper.selectByExample(example);
        //取分页信息
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        //创建返回结果对象
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal((int) pageInfo.getTotal());
        result.setRows(list);

        return result;
    }

    @Override
    public E3Result deleteContentById(long id) {
        //1，取出对象，用于获取CID以便同步缓存
        TbContent content = contentMapper.selectByPrimaryKey(id);
        //2，删除商品以及商品描述
        contentMapper.deleteByPrimaryKey(id);
        //3，缓存同步 清空旧的缓存信息，避免缓存和数据库不一致
        jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
        return E3Result.ok();
    }

    @Override
    public List<TbContent> getContentListByCid(long cid) {
        //查询缓存 不能影响正常业务逻辑 放到try里
        try {
            //将存储的json字符串取出
            String json = jedisClient.hget(CONTENT_LIST, cid + "");
            //如果有缓存 则取出并转为list后返回
            if (StringUtils.isNotBlank(json)) {
                List<TbContent> tbContentList = JsonUtils.jsonToList(json, TbContent.class);
                return tbContentList;
            }
            //如果无缓存则继续向下 查询数据库
        } catch (Exception e) {
            e.printStackTrace();
        }

        //1，根据分类id查询内容 由于不是主键 使用example来查询
        TbContentExample example=new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(cid);
        List<TbContent> tbContentList = contentMapper.selectByExample(example);

        //添加缓存 不能影响正常业务逻辑 放到try里
        try {
            jedisClient.hset(CONTENT_LIST, cid + "", JsonUtils.objectToJson(tbContentList));

        } catch (Exception e) {
            e.printStackTrace();
        }


        return tbContentList;
    }
}
