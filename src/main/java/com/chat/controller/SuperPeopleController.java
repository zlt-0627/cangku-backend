package com.chat.controller;


import com.chat.pojo.Result;
import com.chat.pojo.dto.AddRoleDto;
import com.chat.pojo.dto.GoodsDto;
import com.chat.pojo.dto.LoginDto;
import com.chat.pojo.dto.PageDto;
import com.chat.pojo.vo.RoleVo;
import com.chat.service.AddPeopleService;
import com.chat.service.GoodsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.stream.IIOByteBuffer;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class SuperPeopleController {
    @Autowired
    private AddPeopleService addPeopleService;


    //新增人员角色
    @PostMapping("/addrole")
    public Result addpeople(@Valid @RequestBody AddRoleDto addRole, HttpServletRequest request){
        //把数据给service层，并且还要把role给传入，在后面做角色判断。
        Integer e=(Integer) request.getAttribute("role");
        log.info("得到role+{}",e);
        //把前端的数据信息传入
        addPeopleService.addrole(addRole,e);
        return Result.success();
    }
    //逻辑删除管理员数据
    @DeleteMapping("/delete")
    //通过id删除，前端传来的,就一个数据，不用封装
    public Result delete(@RequestBody AddRoleDto addRoleDto){
        addPeopleService.delete(addRoleDto);
        return Result.success();
    }


    //用户查询回显操作
    //这个操作不用传任何参数，只要把所有用户分页查询出来就可以了
    @GetMapping("/select")
    public Result select(@RequestParam(defaultValue = "1")Integer page,@RequestParam(defaultValue = "5")Integer pageSize,HttpServletRequest request){

        int role=(Integer)request.getAttribute("role");
        //直接调用service层就可以
        PageDto pageDto=new PageDto();
        pageDto.setPageSize(pageSize);
        pageDto.setPage(page);

        //把这个页数封装成一个对象传给service
        log.info("查询出所有的用户");
        //这里最后是返回一个实体封装类的
        RoleVo roleVo=addPeopleService.select(pageDto,role);
        return Result.success(roleVo);
    }
    //用户管理最后一个，更改用户
    //更改用户的role,username,password

    @PutMapping("/update")
    public Result upRole(@RequestBody AddRoleDto addRoleDto,HttpServletRequest request){

        //把登入人的role传入
        int role=(Integer) request.getAttribute("role");


        addPeopleService.uprole(addRoleDto,role);


        return Result.success();

    }

    @GetMapping("/like")
    //模糊查询
    public Result likeselect(String username){
        RoleVo roleVo=addPeopleService.select1(username);
        return Result.success(roleVo);
    }


}
