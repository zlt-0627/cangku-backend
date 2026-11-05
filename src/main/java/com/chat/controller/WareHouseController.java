package com.chat.controller;


import com.chat.pojo.Result;
import com.chat.pojo.dto.WareDto;
import com.chat.pojo.vo.RoleVo;
import com.chat.service.WareHuoseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ware")
//仓库管理接口
//这个仓库管理是单一的对仓库进行操作
public class WareHouseController {
    @Autowired
    private WareHuoseService wareHuoseService;


    public static Integer getrole(HttpServletRequest request){
        Integer role =(Integer) request.getAttribute("role");
        return role;
    }

    //增加仓库

    //这里增加一个仓库要把信息写入
    @PostMapping("/addware")
    public Result addWare(@RequestBody WareDto wareDto, HttpServletRequest request){
        //得到这个role
        Integer role =(Integer) request.getAttribute("role");
        wareHuoseService.addWare(wareDto,role);
        return Result.success();
    }



    //查询仓库信息
    @GetMapping("/selectware")
    public Result selectWare(@RequestParam(defaultValue = "5") Integer pageSize,@RequestParam(defaultValue = "1") Integer page){

        RoleVo roleVo = wareHuoseService.selectWare(page, pageSize);
        return Result.success(roleVo);
    }



    //修改仓库信息,通过仓库信息来找到该信息
    @PutMapping("/updateware")
    public Result updateWare(@RequestBody WareDto wareDto,HttpServletRequest request){
        Integer getrole = getrole(request);
        wareHuoseService.updateWare(wareDto,getrole);
        return Result.success();
    }


    //逻辑删除仓库
    @DeleteMapping("/deleteware")
    //通过id来删除仓库的数据
    public Result deleteWare(@RequestParam Integer id,HttpServletRequest request){
        Integer getrole = getrole(request);
        wareHuoseService.deleteWare(id,getrole);
        return Result.success();

    }


}
