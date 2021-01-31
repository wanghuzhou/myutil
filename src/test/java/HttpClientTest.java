import com.wanghz.myutil.http.HttpConstant;
import com.wanghz.myutil.httpclient.HttpClientUtils;
import com.wanghz.myutil.okhttp.OKHttpUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
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

        String result = HttpClientUtils.postJson(url, param, "utf-8");
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

}
