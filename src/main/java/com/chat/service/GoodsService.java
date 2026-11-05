package com.chat.service;

import com.chat.pojo.dto.GoodsDto;
import com.chat.pojo.dto.PageDto;
import com.chat.pojo.po.GoodsPo;
import com.chat.pojo.vo.RoleVo;

import java.time.LocalDateTime;
import java.util.List;

public interface GoodsService {

    //这个是仓库的操作哦、

    //添加商品的信息,接收到前端的信息
    public void addGoods(GoodsDto goodsDto,Integer role);

    //分页查询商品
    public RoleVo select(PageDto pageDto,Integer role);

    //修改商品
    public void updateGoods(GoodsDto goodsDto,Integer role);

    //逻辑删除商品
    public void deleteGoods(Integer goodsId,Integer role);

    //模糊查询
    public List<GoodsPo> selectLike(String goodsName, LocalDateTime createTime);

}
