package com.chat.mapper;


import com.chat.pojo.dto.AddRoleDto;
import com.chat.pojo.dto.PageDto;
import com.chat.pojo.dto.SelectRoleDto;
import com.chat.pojo.po.AddRolePo;
import com.chat.pojo.vo.RoleVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddRoleMapper {

    //查看现有员工，判断是否会有重名
    //增加行锁
    @Select("select username,version from login where username=#{username} ")
    public AddRolePo selectrole(AddRolePo addRolePo);


    //新增员工数据
    @Insert("insert into login(username,password,role) values(#{username},#{password},#{role})")
    public void addrole(AddRolePo addRolePo);

   @Select("select username,version from login where username=#{username} and is_delete=1")
   public AddRolePo select1(AddRolePo addRolePo);

@Update("update login set is_delete=0,version=version+1 where username=#{username} and version=#{version}")
    public Integer delete(AddRolePo addRolePo);


    @Update("update login set is_delete=1,version=version+1,password=#{password},role=#{role} where username=#{username} and version=#{version}")
    public Integer delete1(AddRolePo addRolePo);


    //查出总数
    @Select("select count(*) from login where is_delete=1")
    public int count();

    //分页查询，注意映射问题
    public List<SelectRoleDto> select(Integer start, Integer pageSize);

    //查询用户,带id的
    @Select("select * from login where is_delete=1 and id=#{id}")
    public AddRolePo getid(AddRolePo addRolePo);


    //修改用户
    public Integer update(AddRolePo addRolePo,Integer version);

    //模糊查询
    @Select("select id,username,role from login where username like concat('%',#{username},'%')")
    public List<SelectRoleDto> select2(String username);

}
