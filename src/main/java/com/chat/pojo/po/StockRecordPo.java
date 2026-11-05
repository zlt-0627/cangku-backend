package com.chat.pojo.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockRecordPo {
    /**
     * 记录主键
     */
    private Integer recordId;

    /**
     * 仓库id
     */
    private Integer warehouseId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 操作类型 1入库 2出库
     */
    private Integer operateType;

    /**
     * 操作数量
     */
    private Integer num;

    /**
     * 操作人id
     */
    private Integer operatorId;

    /**
     * 备注
     */
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
