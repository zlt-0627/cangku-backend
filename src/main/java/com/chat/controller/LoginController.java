package com.chat.controller;


import com.chat.pojo.Result;
import com.chat.pojo.dto.LoginDto;
import com.chat.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class LoginController {
    @Autowired
    private LoginService loginService;

//登入接口,要统一返回结果果
    @GetMapping("/login")
    //拿到前端给的账号密码。在实体类里面做判断
    //使用dto实体类来接收前端的参数
    public String login(@Valid LoginDto loginDto){
        //把这个参数给service层做业务操作
        String jwt=loginService.login(loginDto);
        return jwt;
    }






}
