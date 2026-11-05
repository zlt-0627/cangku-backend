package com.chat.pojo.vo;


//前端只要返回id还有用户的姓名和role就可以

import com.chat.pojo.dto.AddRoleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleVo {
    //返回总数
   private Integer totals;
    //回显出姓名 role id
    private List rows;
}
