package com.wanghz.myutil.httpclient;

import com.alibaba.fastjson.JSON;
import com.wanghz.myutil.common.exception.MyUtilRuntimeException;
import com.wanghz.myutil.http.HttpCommonUtils;
import com.wanghz.myutil.http.HttpConstant;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HttpClient4工具类
 */
public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final CloseableHttpClient httpClient;

    // 采用静态代码块，初始化超时时间配置，再根据配置生成默认httpClient对象
    static {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                // 信任任何https
                .register("https", new SSLConnectionSocketFactory(getSSLContext(), HttpCommonUtils.trustHostnameVerifier()))
                .build();
        PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolConnManager.setMaxTotal(50);
        poolConnManager.setDefaultMaxPerRoute(5);
        poolConnManager.closeExpiredConnections();
//        poolConnManager.closeIdleConnections(10, TimeUnit.SECONDS);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(HttpConstant.CONNECT_TIMEOUT)
                .setSocketTimeout(HttpConstant.EXECUTE_TIMEOUT)
                .setConnectionRequestTimeout(3000)
                .build();
        // 去除User-Agent
        HttpRequestInterceptor requestInterceptor = (request, context) -> request.removeHeaders("User-Agent");

        ConnectionKeepAliveStrategy myStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 5 * 1000;
        };

        httpClient = HttpClientBuilder.create()
