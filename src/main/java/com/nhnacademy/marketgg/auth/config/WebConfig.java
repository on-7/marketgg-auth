package com.nhnacademy.marketgg.auth.config;

import com.nhnacademy.marketgg.auth.interceptor.CookieInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Interceptor 를 등록하기 위한 설정 클래스
 *
 * @author 윤동열
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CookieInterceptor())
                .excludePathPatterns("/login");
    }

}
