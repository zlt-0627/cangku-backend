package com.chat.pojo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
//这里的插入和更新都是用这个实体类封装
public class GoodsDto {
    private Integer goodsId;//id
    private String goodsName;//商品名字
    private String goodsCode;//商品的编码
    private String unit;//商品的单位
    private String remark;//商品的备注
    //在这里面还要得到商品的数量这个是在前端传的，但是要放在库存表里面进行操作。
    // 这里不用把库存放在这里，要先把仓库和商品先创建出来
    private Integer is_delete;
    private Integer version;


    private LocalDateTime updateTime;


}
