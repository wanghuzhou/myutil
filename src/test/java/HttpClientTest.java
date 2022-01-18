import com.wanghz.myutil.http.HttpConstant;
import com.wanghz.myutil.httpclient.HttpClientUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientTest {

    @Test
    public void get() throws IOException {
        String url = "http://127.0.0.1:8080/hello";
        Map<String, String> param = new HashMap<>();
        param.put("name", "哈哈");
        param.put("age", "22");

        String result = HttpClientUtils.get(url, param);
        System.out.println(result);
    }

    @Test
    public void post() throws IOException {
        String url = "http://127.0.0.1:8080/test/hello";
        Map<String, String> param = new HashMap<>();
        param.put("name", "哈哈");
        param.put("age", "22");
        String result = HttpClientUtils.postJson(url, param);
        System.out.println(result);
    }

    @Test
    public void post2() throws IOException {
        String url = "http://127.0.0.1:8080/hello";
        Map<String, String> param = new HashMap<>();
        param.put("name", "哈哈");
        param.put("age", "22");

        String result = HttpClientUtils.post(url, param);
        System.out.println(result);
    }

    /**
     * HttpClient4 带客户端证书请求示例代码
     */
    @Test
    public void testJks() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException, UnrecoverableKeyException {
        //Loading the Keystore file
        String path = "client.keystore.jks";
        char[] password = "123456".toCharArray();
        File file = new File(path);
        KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        clientKeyStore.load(new FileInputStream(path), password);
        //Building the SSLContext usiong the build() method
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(file, password)
                .loadKeyMaterial(clientKeyStore, password)
//                .setSecureRandom(new SecureRandom())
                .build();

        //Creating SSLConnectionSocketFactory object
        SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslcontext,
                new String[]{"TLSv1"}, null, NoopHostnameVerifier.INSTANCE);

        // 连接池
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                // 信任任何https
                .register("https", sslConSocFactory)
                .build();
        PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolConnManager.setMaxTotal(50);
        poolConnManager.setDefaultMaxPerRoute(5);
        poolConnManager.closeExpiredConnections();
        // 请求超时配置
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(HttpConstant.CONNECT_TIMEOUT)
                .setSocketTimeout(HttpConstant.EXECUTE_TIMEOUT)
                .setConnectionRequestTimeout(3000)
                .build();

        //Creating HttpClientBuilder
        HttpClientBuilder clientBuilder = HttpClients.custom();

        //Setting the SSLConnectionSocketFactory
//        clientBuilder.setSSLSocketFactory(sslConSocFactory);

        //Building the CloseableHttpClient
        CloseableHttpClient httpclient = clientBuilder
                .setDefaultRequestConfig(config)
                .setConnectionManager(poolConnManager)
                .build();

        //Creating the HttpPost request
        HttpPost httpPost = new HttpPost("https://host:port/api/v1");

        List<NameValuePair> pairList = new ArrayList<>();
        pairList.add(new BasicNameValuePair("param1", ""));
        pairList.add(new BasicNameValuePair("param2", ""));

        HttpEntity httpEntity = new UrlEncodedFormEntity(pairList, Consts.UTF_8);
        httpPost.setEntity(httpEntity);

        //Executing the request
        HttpResponse httpresponse = httpclient.execute(httpPost);

        //printing the status line
        System.out.println(httpresponse.getStatusLine());

        //Retrieving the HttpEntity and displaying the no.of bytes read
        HttpEntity entity = httpresponse.getEntity();
        if (entity != null) {
            System.out.println(EntityUtils.toString(entity));
        }
        EntityUtils.consume(entity);
    }

}
