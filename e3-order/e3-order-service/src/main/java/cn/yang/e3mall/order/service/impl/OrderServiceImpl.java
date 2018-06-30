package cn.yang.e3mall.order.service.impl;

import cn.yang.e3mall.common.jedis.JedisClient;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.mapper.TbOrderItemMapper;
import cn.yang.e3mall.mapper.TbOrderMapper;
import cn.yang.e3mall.mapper.TbOrderShippingMapper;
import cn.yang.e3mall.order.pojo.OrderInfo;
import cn.yang.e3mall.order.service.OrderService;
import cn.yang.e3mall.pojo.TbOrder;
import cn.yang.e3mall.pojo.TbOrderItem;
import cn.yang.e3mall.pojo.TbOrderShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 订单处理服务
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbOrderShippingMapper orderShippingMapper;

    @Value("${ORDER_ID_KEY}")
    private String ORDER_ID_KEY;
    @Value("${ORDER_ID_START}")
    private String ORDER_ID_START;
    @Value("${ORDER_DETAIL_ID_KEY}")
    private String ORDER_DETAIL_ID_KEY;

    @Override
    public E3Result createOrder(OrderInfo orderInfo) {
        //生成订单号 使用redis的incr生成 不存在则先创建key
        if (!jedisClient.exists(ORDER_ID_KEY)) {
            jedisClient.set(ORDER_ID_KEY,ORDER_ID_START);
        }
        String order_id_gen = jedisClient.incr("ORDER_ID_GEN").toString();
        //补全OrderInfo
        orderInfo.setOrderId(order_id_gen);
        //状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭',
        orderInfo.setStatus(1);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        //插入订单表
        orderMapper.insert(orderInfo);
        //取明细信息并插入订单明细表
        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        for (TbOrderItem tbOrderItem : orderItems) {
            //补全信息
            String order_id = jedisClient.incr(ORDER_DETAIL_ID_KEY).toString();
            tbOrderItem.setId(order_id);
            tbOrderItem.setOrderId(order_id_gen);
            //插入数据库
            orderItemMapper.insert(tbOrderItem);
        }
        //取物流信息并插入订单物流表
        TbOrderShipping orderShipping = orderInfo.getOrderShipping();
        //补全信息并插入
        orderShipping.setOrderId(order_id_gen);
        orderShipping.setCreated(new Date());
        orderShipping.setUpdated(new Date());
        orderShippingMapper.insert(orderShipping);

        //返回成功 包含订单号
        return E3Result.ok(order_id_gen);
    }

}
