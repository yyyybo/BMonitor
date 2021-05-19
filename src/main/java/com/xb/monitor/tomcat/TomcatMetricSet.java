package com.xb.monitor.tomcat;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

import java.util.Map;

/**
 * tomcat 指标集合
 *
 * @author yibo
 * @date 2021-05-06
 */
public class TomcatMetricSet implements MetricSet {

    @Override
    public Map<String, Metric> getMetrics() {
        return ServletContainerStatistics.getMetrics(ServletContainerStatistics.buildTomcatInformationsList());
    }
}
