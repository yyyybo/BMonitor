package com.xb.monitor.tomcat;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * tomcat 相关数据
 *
 * @author yibo
 * @date 2021-05-06
 */
public class ServletContainerStatistics {

    private static final Logger logger = LoggerFactory.getLogger(ServletContainerStatistics.class);

    /**
     * 是否使用tomcat
     */
    private static final boolean IS_TOMCAT_ENVIRONMENT = System.getProperty("catalina.home") != null;

    /**
     * 线程池数据对象
     */
    private static final List<ObjectName> THREAD_POOLS = Lists.newArrayList();

    /**
     * tomcat 数据 bean
     */
    private static final MBeanServer SERVER = ManagementFactory.getPlatformMBeanServer();

    /**
     * 线程池数据对象
     */
    private final ObjectName threadPool;

    static Set<ObjectName> getTomcatThreadPools() throws MalformedObjectNameException {

        return SERVER.queryNames(new ObjectName("Catalina:type=ThreadPool,*"), null);
    }
    static Set<ObjectName> getInlineTomcatThreadPools() throws MalformedObjectNameException {

        return SERVER.queryNames(new ObjectName("Tomcat:type=ThreadPool,*"), null);
    }

    private Object getAttribute(ObjectName name, String attribute) {

        try {
            return SERVER.getAttribute(name, attribute);
        } catch (MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException e) {
            if (logger.isDebugEnabled()) {
                logger.error("获取tomcat数据失败. name={}, attribute={}", name, attribute, e);
            }
            return null;
        }
    }

    static MBeanInfo getBeanInfo(ObjectName name) throws JMException {

        return SERVER.getMBeanInfo(name);
    }

    private ServletContainerStatistics(ObjectName threadPool) {

        this.threadPool = threadPool;
    }

    /**
     * 获取 tomcat 的数据, 每次重新获取一下
     *
     * @return tomcat 的数据
     */
    public static List<ServletContainerStatistics> buildTomcatInformationsList() {

        if (!IS_TOMCAT_ENVIRONMENT) {
            return Collections.emptyList();
        } else {
            return buildServletContainerInformationList();
        }
    }
    /**
     * 获取 tomcat 的数据, 每次重新获取一下
     *
     * @return tomcat 的数据
     */
    public static Map<String, Metric> getMetrics(List<ServletContainerStatistics> servletContainerStatisticsList) {

        Map<String, Metric> metricMap = Maps.newHashMapWithExpectedSize(servletContainerStatisticsList.size());

        servletContainerStatisticsList.forEach(servletContainerStatistics -> {
            String name = servletContainerStatistics.name();
            metricMap.put("max_threads." + name, (Gauge<Integer>) servletContainerStatistics::maxThreads);
            metricMap.put("current_thread_count." + name, (Gauge<Integer>) servletContainerStatistics::currentThreadCount);
            metricMap.put("current_threads_Busy." + name, (Gauge<Integer>) servletContainerStatistics::currentThreadsBusy);
        });
        return metricMap;
    }

    private static List<ServletContainerStatistics> buildServletContainerInformationList() {
        try {
            synchronized (THREAD_POOLS) {
                if (THREAD_POOLS.isEmpty()) {
                    initBeans();
                }
            }

            List<ServletContainerStatistics> servletContainerStatisticsList = Lists.newArrayListWithExpectedSize(THREAD_POOLS.size());

            for (ObjectName threadPool : THREAD_POOLS) {
                servletContainerStatisticsList.add(new ServletContainerStatistics(threadPool));
            }

            return servletContainerStatisticsList;
        } catch (JMException e) {
            logger.error("获取Servlet容器实时数据失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 初始化数据承载的实体
     *
     */
    static void initBeans() throws MalformedObjectNameException {

        THREAD_POOLS.clear();

        // 获取tomcat容器指标
        Set<ObjectName> tomcatThreadPools = getTomcatThreadPools();
        // 如果使用springboot内嵌的tomcat这里需要额外处理
        if (CollectionUtils.isEmpty(tomcatThreadPools)) {
            tomcatThreadPools = getInlineTomcatThreadPools();
        }
        THREAD_POOLS.addAll(tomcatThreadPools);
    }

    /**
     * 线程池名称
     */
    public String name() {

        final String name = threadPool.getKeyProperty("name");
        final String subType = threadPool.getKeyProperty("subType");
        if (StringUtils.isEmpty(subType)) {
            return name;
        }

        return name + "-" + subType;
    }

    /**
     * tomcat最大线程数
     */
    public int maxThreads() {

        Integer maxThreads = (Integer)getAttribute(threadPool, "maxThreads");
        return maxThreads == null ? 0 : maxThreads;
    }

    /**
     * tomcat当前线程数
     */
    public int currentThreadCount() {

        Integer currentThreadCount = (Integer)getAttribute(threadPool, "currentThreadCount");
        return currentThreadCount == null ? 0 : currentThreadCount;
    }

    /**
     * tomcat活跃的线程数
     */
    public int currentThreadsBusy() {

        Integer currentThreadsBusy = (Integer)getAttribute(threadPool, "currentThreadsBusy");
        return currentThreadsBusy == null ? 0 : currentThreadsBusy;
    }
}

