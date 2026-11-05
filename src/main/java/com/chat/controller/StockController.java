package com.chat.controller;


import com.chat.pojo.Result;
import com.chat.pojo.dto.StockDto;
import com.chat.pojo.vo.RoleVo;
import com.chat.service.StockService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stock")
//库存操作
public class StockController {
    @Autowired
    private StockService stockService;

    //新增库存，把仓库和商品连接起来
    @PostMapping("/addstock")
    public Result addStock(@RequestBody StockDto stockDto,HttpServletRequest request){
        Integer getrole = WareHouseController.getrole(request);
        stockService.addStock(stockDto,getrole);
        return Result.success();
    }

    //增加库存，在已创建库存里面添加商品数量
    @PutMapping("/addnum")
    public Result addNum(@RequestParam Integer id,Integer num,HttpServletRequest request){
        Integer userId =(Integer) request.getAttribute("id");
        stockService.addNum(id,num,userId);


        return Result.success();
    }

    //修改库存里的数据
    @PutMapping("/updatestock")
    public Result updateStock(@RequestBody StockDto stockDto,HttpServletRequest request){
        //非管理员不可以修改
        Integer getrole = WareHouseController.getrole(request);

        stockService.updateStock(stockDto,getrole);



        return Result.success();
    }


    //通过多表查询查出库存名和商品名和库存，还有安全值
    @GetMapping("/selectstock")
    public Result selectStock(@RequestParam(defaultValue =  "1") Integer page,@RequestParam(defaultValue = "5") Integer pagesize){
        RoleVo roleVo = stockService.selectStock(page, pagesize);
        return Result.success(roleVo);
    }




}
