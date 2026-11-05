package com.chat.pojo.po;


import lombok.Data;

@Data
public class AddRolePo {
    private Integer id;
    private String username;
    private String password;
    private Integer role;
    private Integer is_delete;
    private Integer version;

}
