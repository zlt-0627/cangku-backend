package com.chat.service;

import com.chat.pojo.po.StockRecordPo;
import com.chat.pojo.vo.RoleVo;

public interface StockRecordService {

    //做分页查询
    public RoleVo selectRecord(Integer page, Integer pageSize, Integer role);


}
