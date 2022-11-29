package com.wanghz.myutil.http;

import com.wanghz.myutil.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * JDK11的HTTP工具类
 * 流式API可以很方便的组装 url, header, timeout等信息，组装完builder再直接调用execute方法
 *
 * @author wanghz
 */
public class JDK11HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(JDK11HttpUtils.class);

    public static String get(String urlParam) {
        return get(urlParam, null);
    }

    public static String get(String urlParam, Map<String, Object> param) {
        return get(urlParam, param, null, 0);
    }

    public static String get(String urlParam, Map<String, Object> param, Map<String, String> header, int timeout) {
        String paramStr = HttpCommonUtils.getUrlParamsByMap(param);
        String url = urlParam + "?" + paramStr;
        return get(url, header, timeout);
    }

    public static <T> T getForObject(String urlParam, Class<T> tClass) {
        String result = get(urlParam, null);
        return JsonUtil.parseObject(result, tClass);
    }

    /**
     * GET请求
     *
     * @param urlParam url地址
     * @param header   请求头参数
     * @param timeout  超时时间 秒
     * @return HTTP响应结果
     */
    public static String get(String urlParam, Map<String, String> header, int timeout) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlParam))
                .timeout(timeout == 0 ? Duration.ofMillis(HttpConstant.EXECUTE_TIMEOUT) : Duration.ofMillis(timeout));
        // 请求头
        buildHeader(header, builder);
        return execute(builder, StandardCharsets.UTF_8);
    }


    /**
     * post表单提交
     *
     * @param urlParam url地址
     * @param body     body
     * @param header   请求头
     * @param timeout  超时时间 秒
     * @return string
     */
    public static String postForm(String urlParam, Map<String, Object> body, Map<String, String> header, int timeout) {
        String reqBody = HttpCommonUtils.getUrlParamsByMap(body);
        if (header == null) {
            header = new HashMap<>();
        }
        header.put(HttpConstant.HEADER_CONTENT_TYPE, HttpConstant.APPLICATION_FORM);
        return post(urlParam, reqBody, header, timeout);
    }

    /**
     * post json提交
     *
     * @param urlParam url地址
     * @param body     body
     * @param header   请求头
     * @param timeout  超时时间 秒
     * @return string
     */
    public static String postJson(String urlParam, Map<String, Object> body, Map<String, String> header, int timeout) {
        if (header == null) {
            header = new HashMap<>();
        }
        header.put(HttpConstant.HEADER_CONTENT_TYPE, HttpConstant.APPLICATION_JSON);
        String reqBody = JsonUtil.toJSONString(body);
        return post(urlParam, reqBody, header, timeout);
    }

    /**
     * POST请求
     *
     * @param urlParam url地址
     * @param header   请求头参数
     * @param timeout  超时时间 秒
     * @return HTTP响应结果
     */
    public static String post(String urlParam, String body, Map<String, String> header, int timeout) {
        HttpRequest.BodyPublisher bodyPublisher = null;
        if (body != null) {
            bodyPublisher = HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8);
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(urlParam))
                .POST(bodyPublisher == null ? HttpRequest.BodyPublishers.noBody() : bodyPublisher)
                .timeout(Duration.ofMillis(timeout));

        buildHeader(header, builder);
        return execute(builder, StandardCharsets.UTF_8);
    }

    /**
     * 请求头放入builder
     */
    public static void buildHeader(Map<String, String> header, HttpRequest.Builder builder) {
        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                builder.header(key, header.get(key));
            }
        }
    }

    private static SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{HttpCommonUtils.trustAllCerts()}, new SecureRandom());
        return sc;
    }

    public static String execute(HttpRequest.Builder builder, Charset charset) {
        HttpRequest request = builder.build();
        try {
            HttpClient client = HttpClient.newBuilder()
                    .sslContext(getSSLContext())
                    .connectTimeout(Duration.ofMillis(HttpConstant.CONNECT_TIMEOUT))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(charset));

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                logger.error("jdk11httpUtil出错 错误码 {} 出参 {}", response.statusCode(), response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String execute(HttpRequest.Builder builder, HttpClient client) {
        try {
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                logger.error("jdk11httpUtil出错 uri{} 错误码 {} 出参 {}", response.uri().getRawPath(), response.statusCode(), response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    public HttpResponse<String> postFile(String url, InputStream inputStream, String... headers) throws IOException, InterruptedException {

        Supplier<? extends InputStream> streamSupplier = (Supplier<BufferedInputStream>) () ->
                inputStream == null ? null : new BufferedInputStream(inputStream);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(headers)
                .POST(HttpRequest.BodyPublishers.ofInputStream(streamSupplier));

        HttpRequest request = builder.build();
        HttpClient client = HttpClient.newBuilder().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> postFile(String url, Path file, String... headers) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(headers)
                .POST(null == file ? HttpRequest.BodyPublishers.noBody() :
                        HttpRequest.BodyPublishers.ofFile(file))
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

}
