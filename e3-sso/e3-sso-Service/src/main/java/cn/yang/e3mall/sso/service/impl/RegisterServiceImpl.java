package cn.yang.e3mall.sso.service.impl;

import cn.yang.e3mall.common.utils.E3Result;
import cn.yang.e3mall.mapper.TbUserMapper;
import cn.yang.e3mall.pojo.TbUser;
import cn.yang.e3mall.pojo.TbUserExample;
import cn.yang.e3mall.sso.service.RegisterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * 用户注册处理Service
 */
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private TbUserMapper tbUserMapper;


    /**
     * 检测是否可以注册
     *
     * @param param
     * @param type
     * @return
     */
    @Override
    public E3Result checkData(String param, int type) {
        //根据不同的type生成不同的查询条件
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        switch (type) {
            case 1:
                criteria.andUsernameEqualTo(param);
                break;
            case 2:
                criteria.andPhoneEqualTo(param);
                break;
            case 3:
                criteria.andEmailEqualTo(param);
                break;
            default:
                return E3Result.build(400, "数据类型错误");
        }
        //执行查询
        List<TbUser> tbUsers = tbUserMapper.selectByExample(example);
        if (tbUsers != null && tbUsers.size() > 0) {
            //有数据返回false
            return E3Result.ok(false);
        }
        //无数据返回ture
        return E3Result.ok(true);
    }


    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public E3Result register(TbUser user) {
        //再次校验数据合法性 防止黑客跳过表现层的校验直接发表单数据过来
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getPhone())) {
            return E3Result.build(400, "数据不完整，注册失败");
        }
        if ((boolean) checkData(user.getUsername(), 1).getData() == false) {
            return E3Result.build(400, "用户名已占用，注册失败");
        }
        if ((boolean) checkData(user.getPhone(), 2).getData() == false) {
            return E3Result.build(400, "手机号已占用，注册失败");
        }
        //补全pojo
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //加密password spring自带一个md5工具
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        //插入数据库
        tbUserMapper.insert(user);
        //返回添加成功
        return E3Result.ok();
    }
}
