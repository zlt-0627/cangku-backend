package com.chat.service.impl;

import com.chat.annotation.BoolRole;
import com.chat.config.PasswordConfigure;
import com.chat.mapper.AddRoleMapper;
import com.chat.pojo.dto.AddRoleDto;
import com.chat.pojo.dto.PageDto;
import com.chat.pojo.dto.SelectRoleDto;
import com.chat.pojo.po.AddRolePo;
import com.chat.pojo.vo.RoleVo;
import com.chat.service.AddPeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AddRoleSeerviceImpl implements AddPeopleService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AddRoleMapper addRoleMapper;

    @BoolRole
    @Transactional
    //新增用户操作
    public void addrole(AddRoleDto addRoleDto,Integer e){
        AddRolePo addRolePo=new AddRolePo();//这里的对象默认是单例的，可能会出现并发问题        //先得到role做身份验证
        //使用了aop动态代理技术
//        if(e!=3){
//            //抛出异常
//            throw new RuntimeException("权限不足，不可以操作！");
//        }

        //这里面做一个用户名的查询，如果存在说明可能是被逻辑删除了，只要改一下is_delete
        addRolePo.setUsername(addRoleDto.getUsername());
        AddRolePo username=addRoleMapper.selectrole(addRolePo);
//把数据传给service层里面，要做一个用户的名字判断
        if(username!=null){
            addRolePo.setUsername(username.getUsername());
            addRolePo.setVersion(username.getVersion());
            addRolePo.setRole(addRoleDto.getRole());
            String encopassword=passwordEncoder.encode(addRoleDto.getPassword());//加密
            addRolePo.setPassword(encopassword);
           addRoleMapper.delete1(addRolePo);

        }else{
            String encopassword=passwordEncoder.encode(addRoleDto.getPassword());//加密
            addRolePo.setRole(addRoleDto.getRole());
            addRolePo.setPassword(encopassword);
            //传给mapper层
            addRoleMapper.addrole(addRolePo);
        }

        //要把前端的密码加密，然后在插入数据库


    }

    //逻辑删除用户
    public void delete(AddRoleDto addRoleDto){
        AddRolePo addRolePo=new AddRolePo();
        addRolePo.setUsername(addRoleDto.getUsername());
        //判空
        AddRolePo addRolePo1=addRoleMapper.select1(addRolePo);
        if(addRolePo1==null){
            throw new RuntimeException("用户不存在！");
        }
        //在这里addRolePo1已经拿到了username，version了
        addRoleMapper.delete(addRolePo1);

    }
    //分页查询
    @BoolRole
    public RoleVo select(PageDto pageDto, Integer role){
        //得到哪一页
        int page=pageDto.getPage();
        int start=(page-1)* pageDto.getPageSize();


        //只有管理员才可以进行人员的更改
//        if(role!=3){
//            throw new RuntimeException("权限不足");
//        }
        //这里查出总的数
        int count=addRoleMapper.count();

        //在这里得到数组和总数
       List<SelectRoleDto> list=addRoleMapper.select(start,pageDto.getPageSize());
    //使用构造方法把数据封装
        RoleVo rv=new RoleVo();
        rv.setRows(list);
        rv.setTotals(count);
        return rv;
    }

    @BoolRole
    @Transactional
    //修改用户,还要校验
    public void uprole(AddRoleDto addRoleDto,Integer role){
        AddRolePo addRolePo=new AddRolePo();
        //先判断生份
//        if(role!=3){
//            throw new RuntimeException("权限不足！");
//        }

        //修改密码要加密
        String password=passwordEncoder.encode(addRoleDto.getPassword());
        //这里使用po接收
        addRolePo.setId(addRoleDto.getId());
        addRolePo.setRole(addRoleDto.getRole());
        addRolePo.setPassword(password);
        addRolePo.setUsername(addRoleDto.getUsername());
        //修改前先做判断,在这里我前面有一点小问题所以我增加一个sql
        AddRolePo addRolePo1=addRoleMapper.getid(addRolePo);
        if(addRolePo1==null){
            throw new RuntimeException("用户不存在！");
        }
        //修改，要使用动态sql,还要使用乐观锁，通过username删除的。
        //把上面的版本号传入





        int rows=addRoleMapper.update(addRolePo,addRolePo1.getVersion());
        //这里做了乐观锁
        if(rows==0){
            throw new RuntimeException("系统错误，请联系管理员");
        }

    }

    //模糊查询
    public RoleVo select1(String username){
        List<SelectRoleDto> list=addRoleMapper.select2(username);
        RoleVo roleVo=new RoleVo();
        roleVo.setRows(list);
        return roleVo;


    }






}























