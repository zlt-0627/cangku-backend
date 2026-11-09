package com.chat.service.impl;


import com.chat.mapper.StockRecordMapper;
import com.chat.pojo.po.StockRecordPo;
import com.chat.pojo.vo.RoleVo;
import com.chat.service.StockRecordService;
import com.chat.util.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StockRecordServiceImpl implements StockRecordService {

    @Autowired
    private StockRecordMapper stockRecordMapper;

    //旁路缓存工具
    @Autowired
    private CacheHelper cache;

    @Override
    public RoleVo selectRecord(Integer page, Integer pageSize, Integer role) {

        if(role==1){
            throw new RuntimeException("权限不足！");
        }
        //先查缓存：角色要放进 key 里，不同角色能看到的数据不一样，不能共用一份
        String cacheKey = "cache:stockrecord:" + role + ":" + page + ":" + pageSize;
        RoleVo cached = cache.get(cacheKey, RoleVo.class);
        if (cached != null) {
            return cached;
        }
        Integer start=(page-1)*pageSize;
        List<StockRecordPo> stockRecordPos = stockRecordMapper.selectRecord(start, pageSize);
        Integer count = stockRecordMapper.count();

        RoleVo roleVo = new RoleVo(count, stockRecordPos);
        //查完写回
        cache.put(cacheKey, roleVo);
        return roleVo;
    }
}
