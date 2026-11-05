package com.chat.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddRoleDto {
    private Integer id;
    //接收新增角色
    @NotBlank(message = "用户名不可以为空！")
    private String username;
    @NotBlank(message = "密码不可以为空！")
    private String password;
    private Integer role;

}
