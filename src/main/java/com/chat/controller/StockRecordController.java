package com.chat.controller;


import com.chat.mapper.StockRecordMapper;
import com.chat.pojo.Result;
import com.chat.pojo.po.StockRecordPo;
import com.chat.pojo.vo.RoleVo;
import com.chat.service.StockRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stockrecord")
public class StockRecordController {

    @Autowired
    private StockRecordService stockRecordService;

    //这个流水表只要查询就可以，这个插入操作放在了别的业务里面
    @GetMapping("/selectrecord")
    //这里还是分页查询,权限为一的不可以看到流水表
    public Result selectRecord(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "5") Integer pageSize, HttpServletRequest request){
        Integer role =(Integer) request.getAttribute("role");
        //这里返回分页的结果
        RoleVo roleVo = stockRecordService.selectRecord(page, pageSize, role);
        return Result.success(roleVo);
    }





}
