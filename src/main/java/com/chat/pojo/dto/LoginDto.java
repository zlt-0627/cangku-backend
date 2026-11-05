package com.chat.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

//把前端的参数传过来，要在这里做判空提示
public class LoginDto {
    @NotBlank(message = "用户名不为空！")
    private String username;
    @NotBlank(message = "密码不为空！")
    private String password;



}
