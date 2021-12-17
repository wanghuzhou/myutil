import com.wanghz.myutil.okhttp.OKHttpUtils;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OKHttpTest {

    @Test
    public void test() throws IOException {
        Response test = OKHttpUtils.get("http://www.baidu.com/");
        if (test.isSuccessful() && test.body() != null)
            System.out.println(test.body().string());
        else
            System.out.println(test.code() + " " + test.message());
    }

    @Test
    public void test2() throws IOException {
        String json = "{\n" +
                "    \"name\": \"哈哈wanghz\",\n" +
                "    \"age\": \"22\"\n" +
                "}";
//        String test = OKHttpUtils.postJson("http://localhost:8080/test/hello", json);
        Request request = OKHttpUtils.json("http://localhost:8082/base/detail", "{\"id\":3}").build();
        String resp = OKHttpUtils.executeForString(request);
//        System.out.println(test);
        System.out.println(resp);

        RequestBody formBody = new FormBody.Builder().add("hosId", "769031850").build();
        Request request2 = OKHttpUtils.builder().url("http://localhost:8082/base/detail").post(formBody).build();
        resp = OKHttpUtils.executeForString(request2);
        System.out.println(resp);

    }

    @Test
    public void test3() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "wanghz");
        Response test = OKHttpUtils.postForm("http://127.0.0.1:8080/test/hello", map);
        System.out.println(test.body().string());
    }

}
