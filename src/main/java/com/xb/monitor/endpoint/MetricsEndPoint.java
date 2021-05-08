package com.xb.monitor.endpoint;

import com.codahale.metrics.MetricRegistry;
import com.xb.monitor.MetricsHolder;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

/**
 * 监控指标
 *
 * @author yibo
 * @date 2021-05-06
 */
@Endpoint(id = "bmonitor")
public class MetricsEndPoint {

    /**
     * 服务暴露的监控指标
     *
     * @return MetricRegistry注册的所有监控指标
     */
    @ReadOperation
    public MetricRegistry getAllMetrics() {
        return MetricsHolder.getMetricRegistry();
    }
}

