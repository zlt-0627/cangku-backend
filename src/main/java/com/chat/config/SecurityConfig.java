//package com.chat.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//    @Configuration
//    @EnableWebSecurity
//    public class SecurityConfig {
//
//        @Bean
//        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//            http
//                    .csrf(csrf -> csrf.disable()) //关闭csrf，post请求必须关
//                    .authorizeHttpRequests(auth -> auth
//                            .requestMatchers("/login").permitAll() //放行登录接口，不需要认证
//                            .anyRequest().authenticated() //其他接口需要认证
//                    );
//            return http.build();
//        }
//    }
//
//
