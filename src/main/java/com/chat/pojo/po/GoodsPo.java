package com.chat.pojo.po;

import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;


@Data
public class GoodsPo {


//
//     `goods_id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
//            `goods_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
//            `goods_code` VARCHAR(50) UNIQUE COMMENT '商品编码',
//            `unit` VARCHAR(20) COMMENT '单位：个/箱/千克',
//            `remark` VARCHAR(255) COMMENT '备注',
//            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
//            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TI


    //接收数据库里面的值
    private Integer goodsId;//id
    private String goodsName;//商品名字
    private String goodsCode;//商品的编码
    private String unit;//商品的单位
    private String remark;//商品的备注
    private LocalDateTime createTime;
    private LocalDateTime updateTime;



}
