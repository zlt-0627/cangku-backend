package com.chat.mapper;

import com.chat.pojo.dto.WareDto;
import com.chat.pojo.po.WarePo;
import com.chat.pojo.vo.RoleVo;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface WareHouseMapper {

    //通过仓库的名字来查询，在新增的操作里面是不用写仓库id的
    @Select("select * from warehouse where  warehouse_name=#{warehouseName} for update")
    public WareDto seleteByName(WareDto wareDto);

    //插入一条仓库信息
    @Insert("insert into warehouse(warehouse_name, address, create_time, update_time) VALUES (#{warehouseName},#{address},#{createTime},#{updateTime})")
    public Integer addWare(WareDto wareDto);

   @Select("select * from warehouse where status=1 limit #{start},#{pageSize} ")
    public List<WarePo> selectByPage(@Param("start") Integer start,@Param("pageSize") Integer pageSize);

   @Select("select count(*) from warehouse where status=1")
    public Integer count();

   //修改数据
    @Select("select * from warehouse where warehouse_id=#{warehouseId} and status=1")
    public WarePo selectById(WarePo warePo);

    public Integer updateWare(WarePo warePo);

    //逻辑删除数据
    @Select("select * from warehouse where warehouse_id=#{id}")
    public WarePo selectId(Integer id);

    //做逻辑删除操作
    @Update("update warehouse set status=0,version=version+1 where warehouse_id=#{id} and version=#{version}")
    public Integer deleteWare(Integer id,Integer version);

}
