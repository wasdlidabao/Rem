package com.tz.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@Component
public class HttpClientConfig {

    private SysProperties sysProperties;

    @Autowired
    public void setSysProperties(SysProperties sysProperties) {
        this.sysProperties = sysProperties;
    }

    /**
     * httpClientTemplate 模板
     */
    @Bean(name = "httpRestTemplate")
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient());
        // 从连接池获取连接的超时时间,不宜过长,单位 ms
        clientHttpRequestFactory.setConnectionRequestTimeout(sysProperties.getConnectionRequestTimeout());
        // 指客户端从服务器读取数据包的间隔超时时间,不是总读取时间，默认30s
        clientHttpRequestFactory.setReadTimeout(60 * 1000);
        // 客户端和服务器建立连接超时，默认2s
        clientHttpRequestFactory.setConnectTimeout(sysProperties.getConnectTimeout());
        // 获取httpClient参数配置
        CloseableHttpClient httpClient = httpClient();
        // 设置配置参数
        clientHttpRequestFactory.setHttpClient(httpClient);

        return createRestTemplate(clientHttpRequestFactory);
    }

    private RestTemplate createRestTemplate(ClientHttpRequestFactory factory) {
        //1.实例化使用httpclient的RestTemplate
        RestTemplate restTemplate = new RestTemplate(factory);
        //2.采用RestTemplate内部的MessageConverter
        //重新设置StringHttpMessageConverter字符集，解决中文乱码问题
        modifyDefaultCharset(restTemplate);
        //3.设置错误处理器
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
    }

    /**
     * 修改默认的字符集类型为utf-8
     *
     * @param restTemplate
     */
    private void modifyDefaultCharset(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        /*HttpMessageConverter<?> converterTarget = null;
        for (HttpMessageConverter<?> item : converterList) {
            if (StringHttpMessageConverter.class == item.getClass()) {
                converterTarget = item;
                break;
            }
        }
        if (null != converterTarget) {
            converterList.remove(converterTarget);
        }
        Charset defaultCharset = Charset.forName("UTF-8");
        converterList.add(1, new StringHttpMessageConverter(defaultCharset));*/

        converterList.stream().forEach(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                //设置编码为UTF-8
                messageConverter.setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
    }

    public CloseableHttpClient httpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        try {
            //设置信任ssl访问
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
            httpClientBuilder.setSSLContext(sslContext);
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    // 注册http和https请求
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build();
            //使用Httpclient连接池的方式配置(推荐)，同时支持netty，okHttp以及其他http框架
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 最大连接数
            poolingHttpClientConnectionManager.setMaxTotal(sysProperties.getMaxTotal());
            // 同路由并发数
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(sysProperties.getDefaultMaxPerRoute());
            //配置连接池
            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
            // 重试次数
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(sysProperties.getRetryCount(), sysProperties.isRequestSentRetryEnabled()));
            //设置默认请求头
            List<Header> headers = getDefaultHeaders();
            httpClientBuilder.setDefaultHeaders(headers);
            //设置长连接保持策略
            // httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy());
           /*启动清理线程，也可以启用用CloseableHttpClient的清理线程
            Thread t = new IdleConnectionMonitorThread(connectionManager);
            t.setName("httpconnections-pool-evict-thread");
            t.start();
           */
            //设置后台线程剔除失效连接
            httpClientBuilder.evictExpiredConnections();
            httpClientBuilder.evictIdleConnections(100, TimeUnit.MILLISECONDS);
            return httpClientBuilder.build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("初始化HTTP连接池出错", e);
        }
        return null;
    }


    /**
     * 设置请求头
     *
     * @return
     */
    private List<Header> getDefaultHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN"));
        headers.add(new BasicHeader("Connection", "Keep-Alive"));
        return headers;
    }
}

