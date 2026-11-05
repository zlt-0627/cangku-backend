package com.chat.pojo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockDto {
    /**
     * 库存主键
     */
    private Integer stockId;

    /**
     * 仓库id
     */
    private Integer warehouseId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 当前库存数量
     */
    private Integer stockNum;

    /**
     * 安全库存阈值
     */
    private Integer safeNum;

    /**
     * 更新时间
     */
}
