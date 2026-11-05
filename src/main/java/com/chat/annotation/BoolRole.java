package com.chat.annotation;


import java.lang.annotation.*;

//自定义一个注解，就是作用给aop的，等会给他放在serrvice层里面要用的aop技术上的

@Target(ElementType.METHOD)//给他一个范围，这个只作用在方法里面
@Retention(RetentionPolicy.RUNTIME)//这是在运行的时候的，这里用aop是必须加上的，不然就是默认在class上的
@Documented
public @interface BoolRole {
}
