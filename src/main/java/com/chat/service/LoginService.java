package com.chat.service;

import com.chat.pojo.dto.LoginDto;

//业务层类方法
public interface LoginService {
    //登入方法,要给controller返回jwt立牌的token
    //要接收到前端的账号密码
    public String login(LoginDto loginDto);

}
