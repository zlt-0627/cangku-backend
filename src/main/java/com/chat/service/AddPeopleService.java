package com.chat.service;

import com.chat.pojo.dto.AddRoleDto;
import com.chat.pojo.dto.PageDto;
import com.chat.pojo.vo.RoleVo;

import java.util.List;

public interface AddPeopleService {
    //新增人员实现类
    public void addrole(AddRoleDto addRoleDto,Integer e);
    public void delete(AddRoleDto addRoleDto);

    public RoleVo select(PageDto pageDto, Integer role);

    //修改用户
    public void uprole(AddRoleDto addRoleDto,Integer role);
    //模糊查询
    public RoleVo select1(String username);


}


