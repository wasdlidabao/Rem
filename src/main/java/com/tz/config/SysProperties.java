package com.tz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage")
public class SysProperties {

    /**
     * 从连接池获取连接的超时时间,不宜过长,单位 ms
     */
    private int connectionRequestTimeout = 200;
    /**
     * 指客户端从服务器读取数据包的间隔超时时间,不是总读取时间，默认30s
     */
    private int readTimeout = 30*1000;
    /**
     * 客户端和服务器建立连接超时，默认2s
     */
    private int connectTimeout = 2*1000;
    /**
     * httpClient 最大连接池数量，默认10
     */
    private int maxTotal = 10;
    /**
     * 同路由并发数，默认5
     */
    private int defaultMaxPerRoute = 5;
    /**
     * 重试次数
     */
    private int retryCount;

    /**
     * 是否开启重试
     */
    private boolean requestSentRetryEnabled;

}
