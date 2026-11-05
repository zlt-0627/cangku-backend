package com.chat.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//在使用aop的时候，要加上依赖配置
@Aspect
@Component
@Order(1)
public class BoolRoleAspect {

    //在这里要使用到切点表达式，把合适的取出来
    @Pointcut("@annotation(com.chat.annotation.BoolRole)")
    public void boolRoleCut(){}


    @Before("boolRoleCut()")
    //这个joinpoint是用来获取参数的
    public void checkRole(JoinPoint joinPoint){
        Object[] args=joinPoint.getArgs();//得到参数
        //定义一个role接收
        Integer role=null;
       //遍历，把所有在args里面的参数都搞出来
        for(Object arg : args){
            if(arg instanceof  Integer){
                role=(Integer) arg;
                break;
            }

        }
        //
        if(role==null||role!=3){
            throw new RuntimeException("权限不足，不可以操作！");

        }





    }




}
