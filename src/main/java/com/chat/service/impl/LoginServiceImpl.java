package com.chat.service.impl;


import com.chat.exception.OwerException;
import com.chat.mapper.UserMapper;
import com.chat.pojo.dto.LoginDto;
import com.chat.pojo.po.LoginPo;
import com.chat.service.LoginService;
import com.chat.until.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Jwt jwtUtil;


    @Override
    //在这里面做业务操作,登入的操作
    public String login(LoginDto loginDto){
          LoginPo loginPo=new LoginPo();
//        //模拟插入操作
//        String username="朱浪涛";
//        String password="zlt200627";
//        Integer role=4;
//       String encopassword= passwordEncoder.encode(password);
//
//        loginPo.setUsername(username);
//        loginPo.setPassword(encopassword);
//        loginPo.setRole(role);
//        userMapper.insert(loginPo);


        //不可以直接用dto的数据给数据库，不安全，这里要给密码加密，不可以传密码去匹配

       loginPo.setUsername(loginDto.getUsername());
//       loginPo.setPassword(loginDto.getPassword());//这里面不用给role，登入还没有role
        //把数据传给mapper层
        LoginPo loginPo1=userMapper.login(loginPo);//拿到数据库里面的账号密码

        // 用户不存在时 loginPo1 是 null，直接 .getPassword() 会空指针，
        // 前端拿到的是 500 + 一段 Java 异常信息。这里统一成"账号或密码错误"，
        // 顺便也避免通过报错差异被人枚举出哪些用户名是存在的。
        if (loginPo1 == null) {
            throw new OwerException("账号或密码错误");
        }
        boolean e=passwordEncoder.matches(loginDto.getPassword(),loginPo1.getPassword());
        if(!e){
            throw new OwerException("账号或密码错误");
        }
        //密码正确给一个jwt令牌,把id和role给jwt.
        Map<String, Object> map=new HashMap();
        map.put("id",loginPo1.getId());
        map.put("role",loginPo1.getRole());

        return jwtUtil.createToken(map);
    }


}
