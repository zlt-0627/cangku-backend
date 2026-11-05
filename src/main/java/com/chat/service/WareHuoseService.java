package com.chat.service;

import com.chat.pojo.dto.WareDto;
import com.chat.pojo.vo.RoleVo;



public interface WareHuoseService {

    //增加仓库
    public void addWare(WareDto wareDto,Integer role);


    //查看仓库
    public RoleVo selectWare(Integer page,Integer pageSize);


    //修改仓库信息
    public void updateWare(WareDto wareDtoe,Integer role);

    //删除数据
    public void deleteWare(Integer id, Integer role);
}
