package com.xb.monitor;

import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.InstrumentedThreadFactory;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.concurrent.*;

/**
 * 监控客户端代码入口
 *
 * @author yibo
 * @date 2021-05-06
 */
public final class BMonitor {

    /**
     * 只对接口的QPS进行监控
     *
     * @param meterName 监控名
     */
    public static void meter(String meterName) {
        MetricsHolder.meter(meterName).mark();
    }

    /**
     * 监控QPS和响应时间(RT)
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
