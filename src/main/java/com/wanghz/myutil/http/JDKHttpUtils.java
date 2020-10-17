package com.wanghz.myutil.http;

import com.wanghz.myutil.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JDKHttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(JDKHttpUtils.class);

    public static String reqGet(String urlParam, Map<String, Object> map) {
        String param = HttpCommonUtils.getUrlParamsByMap(map);
        String url = urlParam + "?" + param;
        return reqGet(url);
    }

    public static String reqGet(String urlParam, Map<String, Object> param, Map<String, String> header, int timeout) {
        String paramStr = HttpCommonUtils.getUrlParamsByMap(param);
        String url = urlParam + "?" + paramStr;
        return reqGet(url, header, timeout);
    }

    public static String reqGet(String urlParam) {
        return reqGet(urlParam, null, 0);
    }

    /**
     * GET请求
     *
     * @param urlParam url地址
     * @param header   请求头参数
     * @param timeout  超时时间 秒
     * @return HTTP响应结果
     */
    public static String reqGet(String urlParam, Map<String, String> header, int timeout) {
        HttpURLConnection connection;
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
//        OutputStreamWriter out = null;
        timeout = Math.max(timeout * 1000, 0);
        long startTime = 0L;
        long endTime = 0L;
        try {
            URL url = new URL(urlParam);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(HttpConstant.CONNECT_TIMEOUT);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");
            // 请求头
            if (header != null && !header.isEmpty()) {
                for (String tmp : header.keySet()) {
                    connection.setRequestProperty(tmp, header.get(tmp));
                }
            }
//            connection.setRequestProperty(HttpConstant.HEADER_CONTENT_TYPE,HttpConstant.APPLICATION_JSON);
            startTime = System.currentTimeMillis();
            connection.setDoInput(true);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                logger.error("请求出错：code-{}, message-{}", connection.getResponseCode(), connection.getResponseMessage());
                return connection.getResponseCode() + " " + connection.getResponseMessage();
            }
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            endTime = System.currentTimeMillis();
        } catch (MalformedURLException e) {
            logger.error("MalformedURLException:", e);
        } catch (IOException e) {
            logger.error("IO错误:", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        logger.info("{} : {} 执行时长：{}ms 请求头：{} 表单参数:{}, Body 体参数：{} 响应：{}",
                "GET", "urlPara", endTime - startTime, "tmpReqHeaderStr", "tmpReqFormStr", "tmpReqBodyStr", result.toString());
        return result.toString();
    }


    public static String reqPostForm(String urlParam, Map<String, Object> body, Map<String, String> header, int timeout) {
        String reqBody = HttpCommonUtils.getUrlParamsByMap(body);
        return reqPost(urlParam, reqBody, header, true, timeout);
    }

    public static String reqPostJson(String urlParam, Map<String, Object> body, Map<String, String> header, int timeout) {
        if (header == null) {
            header = new HashMap<>();
        }
        header.put(HttpConstant.HEADER_CONTENT_TYPE, HttpConstant.APPLICATION_JSON);
        String reqBody = JsonUtil.toJSONString(body);
        return reqPost(urlParam, reqBody, header, false, timeout);
    }

    /**
     * POST请求
     *
     * @param urlParam url地址
     * @param header   请求头参数
     * @param timeout  超时时间 秒
     * @return HTTP响应结果
     */
    public static String reqPost(String urlParam, String body, Map<String, String> header, boolean isForm, int timeout) {
        HttpURLConnection connection;
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        OutputStreamWriter out = null;
        timeout = Math.max(timeout * 1000, 0);
        long startTime = 0L;
        long endTime = 0L;
        try {
            URL url = new URL(urlParam);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(HttpConstant.CONNECT_TIMEOUT);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("POST");
            // 请求头
            if (header != null && !header.isEmpty()) {
                for (String tmp : header.keySet()) {
                    connection.setRequestProperty(tmp, header.get(tmp));
                }
            }

            if (isForm) {
                connection.setRequestProperty(HttpConstant.HEADER_CONTENT_TYPE, HttpConstant.APPLICATION_FORM);
            }

            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(HttpCommonUtils.createSSLSocketFactory());
                ((HttpsURLConnection) connection).setHostnameVerifier(HttpCommonUtils.trustHostnameVerifier());
            }

            connection.setDoOutput(true);
            out = new OutputStreamWriter(connection.getOutputStream());
            out.write(body);
            out.flush();

            startTime = System.currentTimeMillis();
            /*if (!connection.getDoInput()) {
                connection.setDoInput(true);
            }*/
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                logger.error("请求出错：code-{}, message-{}", connection.getResponseCode(), connection.getResponseMessage());
                return connection.getResponseCode() + " " + connection.getResponseMessage();
            }
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            endTime = System.currentTimeMillis();
        } catch (MalformedURLException e) {
            logger.error("MalformedURLException:", e);
        } catch (IOException e) {
            logger.error("IO错误:", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        logger.info("{} : {} 执行时长：{}ms 请求头：{} 表单参数:{}, Body 体参数：{} 响应：{}",
                "POST", "urlPara", endTime - startTime, "tmpReqHeaderStr", "tmpReqFormStr", "tmpReqBodyStr", result.toString());
        return result.toString();
    }

}
