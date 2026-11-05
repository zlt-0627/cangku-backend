package com.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chat.exception.OwerException;
import com.chat.mapper.GoodsMapper;
import com.chat.pojo.dto.GoodsDto;
import com.chat.pojo.dto.PageDto;
import com.chat.pojo.po.GoodsPo;
import com.chat.pojo.vo.RoleVo;
import com.chat.service.GoodsService;
import com.chat.until.CacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


//新增商品
@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    //旁路缓存工具
    @Autowired
    private CacheHelper cache;

    @Override
    @Transactional
    public void addGoods(GoodsDto goodsDto,Integer role) {
        //这里校验身份，要可以增加商品要先看有没有权限
        if(role==1){

            throw new RuntimeException("权限不足，不可以操作！");
        }

        //这样也可以封装
//        BeanUtil.copyProperties(goodsDto, GoodsPo.class);

        //为了安全，不建议使用dto直接就去连接数据库
        GoodsPo goodsPo=new GoodsPo();
        //在这里它的id是自动加的，所以插入的时候不可以直接把id写入，会报错哦
//        goodsPo.setGoodsId(goodsDto.getGoodsId());
        goodsPo.setGoodsName(goodsDto.getGoodsName());
        goodsPo.setGoodsCode(goodsDto.getGoodsCode());
        goodsPo.setUnit(goodsDto.getUnit());
        goodsPo.setRemark(goodsDto.getRemark());
        goodsPo.setCreateTime(LocalDateTime.now());
        goodsPo.setUpdateTime(LocalDateTime.now());
        // 查重：同一个商品编码只允许存在一次
        // 注意这里的参数是 goodsDto.getGoodsCode()。
        // 以前写的是 getbycode(code)，而 code 是缓存里取出来的值（永远是 null），
        // 等于每次都在拿 null 查库，查重从来没生效过 —— 已修。
        String codename = goodsMapper.getbycode(goodsDto.getGoodsCode());
        if (codename != null) {
            throw new RuntimeException("商品已经存在！");
        }
        // 数据库里没有，插入
        Integer count = goodsMapper.insertcode(goodsPo);
        if (count != 1) {
            throw new RuntimeException("商品添加失败，请重试");
        }
        //新增成功：把商品列表的分页缓存全部作废，下次查询会重新查库并写回
        cache.evict("cache:goods:");
    }

    //分页查询商品
    public RoleVo select(PageDto pageDto,Integer role){
        //在这里先把功能完善
        if(role==1){
            throw new RuntimeException("权限不足！");

        }
    //解析到page和pagesize
        //先看缓存：同一个角色、同一页、同样的每页条数，直接返回
        String cacheKey = "cache:goods:" + role + ":" + pageDto.getPage() + ":" + pageDto.getPageSize();
        RoleVo cached = cache.get(cacheKey, RoleVo.class);
        if (cached != null) {
            log.info("商品分页命中缓存 {}", cacheKey);
            return cached;
        }

        //这个起始位置要计算
        Integer start=(pageDto.getPage()-1)* pageDto.getPageSize();
        //把这两个传给mapper
        List<GoodsDto> list=goodsMapper.selectGoods(start,pageDto.getPageSize());
        Integer count=goodsMapper.selectCount();
        RoleVo roleVo=new RoleVo();
        roleVo.setRows(list);
        roleVo.setTotals(count);
        //查完写回缓存，下次同一页就不用再查库了
        cache.put(cacheKey, roleVo);
        return  roleVo;
    }

    //在这里要修改商品的信息，所以这里要使用乐观锁，避免高并发
    @Override
    @Transactional
    public void updateGoods(GoodsDto goodsDto, Integer role) {
        //这里直接把数据传进去，没有要保护的数据
        if(role==1){
            throw new OwerException("权限不足!");
        }
        //判断商品是否存在
        GoodsDto goodsDto1 = goodsMapper.selectById(goodsDto);
        if(goodsDto1==null){
            throw new OwerException("商品不存在！");
        }
        Integer version = goodsDto1.getVersion();
        goodsDto.setVersion(version);
//        try {
//            Thread.sleep(10000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        int i = goodsMapper.updateGoods(goodsDto);
        if(i!=1){
            throw new RuntimeException("已被修改！");
        }
        //改成功了，旧数据不能再用
        cache.evict("cache:goods:");
    }
    //逻辑删除商品
    @Override
    public void deleteGoods(Integer goodsId, Integer role) {
        //判断权限是否合理
        if(role==1){
            throw new OwerException("权限不足！");
        }
        //先查询是否存在该用户在删除
        GoodsDto goodsDto = goodsMapper.selectId(goodsId);
        if(goodsDto==null){
            throw new OwerException("商品不存在！");
        }
//        try{
//            Thread.sleep(10000);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        Integer version = goodsDto.getVersion();
        //存在的话，就是逻辑删除
        Integer i = goodsMapper.deleteGoods(goodsId, version);
        //这里使用乐观锁，有可能在高并发的情况下，会有用户出现异常。
        if(i!=1){
            throw new RuntimeException("该商品被删除！");

        }
        //删掉了，缓存里可能还留着它
        cache.evict("cache:goods:");
    }


    //模糊查询
    @Override
    public List<GoodsPo> selectLike(String goodsName,LocalDateTime createTime) {

        List<GoodsPo> goodsPos = goodsMapper.selectLike(goodsName, createTime);

        return   goodsPos;


    }








}
