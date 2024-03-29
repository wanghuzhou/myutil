package com.wanghz.myutil.okhttp;

import com.wanghz.myutil.http.HttpCommonUtils;
import com.wanghz.myutil.json.JsonUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OKHttp3 工具类
 *
 * @author wanghz
 * Created on 2020-08-30.
 */
public class OKHttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(OKHttpUtils.class);

    public final static int CONNECT_TIMEOUT = 3;
    public final static int READ_TIMEOUT = 30;
    public final static int WRITE_TIMEOUT = 30;
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    public static final OkHttpClient mOkHttpClient;

    /**
     * 自定义网络回调接口
     */
    public interface NetCall {
        void success(Call call, Response response) throws IOException;

        void failed(Call call, IOException e);
    }

    static {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //连接超时
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //读取超时
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //写入超时
                //支持HTTPS请求，跳过证书验证
                .sslSocketFactory(HttpCommonUtils.createSSLSocketFactory(), HttpCommonUtils.trustAllCerts())
                .hostnameVerifier(HttpCommonUtils.trustHostnameVerifier());

        // 连接复用默认配置
        ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.MINUTES);

        mOkHttpClient = clientBuilder
                .addInterceptor(new LoggingInterceptor())
                .addNetworkInterceptor(new UserAgentInterceptor(""))
                .connectionPool(connectionPool)
                .build();

    }

    /**
     * get请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @return
     */
    public static Response get(String url) {
        Request request = builder().get().url(url).build();
        try {
            return execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post请求，同步方式，提交数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @param bodyParams
     * @return
     */
    public static Response postForm(String url, Map<String, String> bodyParams) {
        RequestBody body = setFormBody(bodyParams);
        Request request = builder().post(body).url(url).build();
        try {
            return execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response postForm(String url, Map<String, String> bodyParams, String charset) {
        //1构造RequestBody
        RequestBody body = setFormBody(bodyParams, Charset.forName(charset));
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * get请求，异步方式，获取网络数据，是在子线程中执行的，需要切换到主线程才能更新UI
     *
     * @param url
     * @param netCall
     * @return
     */
    public void getAsync(String url, final NetCall netCall) {
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        //2 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //3 执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);

            }
        });
    }

    /**
     * post请求，异步方式，提交数据，是在子线程中执行的，需要切换到主线程才能更新UI
     *
     * @param url
     * @param bodyParams
     * @param netCall
     */
    public void postAsync(String url, Map<String, String> bodyParams, final NetCall netCall) {
        //1构造RequestBody
        RequestBody body = setFormBody(bodyParams);
        Request request = builder().post(body).url(url).build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);

            }
        });
    }

    /**
     * post的请求参数，构造RequestBody
     *
     * @param bodyParams
     * @return
     */
    private static RequestBody setFormBody(Map<String, String> bodyParams) {
        return setFormBody(bodyParams, StandardCharsets.UTF_8);
    }

    private static RequestBody setFormBody(Map<String, String> bodyParams, Charset charset) {
        FormBody.Builder formEncodingBuilder = new FormBody.Builder(charset);
        if (bodyParams != null) {
            Iterator<String> iterator = bodyParams.keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next();
                formEncodingBuilder.add(key, bodyParams.get(key));
                logger.debug("post http, post_Params==={}===={}", key, bodyParams.get(key));
            }
        }
        return formEncodingBuilder.build();
    }

    /**
     * json提交
     *
     * @throws IOException 抛出IO错误
     */
    public static String postJson(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public void postJsonAsync(String url, String json, final NetCall netCall) {
        RequestBody body = RequestBody.create(JSON, json);
        //2 构造Request
        Request request = builder().post(body).url(url).build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);
            }
        });
    }

/*    public static HttpUrl urlParse(String url) {
        HttpUrl httpUrl =HttpUrl.parse(url)
                .newBuilder()
                .addQueryParameter("access_token", "")
                .build();
        return httpUrl;
    }*/

    public static Request.Builder builder() {
        return new Request.Builder();
    }

    public static Request.Builder json(String url, Object obj) {
        RequestBody body;
        if (obj instanceof String) {
            body = RequestBody.create(JSON, (String) obj);
        } else {
            body = RequestBody.create(JSON, JsonUtil.toJSONString(obj));
        }
        return builder().url(url).post(body);
    }

    public static Response execute(Request request) throws IOException {
        Response response = mOkHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            logger.error("http响应码 {}, 响应信息 {}", response.code(), response.message());
            if (response.body() != null) {
                logger.error("http响应body {}", response.body().string());
            }
        }
        return response;
    }

    public static String executeForString(Request request) {
        try {
            return execute(request).body().string();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("http请求出错", e);
        }
        return null;
    }

    public static <T> T executeForEntity(Request request, Class<T> clazz) {
        try {
            String resp = execute(request).body().string();
            if (clazz == String.class) {
                return (T) resp;
            }
            return JsonUtil.parseObject(resp, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("http请求出错", e);
        }
        return null;
    }

    /**
     * OKHttp 日志拦截器
     */
    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.currentTimeMillis();
            logger.info(String.format("入参 %s , %s%n%s", request.url(), chain.connection(), request.headers()));
            System.out.printf("入参 %s , %s %n %s %n", request.url(), chain.connection(), request.headers());
            Response response = chain.proceed(request);

            long t2 = System.currentTimeMillis();
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            logger.debug("http响应头 {}", response.headers());
            logger.info("http请求信息： url {} , 耗时 {}ms, 出参 {}", response.request().url(), (t2 - t1), responseBody.string());
            System.out.printf("http请求信息 url %s 耗时 %dms%n%s%n", response.request().url(), (t2 - t1), response.headers());

            return response;
        }
    }

    /**
     * 客户端标识UserAgent拦截
     */
    static class UserAgentInterceptor implements Interceptor {

        private final String userAgent;

        public UserAgentInterceptor(String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", userAgent)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }
}
