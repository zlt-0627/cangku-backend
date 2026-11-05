package com.chat.service;

import com.chat.pojo.dto.StockDto;
import com.chat.pojo.vo.RoleVo;

import java.util.List;

public interface StockService {

    //新建库存
    public void addStock(StockDto stockDto,Integer role);

    //增加商品数量
    public void addNum(Integer id,Integer num,Integer userid);

    //更改数据
    public void updateStock(StockDto stockDto,Integer role);


    public RoleVo selectStock(Integer page, Integer pageSize);
}
