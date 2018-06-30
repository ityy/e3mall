package cn.yang.e3mall.sso.service;

import cn.yang.e3mall.common.utils.E3Result;

public interface TokenService {
    E3Result getUserByToken(String token);
    E3Result deleteUserByToken(String token);
}
