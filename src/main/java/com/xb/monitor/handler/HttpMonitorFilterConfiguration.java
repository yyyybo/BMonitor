package com.xb.monitor.handler;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 加载监控拦截器
 *
 * @author yibo
 * @date 2021-05-08
 */
@Configuration
public class HttpMonitorFilterConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 监控
        registry.addInterceptor(new HttpMonitorInterceptor())
                .addPathPatterns("/**")
                .order(Ordered.HIGHEST_PRECEDENCE);

    }


}
