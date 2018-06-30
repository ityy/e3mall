package cn.yang.e3mall.sso.service;

import cn.yang.e3mall.common.utils.E3Result;

public interface LoginService {
    E3Result login(String username, String password);
}
