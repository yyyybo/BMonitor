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

    /**
     * 创建可监控的线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   保持活跃时间
     * @param unit            keepAliveTime的单位
     * @param workQueue       队列
     * @param threadFactory   线程工厂
     * @param handler         拒绝策略
     * @param metricName      监控名称
     * @return 创建好的线程池
     */
    public static ExecutorService createInstrumentedExecutorService(int corePoolSize, int maximumPoolSize,
                                                                    long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                                                    RejectedExecutionHandler handler, String metricName) {

        MetricRegistry metricRegistry = MetricsHolder.getMetricRegistry();
        InstrumentedThreadFactory instrumentedThreadFactory =
                new InstrumentedThreadFactory(threadFactory, metricRegistry, metricName);

        InstrumentedAbortPolicy instrumentedAbortPolicy =
                new InstrumentedAbortPolicy(handler, metricRegistry, metricName);

        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                        instrumentedThreadFactory, instrumentedAbortPolicy);

        return new InstrumentedExecutorService(threadPoolExecutor, metricRegistry, metricName);
    }

}
