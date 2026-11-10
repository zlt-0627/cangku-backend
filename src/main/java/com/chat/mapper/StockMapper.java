package com.chat.mapper;


import com.chat.pojo.dto.StockDto;
import com.chat.pojo.po.StockPo;
import com.chat.pojo.vo.StockRecordVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StockMapper {

    //新增仓库
    @Insert("insert into stock(warehouse_id, goods_id, stock_num, safe_num, update_time)  values (#{warehouseId},#{goodsId},#{stockNum},#{safeNum},#{updateTime}) ")
    public Integer addStock(StockPo stockPo);

    //根据一个仓库放一件相同的商品原则，来查询
    @Select("select * from stock where warehouse_id=#{warehouseId} and goods_id=#{goodsId} for update")
    public StockDto selectById(StockPo stockPo);


    //根据库存id来判断是否存在
    @Select("select * from stock where stock_id=#{id}")
    public StockPo selectId(Integer id);

    //更新stocknum
    @Update("update stock set stock_num=stock_num+#{num},version=version+1 where stock_id=#{id} and version=#{version}")
    public Integer addNum(@Param("id") Integer id, @Param("num") Integer num, @Param("version") Integer version);


    //更新库存表的数据

    public Integer updateStock(StockPo stockPo);

    //分页查询总数（注意是 stock 不是 goods——之前写错表，导致总数算成商品数）
    @Select("select count(*) from stock")
    public Integer selectCount();

    @Select("select stock_id, warehouse_name, goods_name, stock_num, safe_num from stock,warehouse,goods where goods.goods_id=stock.goods_id and stock.warehouse_id=warehouse.warehouse_id limit #{start},#{pageSize}")
    public List<StockRecordVo> selectPage(Integer start, Integer pageSize);


}
