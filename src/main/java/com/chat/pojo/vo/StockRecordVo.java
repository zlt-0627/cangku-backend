package com.chat.pojo.vo;


import lombok.Data;

@Data
public class StockRecordVo {

    private Integer stockId;
    private String warehouseName;
    private String goodsName;
   private Integer stockNum;
    private Integer safeNum;
}
