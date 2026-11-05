package com.chat.mapper;


import com.chat.pojo.dto.GoodsDto;
import com.chat.pojo.po.GoodsPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GoodsMapper {

    @Select("select goods_code from goods where goods_code=#{goodCode} for update")
    //查询操作，通过商品的名字
    public String getbycode(String goodsCode);

    //通过商品的id判断商品是否存在
    @Select("select * from goods where goods_id=#{goodsId} and is_delete=0")
    public GoodsDto selectById(GoodsDto goodsDto);

    //插入商品
    @Insert("insert into goods(goods_name, goods_code, unit, remark, create_time, update_time) values(#{goodsName},#{goodsCode},#{unit},#{remark},#{createTime},#{updateTime})")
    public Integer insertcode(GoodsPo goodsPo);


    //分页查询商品,这个要放在一个集合里面
    @Select("select goods_id, goods_name, goods_code, unit, remark, update_time from goods where is_delete=0 limit #{start},#{pageSize}")
    public List<GoodsDto> selectGoods(Integer start, Integer pageSize);

    //分页查询总数
    @Select("select count(*) from goods where is_delete=0")
    public Integer selectCount();

    //修改商品信息
    public Integer updateGoods(GoodsDto goodsDto);

    //查询用户是否存在，不用使用is_delete来筛选。
    @Select("select * from goods where goods_id=#{goodsId} and is_delete=0")
    public GoodsDto selectId(Integer goodsId);

    //逻辑删除,在这里避免会有连续删除的情况，还是使用了乐观锁，就算不使用也不会问题也不大。
    @Update("update goods set is_delete=1,version=version+1 where goods_id=#{goodsId} and version=#{version}")
    public Integer deleteGoods(Integer goodsId,Integer version);


    //模糊查询
    public List<GoodsPo> selectLike(String goodsName, LocalDateTime createTime);

}
