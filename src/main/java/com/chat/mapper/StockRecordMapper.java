package com.chat.mapper;


import com.chat.pojo.po.StockRecordPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StockRecordMapper {

    @Insert("insert into stock_record(warehouse_id, goods_id, operate_type, num, operator_id,create_time) values (#{warehouseId},#{goodsId},#{operateType},#{num},#{operatorId},#{createTime})")
    public void insertRecord(StockRecordPo stockRecordPo);

    //分页查询
    @Select("select * from stock_record limit #{start},#{pageSize}")
    public List<StockRecordPo> selectRecord(Integer start, Integer pageSize);
    @Select("select count(*) from stock_record ")
    public Integer count();


}
