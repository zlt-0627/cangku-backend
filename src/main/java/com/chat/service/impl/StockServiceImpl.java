package com.chat.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.chat.exception.BizException;
import com.chat.mapper.GoodsMapper;
import com.chat.mapper.StockMapper;
import com.chat.mapper.StockRecordMapper;
import com.chat.mapper.WareHouseMapper;
import com.chat.pojo.dto.GoodsDto;
import com.chat.pojo.dto.StockDto;
import com.chat.pojo.po.StockPo;
import com.chat.pojo.po.StockRecordPo;
import com.chat.pojo.po.WarePo;
import com.chat.pojo.vo.RoleVo;
import com.chat.pojo.vo.StockRecordVo;
import com.chat.service.StockService;
import com.chat.util.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private WareHouseMapper wareHouseMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    //旁路缓存工具
    @Autowired
    private CacheHelper cache;

    //新建库存
    @Override
    @Transactional
    public void addStock(StockDto stockDto, Integer role) {
        if(role==1){
            throw new RuntimeException("权限不足！");
        }
        StockPo stockPo = BeanUtil.copyProperties(stockDto, StockPo.class);
        LocalDateTime updateTime=LocalDateTime.now();
        stockPo.setUpdateTime(updateTime);
        //判断在商品和仓库是否存在
        Integer goodsId = stockPo.getGoodsId();

        GoodsDto goodsDto = goodsMapper.selectId(goodsId);
        WarePo warePo = wareHouseMapper.selectId(stockDto.getWarehouseId());
        if(goodsDto==null){
            throw new RuntimeException("该商品不存在！");
        }
        if(stockDto.getSafeNum()<stockDto.getStockNum()){
            throw new RuntimeException("超出安全值！");
        }
        if(warePo==null || warePo.getStatus()==0){
            throw new RuntimeException("该仓库不存在！");
        }
        StockDto stockDto1 = stockMapper.selectById(stockPo);
        if(stockDto1!=null){
            throw new RuntimeException("该库存已经存在！不可以在建立");
        }
        Integer i = stockMapper.addStock(stockPo);
        if(i==0){
            throw new RuntimeException("该库存已存在！");
        }
        //新增了库存，列表缓存作废
        cache.evict("cache:stock:");
    }

    //增加商品数量
    @Override
    @Transactional
    public void addNum(Integer id, Integer num,Integer userId) {

        //查看是否存在该库存
        StockPo stockPo = stockMapper.selectId(id);
        if(stockPo==null){
            throw new BizException("该库存不存在!");
        }
        Integer version = stockPo.getVersion();
        if(stockPo.getStockNum()+num>stockPo.getSafeNum()){
            throw new BizException("超出安全值，请到其他仓库存！");
        }

        if(stockPo.getStockNum()+num<0){
            throw new RuntimeException("该仓库库存不足！");
        }
        //存在，更新stocknum
        Integer i = stockMapper.addNum(id, num, version);
        if(i==0){
            throw new RuntimeException("请稍后重式！");
        }
        //要关联到流水表，在这里
        StockRecordPo stockRecordPo=new StockRecordPo();
        stockRecordPo.setNum(num);
        stockRecordPo.setOperatorId(userId);
        stockRecordPo.setGoodsId(stockPo.getGoodsId());
        stockRecordPo.setWarehouseId(stockPo.getWarehouseId());
        if(num<0){
            stockRecordPo.setOperateType(2);

        }else {
            stockRecordPo.setOperateType(1);
        }
        stockRecordPo.setCreateTime(LocalDateTime.now());
        stockRecordMapper.insertRecord(stockRecordPo);
        //库存数量和流水都变了，两边的缓存一起清
        cache.evict("cache:stock:");
        cache.evict("cache:stockrecord:");
    }

    //更新库存的数据
    @Override
    public void updateStock(StockDto stockDto, Integer role) {
        if(role==1){
            throw new RuntimeException("权限不足！");
        }
        //在更新之前要判断数据存不存在
        StockPo stockPo = BeanUtil.copyProperties(stockDto, StockPo.class);
        StockPo stockPo1 = stockMapper.selectId(stockPo.getStockId());
        if(stockPo1==null){
            throw new RuntimeException("修改失败，不存在该库存！");
        }
        //得到版本号
        Integer version = stockPo1.getVersion();
        stockPo.setUpdateTime(LocalDateTime.now());
        stockPo.setVersion(version);
        Integer i = stockMapper.updateStock(stockPo);
        if(i==0){
            throw new RuntimeException("该库存已被修改，请在次修改！");
        }
        //改完了，缓存里的旧数据删掉
        cache.evict("cache:stock:");
    }

    @Override
    public RoleVo selectStock(Integer page, Integer pageSize) {
        //先查缓存
        String cacheKey = "cache:stock:" + page + ":" + pageSize;
        RoleVo cached = cache.get(cacheKey, RoleVo.class);
        if (cached != null) {
            return cached;
        }
        Integer start=(page-1)*pageSize;
        List<StockRecordVo> stockPos = stockMapper.selectPage(start, pageSize);
        Integer count = stockMapper.selectCount();
        RoleVo roleVo = new RoleVo(count, stockPos);
        //查完写回
        cache.put(cacheKey, roleVo);
        return roleVo;
    }


}

























