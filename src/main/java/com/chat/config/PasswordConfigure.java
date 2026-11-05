package com.chat.config;
/*
*
* 把加密交给spring容器管理
* */



import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // 解除注释，启用配置类
public class PasswordConfigure {


    @Bean
   // @ConfigurationProperties(prefix = "sour")
    public PasswordEncoder passwordencoder(){





         return new  BCryptPasswordEncoder();
    }
}





