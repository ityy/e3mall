package cn.yang.e3mall.sso.service.impl;

import cn.yang.e3mall.common.jedis.JedisClient;
import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.common.utils.JsonUtils;
import cn.yang.e3mall.pojo.TbUser;
import cn.yang.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private JedisClient jedisClient;

    @Value("${USER_INFO}")
    private String USER_INFO;
    @Value("${SESSION_EXPIRE}")
    private int SESSION_EXPIRE;

    @Override
    public E3Result getUserByToken(String token) {
        //根据token到redis中取用户名
        String json = jedisClient.get(USER_INFO + ":" + token);
        //取不到说明redis中已经过期，返回登陆已过期
        if (StringUtils.isBlank(json)) {
            return E3Result.build(201,"用户登陆已过期");
        }
        //取到后 转为pojo并刷新redis的过期时间
        TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
        jedisClient.expire(USER_INFO + ":" + token,SESSION_EXPIRE);
        //返回结果
        return E3Result.ok(user);
    }

    @Override
    public E3Result deleteUserByToken(String token) {
        //根据token到redis中取用户名
        String json = jedisClient.get(USER_INFO + ":" + token);
        //存在则删除
        if (StringUtils.isNotBlank(json) && !json.equals("{}")) {
            //取到后 删除token
            jedisClient.del(USER_INFO + ":" + token);
        }
        //返回结果
        return E3Result.ok();
    }
}
