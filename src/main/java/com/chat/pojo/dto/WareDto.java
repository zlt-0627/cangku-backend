package com.chat.pojo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WareDto {
    /**
     * 仓库主键id
     */
    private Integer warehouseId;
    /**
     * 仓库名称
     */
    private String warehouseName;
    /**
     * 仓库地址
     */
    private String address;
    /**
     * 状态 0停用 1启用
     */
    private Integer status;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
