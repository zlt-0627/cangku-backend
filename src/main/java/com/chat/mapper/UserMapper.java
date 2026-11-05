package com.chat.mapper;


import com.chat.pojo.po.LoginPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

//@Mapper
public interface UserMapper {

    @Select("select id,password,role from login where username=#{username} and is_delete=1")
    public LoginPo login(LoginPo loginPo);

//    @Insert("insert into login(username, password,role) values(#{username},#{password},#{role})")
//    public void insert(LoginPo loginPo);

}
