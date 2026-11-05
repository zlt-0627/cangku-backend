package com.chat.pojo.dto;


import lombok.Data;

//这个是接收前端的页数每页大小的
@Data
public class PageDto {
    private Integer page;
    private Integer pageSize;


}
