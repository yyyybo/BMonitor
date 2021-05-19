package com.xb.monitor;

import com.codahale.metrics.Timer;

import java.util.concurrent.TimeUnit;

/**
 * 监控客户端代码入口
 *
 * @author yibo
 * @date 2021-05-06
 */
public final class BMonitor {

    /**
     * 统计系统中某一个事件的速率。比如每秒请求数（TPS），每秒查询数（QPS）等等
     *
     * 这个指标能反应系统当前的处理能力，帮助我们判断资源是否已经不足。
     *
     * 对应指标
     * m1 ===> 1分钟速率
     * m5 ===> 5分钟速率
     *
     * @param meterName 监控名
     */
    public static void meter(String meterName) {
        MetricsHolder.meter(meterName).mark();
    }

    /**
     * 统计请求的速率和处理时间。
     * 对于接口中调用的延迟等信息的统计就比较方便了。如果发现一个方法的RPS（请求速率）很低，而且平均的处理时间很长，那么这个方法八成出问题了。
     *
     * 对应指标
     * m1 ===> 1分钟速率
     * m5 ===> 5分钟速率
     * p98 ===> 按照排序后，占比在98%大的那个方法处理时间
     * p95 ===> 按照排序后，占比在95%大的那个方法处理时间
     *
     * @param timerName 监控名
     */
    public static void timer(String timerName, long milliseconds) {
        MetricsHolder.timer(timerName).update(milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * context
     *
     * @param timerName 监控名
     * @return Timer上下文, 结束可执行 .stop();
     */
    public static Timer.Context timer(String timerName) {
        return MetricsHolder.timer(timerName).time();
    }

}
