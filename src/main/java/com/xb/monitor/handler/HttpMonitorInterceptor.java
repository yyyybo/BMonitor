package com.xb.monitor.handler;


import com.codahale.metrics.Timer;
import com.google.common.primitives.Ints;
import com.xb.monitor.BMonitor;
import com.xb.monitor.util.Safes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * 监控Http的所有请求的timer
 *
 * @author yibo
 * @date 2021-05-06
 **/
public class HttpMonitorInterceptor implements HandlerInterceptor {

    private final NamedThreadLocal<Timer.Context> timerContextThreadLocal =
            new NamedThreadLocal<>("httpMonitorInterceptor");

    private static final Pattern SPACE_PATTERN_01 = Pattern.compile("//");
    private static final Pattern SPACE_PATTERN_02 = Pattern.compile("/");
    private static final Pattern SPACE_PATTERN_03 = Pattern.compile("\\{|\\}");

    private static final Comparator<String> STRING_LENGTH_REVERSE = (o1, o2) -> Ints.compare(o2.length(), o1.length());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof HandlerMethod) {
            String monitorName = getMonitorName(request, (HandlerMethod) handler);
            if (StringUtils.isNotEmpty(monitorName)) {
                Timer.Context context = BMonitor.timer(monitorName);
                timerContextThreadLocal.set(context);
            }
        }

        return true;
    }

    public static String getMonitorName(HttpServletRequest request, HandlerMethod handlerMethod) {

        String requestUri = Safes.of(request.getRequestURI());


        StringBuilder monitorNameBuilder = new StringBuilder();
        RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        Class<?> controllerClass = handlerMethod.getBeanType();
        RequestMapping requestMappingForController =
                controllerClass.getAnnotation(RequestMapping.class);
        if (requestMappingForController != null) {
            monitorNameBuilder.append(getMappingPath(requestMappingForController.value(), requestUri));
        }
        if (requestMapping != null) {
            monitorNameBuilder.append(getMappingPath(requestMapping.value(), requestUri));
        }
        if (monitorNameBuilder.length() == 0) {
            return StringUtils.EMPTY;
        }
        return SPACE_PATTERN_03.matcher(SPACE_PATTERN_02.matcher(SPACE_PATTERN_01.matcher(monitorNameBuilder.toString()).replaceAll("/")).replaceAll("_")).replaceAll(StringUtils.EMPTY);

    }

    /**
     * 获取controller里配置的路径
     *
     * @param value      RequestMapping配置的路径
     * @param requestUri 完整的请求路径
     * @return 匹配到的路径
     */
    private static String getMappingPath(String[] value, String requestUri) {
        if (value == null || value.length == 0) {
            return StringUtils.EMPTY;
        }
        if (value.length == 1) {
            return value[0];
        }
        Arrays.sort(value, STRING_LENGTH_REVERSE);
        for (String item : value) {
            if (requestUri.contains(item)) {
                return item;
            }
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        //结束计时并清理
        Timer.Context context = timerContextThreadLocal.get();
        if (context != null) {
            context.stop();
            timerContextThreadLocal.remove();
        }
    }

}
