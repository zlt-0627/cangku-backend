package com.chat.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class WaerHouseDto {

//    warehouse_id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '仓库ID',
//            `warehouse_name` VARCHAR(50) NOT NULL COMMENT '仓库名称，如：原料仓',
//            `address` VARCHAR(200) COMMENT '仓库地址',
//            `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
//            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
//            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    private Integer wareHouseId;
    private String wareHouseName;
    private String adress;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime pdateTime;
}
