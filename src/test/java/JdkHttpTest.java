import com.wanghz.myutil.http.JDKHttpUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JdkHttpTest {

    @Test
    public void reqGet() {
        String tmpUrl = "http://www.baidu.com";
        Map<String, Object> map = new HashMap<>();
        map.put("name", "ddd");
        String s = JDKHttpUtils.get(tmpUrl);
        System.out.println(s);
    }

    @Test
    public void reqGet2() {
        String tmpUrl = "http://127.0.0.1:8080/test/test";
        Map<String, Object> map = new HashMap<>();
        map.put("name", "ddd");
        String s = JDKHttpUtils.get(tmpUrl);
        System.out.println(s);
    }

    @Test
    public void reqPostJson() {
        String tmpUrl = "http://127.0.0.1:8080/test/hello";
        Map<String, Object> map = new HashMap<>();
        map.put("name", "哈哈哈");
        map.put("age", "11");
        String s = JDKHttpUtils.postJson(tmpUrl, map, null, 10);
        System.out.println(s);
    }

    @Test
    public void reqPost() {
//        String tmpUrl = "http://127.0.0.1:8080/test/hello";
        String tmpUrl = "http://127.0.0.1:8080/hello";
        Map<String, Object> map = new HashMap<>();
        map.put("name", "哈哈哈");
        map.put("age", 11);
        String s = JDKHttpUtils.postForm(tmpUrl, map, null, 10);
        System.out.println(s);
    }
}
