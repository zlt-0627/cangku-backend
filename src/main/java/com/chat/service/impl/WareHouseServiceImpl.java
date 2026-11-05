package com.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chat.exception.OwerException;
import com.chat.mapper.WareHouseMapper;
import com.chat.pojo.dto.WareDto;
import com.chat.pojo.po.WarePo;
import com.chat.pojo.vo.RoleVo;
import com.chat.service.WareHuoseService;
import com.chat.until.CacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


//仓库的业务操作
@Service
@Slf4j
public class WareHouseServiceImpl implements WareHuoseService {
    @Autowired
    private WareHouseMapper wareHouseMapper;

    //旁路缓存工具
    @Autowired
    private CacheHelper cache;


    //增加仓库
    @Transactional
    @Override
    public void addWare(WareDto wareDto, Integer role) {
        //身份校验
        if(role==1){
            throw new OwerException("权限不足!");
        }
        //满足权限的时候，看看该仓库是否存在
        //查询
        WareDto wareDto1 = wareHouseMapper.seleteByName(wareDto);
        if(wareDto1!=null){
            //数据库查到了这条数据，抛出异常
            throw new RuntimeException("这个仓库已经存在！");

        }
        //这个时间就是当前时间
        LocalDateTime createTime=LocalDateTime.now();
        LocalDateTime updateTime=LocalDateTime.now();
        wareDto.setCreateTime(createTime);
        wareDto.setUpdateTime(updateTime);
//        try{
//            Thread.sleep(10000);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        //没有查到,为了防止没有查到高并发的情况下，这里写一个间隙锁
        Integer i = wareHouseMapper.addWare(wareDto);
        if(i!=1){
            throw new OwerException("改商品已被添加！");

        }
        //新增成功，仓库列表缓存作废
        cache.evict("cache:ware:");
    }

    //查询仓库
    @Override
    public RoleVo selectWare(Integer page, Integer pageSize) {
        //先查缓存
        String cacheKey = "cache:ware:" + page + ":" + pageSize;
        RoleVo cached = cache.get(cacheKey, RoleVo.class);
        if (cached != null) {
            return cached;
        }
        //开始页数
        Integer start= (page-1)*pageSize;
        List<WarePo> list = wareHouseMapper.selectByPage(start, pageSize);
        Integer count = wareHouseMapper.count();
        RoleVo roleVo = new RoleVo(count, list);
        //查完写回
        cache.put(cacheKey, roleVo);
        return roleVo;
    }



    //修改仓库,这里要加上乐观锁的概念
    @Override
    public void updateWare(WareDto wareDto, Integer role) {

        //时间要更新
        LocalDateTime updateTime=LocalDateTime.now();
        //这里会自动封装
        WarePo warePo = BeanUtil.copyProperties(wareDto, WarePo.class);
        warePo.setUpdateTime(updateTime);
        System.out.println(warePo);
        //在修改数据的时候，要确认是否存在该数据
        WarePo warePo1 = wareHouseMapper.selectById(warePo);
        if(warePo1==null){
            throw new RuntimeException("该数据不存在！");

        }
        //做一个乐观锁
       warePo.setVersion(warePo1.getVersion());
        //把数据改了
        Integer i = wareHouseMapper.updateWare(warePo);
        if(i!=1){
            throw new OwerException("该数据已被更改！");
        }
        //改完了，缓存里的旧数据删掉
        cache.evict("cache:ware:");
    }



    //逻辑删除仓库
    @Override
    public void deleteWare(Integer id, Integer role) {
        if(role==1){
            throw new OwerException("没有权限！");
        }
        //查询这个数据是否存在。
        WarePo warePo = wareHouseMapper.selectId(id);
        //不存在，删除失败
        if(warePo==null || warePo.getStatus()==0){
            throw new RuntimeException("该数据已被删除！");
        }
        //把status改为0.

        Integer i = wareHouseMapper.deleteWare(id, warePo.getVersion());
        if(i==0){
            throw new OwerException("该数据被删除了！");
        }
        //逻辑删除了，缓存里可能还留着它
        cache.evict("cache:ware:");
    }





}
