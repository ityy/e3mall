package cn.yang.e3mall.cart.service.impl;

import cn.yang.e3mall.cart.service.CartService;
import cn.yang.e3mall.common.jedis.JedisClient;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.common.utils.JsonUtils;
import cn.yang.e3mall.mapper.TbItemMapper;
import cn.yang.e3mall.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车处理service
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private TbItemMapper itemMapper;

    @Value("${REDIS_CART_PRE}")
    private String REDIS_CART_PRE;

    @Override
    public E3Result addCart(long userId, long itemId, int num) {
        //向redis添加购物车, 数据类型是hash
        //key:用户id  field:商品id  value:商品信息
        //判断商品是否存在 存在则相加,不存在则取商品信息后添加到购物车列表
        Boolean hexists = jedisClient.hexists(REDIS_CART_PRE + ":" + userId, itemId + "");
        if (hexists) {
            //存在 取出并修改
            String json = jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
            TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
            tbItem.setNum(tbItem.getNum() + num);
            //写回redis
            jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
            return E3Result.ok();

        } else {
            //不存在 根据商品id取商品
            TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
            //设置购物车数量  这里pojo为复用,在购物车为商品数量,在商品详情页为库存量
            tbItem.setNum(num);
            //设置一张购物车显示的图片5620
            String image = tbItem.getImage();
            if (StringUtils.isNotBlank(image)) {
                tbItem.setImage(image.split(",")[0]);

            }
            //写回redis
            jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
            return E3Result.ok();
        }
    }

    @Override
    public List<TbItem> getCartList(long userId) {
        //获取value的集合
        List<String> hvals = jedisClient.hvals(REDIS_CART_PRE + ":" + userId);
        List<TbItem> itemList = new ArrayList<>();
        //遍历value转为对象再放入新建的集合
        for (String s : hvals) {
            TbItem tbItem = JsonUtils.jsonToPojo(s, TbItem.class);
            itemList.add(tbItem);
        }

        return itemList;
    }


    @Override
    public E3Result mergeCart(long userId, List<TbItem> itemList) {
        for (TbItem tbItem : itemList) {
            addCart(userId, tbItem.getId(), tbItem.getNum());

        }
        return E3Result.ok();
    }


    /**
     * 清空购物车
     * @param userId
     * @return
     */
    @Override
    public E3Result clearCartItem(long userId) {
        //删除redis中的购物车信息
        jedisClient.del(REDIS_CART_PRE + ":" + userId);//直接删除hash
        return E3Result.ok();
    }
}
