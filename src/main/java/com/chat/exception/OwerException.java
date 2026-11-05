package com.chat.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
@Data

@NoArgsConstructor
public class OwerException extends RuntimeException{
    private Integer code;//状态码
    private String msg;//错误信息
//这个业务异常就只要抛出错误信息。
   public OwerException(String msg){
        super(msg);
        this.msg=msg;
    }
    public OwerException(Integer code,String msg){
       super(msg);
       this.msg=msg;
       this.code=code;


    }










}
