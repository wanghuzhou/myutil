package com.wanghz.myutil.http;

import com.wanghz.myutil.common.exception.MyUtilRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Http公共工具类
 */
public class HttpCommonUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpCommonUtils.class);

    public static String getUrlParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

    public static String getFormParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "\n");
        }
        return s;
    }

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sslFactory;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{HttpCommonUtils.trustAllCerts()}, new SecureRandom());
            sslFactory = sc.getSocketFactory();
        } catch (Exception e) {
            logger.error("生成安全套接字工厂出错", e);
            throw new MyUtilRuntimeException("连接出错");
        }
        return sslFactory;
    }

    /**
     * 信任所有证书
     */
    public static X509TrustManager trustAllCerts() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    /**
     * 信任所有域名
     */
    public static HostnameVerifier trustHostnameVerifier() {
        return (s, sslSession) -> true;
    }

}
