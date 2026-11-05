package com.chat.controller;

import com.chat.pojo.Result;
import com.chat.pojo.dto.GoodsDto;
import com.chat.pojo.dto.PageDto;
import com.chat.pojo.po.GoodsPo;
import com.chat.pojo.vo.RoleVo;
import com.chat.service.GoodsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    //这里的就单纯的添加商品，不用吧库存放在里面
//添加商品的信息
    @PostMapping("/addgoods")
    //在这里面传的数据不用开始事件和更新时间，自动更新
    public Result addnum(@RequestBody GoodsDto goodsDto, HttpServletRequest request){
        //在这里要可以添加商品必须保证用户有这个权限
        //得到里面的role
        Integer role=(Integer) request.getAttribute("role");//注意这里拿到role是校验身份的，还要通过id这个唯一标识来确认身份
        //把数据下沉到service操作
        goodsService.addGoods(goodsDto,role);
        return Result.success();
    }


    //分页查询商品数据
    //注意分页查询不要对这个封装，直接传就可以
    @GetMapping("/selectgoods")
    //这里要拿到权限，还有分页的页数
    public Result selectGoods(@RequestParam(defaultValue =  "1") Integer page,@RequestParam(defaultValue = "5") Integer pagesize,HttpServletRequest request){
        //在这里面我后面要使用aop把权限控制所以把这里分装。
        PageDto pageDto=new PageDto();
        pageDto.setPage(page);
        pageDto.setPageSize(pagesize);
        //还要解析这个role,要使用强转
        Integer role=(Integer) request.getAttribute("role");
        RoleVo roleVo=goodsService.select(pageDto,role);
        return Result.success(roleVo);
    }

    //修改商品信息
    @PutMapping("/updategoods")
    public Result updateGoods(@RequestBody GoodsDto goodsDto, HttpServletRequest request){
        //把role找出来
        Integer role = (Integer) request.getAttribute("role");

        goodsService.updateGoods(goodsDto,role);

        return Result.success();
    }

    //删除商品信息（逻辑删除）
    @DeleteMapping("/deletedoods")
    //通过id删除，只要把商品id传入
    public Result deleteGoods(@RequestParam Integer goodsId,HttpServletRequest request){
        Integer role=(Integer) request.getAttribute("role");
        goodsService.deleteGoods(goodsId,role);
        return Result.success();
    }


    //模糊查询
    @GetMapping("/likeselect")
    public Result selectLike(@RequestParam (required = false) String goodsName,@RequestParam (required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createTime){
        List<GoodsPo> goodsPos = goodsService.selectLike(goodsName, createTime);
        return Result.success(goodsPos);
    }
}