//                .disableContentCompression()
                .setDefaultRequestConfig(config)
                .setConnectionManager(poolConnManager)
                .addInterceptorLast(requestInterceptor)
                .setKeepAliveStrategy(myStrategy)
                .evictExpiredConnections()
                .build();
    }

    public static String get(String url) throws IOException {
        return get(url, null);
    }

    public static String get(String url, Map<String, String> params) throws IOException {
        try {
            return get(url, params, HttpConstant.UTF8_ENCODE);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String url, Map<String, String> params) throws IOException {
        return post(url, params, HttpConstant.UTF8_ENCODE);
    }

    /**
     * HTTP Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String get(String url, Map<String, String> params, String charset) throws IOException, URISyntaxException {
        HttpGet httpGet;
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            URIBuilder uriBuilder = new URIBuilder(url);
            //设置参数
            uriBuilder.addParameters(pairs);
            httpGet = new HttpGet(uriBuilder.build());
        } else {
            httpGet = new HttpGet(url);
        }
        return execute(httpGet);

    }

    public static String postJson(String url, Object obj) {
        return postJson(url, obj, null);
    }

    public static String postJson(String url, Object obj, Map<String, String> header) {
        try {
            if (obj instanceof String) {
                return postJson(url, (String) obj, header, HttpConstant.UTF8_ENCODE);
            }
            return postJson(url, JSON.toJSONString(obj), header, HttpConstant.UTF8_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("post请求出错", e);
            return null;
        }
    }

    public static String postJson(String url, String json, Map<String, String> header, String charset) throws IOException {
        StringEntity entity = new StringEntity(json, charset);
        entity.setContentEncoding(HttpConstant.UTF8_ENCODE);
        entity.setContentType(HttpConstant.APPLICATION_JSON);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);

        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {

                httpPost.addHeader(entry.getKey(), entry.getValue());
//                httpPost.setHeaders();
            }
        }

        logger.info("HttpClient PostJson 请求地址：{}  请求头：{} 入参: {}", url, JSON.toJSONString(header), json);
        return execute(httpPost, charset);
    }

    public static String postJson(String url, String json, Header[] headers, String charset) throws IOException {
        StringEntity entity = new StringEntity(json, charset);
        entity.setContentEncoding(HttpConstant.UTF8_ENCODE);
        entity.setContentType(HttpConstant.APPLICATION_JSON);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        httpPost.setHeaders(headers);

        logger.info("HttpClient PostJson 请求地址：{}  请求头：{} 入参: {}", url, JSON.toJSONString(headers), json);
        return execute(httpPost, charset);
    }

    public static InputStream downloadFile(String url) {
        HttpGet httpGet = new HttpGet(url);
        long startTime = System.currentTimeMillis();
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            logger.error("HttpClient 请求文件出错 {}", url, e);
            return null;
        }
        long endTime = System.currentTimeMillis();
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            httpGet.abort();
            logger.error("HttpClient 请求文件出错 错误码: {}  错误信息: {}  耗时：{} 毫秒",
                    statusCode, response.getStatusLine().getReasonPhrase(), endTime - startTime);
            return null;
        }
        HttpEntity entity = response.getEntity();
        InputStream inputStream = null;
        if (entity != null) {
            try {
                inputStream = entity.getContent();
            } catch (IOException e) {
                logger.error("HttpClient 下载文件出错", e);
                return null;
            }
        }
        logger.info("HttpClient 耗时：{} 毫秒", endTime - startTime);
        return inputStream;
    }

    public static String uploadFile(String url, String filePath) throws IOException {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addPart("file", new FileBody(new File(filePath)));
        // 设置RFC6532 防止乱码
        multipartEntityBuilder.setMode(HttpMultipartMode.RFC6532);

        HttpUriRequest request = RequestBuilder.post().setUri(url)
                .addHeader(HttpHeaders.CONTENT_TYPE, ContentType.MULTIPART_FORM_DATA.getMimeType())
                .setEntity(multipartEntityBuilder.build())
                .build();

        logger.info("HttpClient 文件上传 {}", request.getRequestLine());
        return execute(request);
    }

    public static String execute(final RequestBuilder rb) throws IOException {
        HttpUriRequest request = rb.build();
        logger.info("HttpClient 请求行：{}  请求头：{} 表单入参: {} json: {}", request.getRequestLine(),
                Arrays.toString(request.getAllHeaders()), rb.getParameters(),
                rb.getEntity() == null ? null : EntityUtils.toString(rb.getEntity()));
        return execute(request, HttpConstant.UTF8_ENCODE);
    }

    public static String execute(final HttpUriRequest request) throws IOException {
        return execute(request, HttpConstant.UTF8_ENCODE);
    }

    public static String execute(final HttpUriRequest request, String charset) throws IOException {
        CloseableHttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            request.abort();
            logger.error("HttpClient, error status code : {}", statusCode);
            throw new MyUtilRuntimeException("HttpClient,error status code :" + statusCode);
        }
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
            result = EntityUtils.toString(entity, charset);
        }
        EntityUtils.consume(entity);
        return result;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     * @throws IOException
     */
    public static String post(String url, Map<String, String> params, String charset) throws IOException {
        List<NameValuePair> pairs = null;
        if (params != null && !params.isEmpty()) {
            pairs = convertMap2BasicNameValuePairs(params);
        }

        HttpPost httpPost = new HttpPost(url);
        if (pairs != null && pairs.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
        }
        return execute(httpPost, charset);
    }

    /**
     * HTTPS Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doGetSSL(String url, Map<String, String> params, String charset) throws IOException {

        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
        }
        HttpGet httpGet = new HttpGet(url);

        // https  注意这里获取https内容，使用了忽略证书的方式，当然还有其他的方式来获取https内容
        CloseableHttpClient httpsClient = createSSLClientDefault();
        CloseableHttpResponse response = httpsClient.execute(httpGet);
        return execute(httpGet);
    }

    /**
     * 这里创建了忽略整数验证的CloseableHttpClient对象
     *
     * @return
     */
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build();
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    public static SSLContext getSSLContext() {

        SSLContext sslContext = null;
        try {
            sslContext = SSLContextBuilder.create().loadTrustMaterial(TrustAllStrategy.INSTANCE).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    private static NameValuePair[] convertMap2NameValuePairs(Map<String, String> data) {
        return data.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .toArray(NameValuePair[]::new);
    }

    private static List<NameValuePair> convertMap2BasicNameValuePairs(Map<String, String> data) {
        return data.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static StringEntity json2Entity(String json) {
        return new StringEntity(json, ContentType.APPLICATION_JSON);
    }
}
