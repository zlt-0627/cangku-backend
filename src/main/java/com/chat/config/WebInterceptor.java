package com.chat.config;

import com.chat.interceptor.Interceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//这里要加上才可以被spring识别。
@Configuration
public class WebInterceptor implements WebMvcConfigurer {

    // 拦截器现在是 Spring 的 Bean（里面要注入 Redis），不能再 new 出来
    private final Interceptor interceptor;

    public WebInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    //注册拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        //这里面的不是一个类面那个不是一个类名，是一个类对象
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/login");
    }

//开启跨域访问
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
}
