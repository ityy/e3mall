package cn.yang.e3mall.service.impl;

import cn.yang.e3mall.common.jedis.JedisClient;
import cn.yang.e3mall.common.pojo.EasyUIDataGridResult;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.common.utils.IDUtils;
import cn.yang.e3mall.common.utils.JsonUtils;
import cn.yang.e3mall.mapper.TbItemDescMapper;
import cn.yang.e3mall.mapper.TbItemMapper;
import cn.yang.e3mall.pojo.TbItem;
import cn.yang.e3mall.pojo.TbItemDesc;
import cn.yang.e3mall.pojo.TbItemExample;
import cn.yang.e3mall.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.Date;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicDestination;
    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_ITEM_PRE}")
    private String REDIS_ITEM_PRE;
    @Value("${REDIS_ITEM_EXPIRE}")
    private Integer REDIS_ITEM_EXPIRE;

    @Override
    public TbItem getItemById(long itemId) {
        //查询缓存
        try {
            String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":BASE");
            if (StringUtils.isNotBlank(json)) {
                TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
                System.out.println("ItemServiceImpl_getItemById()_" + REDIS_ITEM_PRE + ":" + itemId + ":BASE" + "_：从缓存中查询了一条商品信息！");
                return tbItem;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //缓存中没有，查询数据库
        //根据主键查询
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        //加入缓存
        try {
            //将对象转为json字符串后放入指定的key
            jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":BASE", JsonUtils.objectToJson(tbItem));
            //设置过期时间 1小时后过期 即一个商品1小时内只查一次数据库 避免了访问率低的商品被放入缓存
            jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":BASE", REDIS_ITEM_EXPIRE);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbItem;

    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {
        //设置分页信息 开始页数,页内大小 只对之后的一条查询语句有效
        PageHelper.startPage(page, rows);
        //执行查询
        TbItemExample example = new TbItemExample();
        List<TbItem> list = tbItemMapper.selectByExample(example);
        //取分页信息
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);

        //创建返回结果对象
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal((int) pageInfo.getTotal());
        result.setRows(list);

        return result;
    }

    @Override
    public E3Result addItem(TbItem item, String desc) {
        //思路流程
        //1 生成商品id
        long itemId = IDUtils.genItemId();
        //2 补全属性
        item.setId(itemId);
        //Status商品状态，1-正常，2-下架，3-删除
        item.setStatus((byte) 1);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        //3 存入数据库商品表
        tbItemMapper.insert(item);
        //4 创建商品描述对应的pojo对象
        TbItemDesc itemDesc = new TbItemDesc();
        //5 补全属性
        itemDesc.setItemId(itemId);
        itemDesc.setItemDesc(desc);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(new Date());
        //6 存入数据库商品描述表
        tbItemDescMapper.insert(itemDesc);
        //额外：发送商品添加消息 （注意到这里发送消息在事务提交之前，接收消息端应适当等待）
        jmsTemplate.send(topicDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(itemId + "");
                return textMessage;
            }
        });
        //7 返回成功信息
        return E3Result.ok();
    }

    @Override
    public E3Result getDescById(long itemId) {
        //查询缓存
        try {
            String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":DESC");
            if (StringUtils.isNotBlank(json)) {
                TbItemDesc itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
                System.out.println("ItemServiceImpl_getDescById()_" + REDIS_ITEM_PRE + ":" + itemId + ":DESC" + "_：从缓存中查询了一条商品描述信息！");
                return E3Result.ok(itemDesc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //缓存中没有，查询数据库
        //根据id查询
        TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);


        //加入缓存
        try {
            //将对象转为json字符串后放入指定的key
            jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemDesc));
            //设置过期时间 1小时后过期 即一个商品1小时内只查一次数据库 避免了访问率低的商品被放入缓存
            jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":DESC", REDIS_ITEM_EXPIRE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return E3Result.ok(itemDesc);
    }

    @Override
    public E3Result queryItemById(long itemId) {
        //根据id查询
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        return E3Result.ok(item);
    }

    @Override
    public E3Result updateItem(TbItem item, String desc) {
        //修改更新时间
        item.setUpdated(new Date());
        //向数据库更新
        tbItemMapper.updateByPrimaryKeySelective(item);

        //创建商品描述对象 并设置相关参数
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        itemDesc.setUpdated(new Date());
        //向数据库更新
        tbItemDescMapper.updateByPrimaryKeySelective(itemDesc);

        return E3Result.ok();
    }

    @Override
    public E3Result deleteItemById(long itemId) {
        //删除商品以及商品描述
        tbItemMapper.deleteByPrimaryKey(itemId);
        tbItemDescMapper.deleteByPrimaryKey(itemId);
        return E3Result.ok();
    }

    @Override
    public E3Result instockAndReshelfItem(long itemId, byte status) {
        TbItem item = new TbItem();
        item.setId(itemId);
        item.setStatus(status);
        item.setUpdated(new Date());
        tbItemMapper.updateByPrimaryKeySelective(item);
        return E3Result.ok();
    }


}
