package com.chat.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//返回给前端的统一结果
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;//返回状态码。
    private String msg;//给前端返回一个提示信息
    private Object date;//返回要给前端的一个json.
    //做增删改成功的结果
    //无参的构造方法
    //这些方法都为静态1类
    public static Result success(){
        return new Result(1,"操作成功",null);
    }
    //返回查询的结果，一个有参的构造方法
    public static Result success(Object date){
        return new Result(1,"操作成功",date);
    }
    //返回一个操作失败的结果,要把操作失败的提示给统一异常处理把信息返回
    public static Result error(String msg){
        return new Result(0,msg,null);
    }

}
