package com.chat.pojo.po;


import com.chat.interceptor.Interceptor;
import lombok.Data;

@Data
//这个实体类要和数据库保持一致
public class LoginPo {
    private Integer id;
    private String username;
    private String password;
    private Integer role;
}
