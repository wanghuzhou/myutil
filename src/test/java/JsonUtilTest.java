import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.wanghz.myutil.json.JsonUtil;
import com.wanghz.myutil.security.HashUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class JsonUtilTest {

    @Test
    public void test1() {
        String s2 = "{\"a\":\"哈\\\\哈哈\"}";

//        String s2 = StringEscapeUtils.unescapeJson(s);
//        String s2 = s.replace("\\","/");
        System.out.println(s2);
        Map<String, String> map = JsonUtil.parseMap(s2);
        JSONObject jsonObject = JSON.parseObject(s2);
        System.out.println(JsonUtil.toJSONString(map));
        System.out.println(jsonObject.toJSONString());
    }

    @Test
    public void test2() {

        Map<String, String> map = new HashMap<>();
        map.put("a", "哈哈\\哈");
        String str = JsonUtil.toJSONString(map);
        System.out.println(JsonUtil.toJSONString(map));
        Map<String, String> map2 = JsonUtil.parseMap(str);
        System.out.println(map2.get("a"));
    }

    @Test
    public void test3() {
        String s = "sfsdf";
        System.out.println(HashUtils.sha1Encode(s));
        System.out.println(HashUtils.md5Encode(s));
        System.out.println(HashUtils.sha2Encode(s));
        String sha1 = DigestUtils.sha1Hex(s.getBytes());
        String sha256Hex = DigestUtils.sha256Hex(s.getBytes());
        String md5 = DigestUtils.md5Hex(s.getBytes());
        System.out.println(sha1);
        System.out.println(md5);
        System.out.println(sha256Hex);
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static XmlMapper xmlMapper = new XmlMapper();

    @Test
    public void test4() throws IOException {
        User user = new User("waddd", "dsafsdf");
        System.out.println(objectMapper.writeValueAsString(user));
        System.out.println(xmlMapper.writeValueAsString(user));

        Map<String, String> map = new HashMap<>();
        map.put("aa", "aavalue");
        map.put("bb", "bbbvalue");
        System.out.println(objectMapper.writeValueAsString(map));
        System.out.println(xmlMapper.writeValueAsString(map));
    }

    @Test
    public void test5() throws IOException {

        String xml = "<User><username>waddd</username><password>dsafsdf</password><a>0</a><b>0.0</b></User>";
        User user = xmlMapper.readValue(xml, User.class);
        System.out.println(user.toString());

        String json = convertXmlToJson(xml);
        System.out.println(json);
//        System.out.println(convertJsonToXml(json));
        System.out.println(json2map2xml(json));


    }

    public static String convertXmlToJson(String xml) {


        StringWriter w = new StringWriter();
        try {
            JsonParser jp = xmlMapper.getFactory().createParser(xml);
            JsonGenerator jg = objectMapper.getFactory().createGenerator(w);
            while (jp.nextToken() != null) {
                jg.copyCurrentEvent(jp);
            }
            jp.close();
            jg.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return w.toString();
    }

    public static String convertJsonToXml(String xml) {


        StringWriter w = new StringWriter();
        try {
            JsonParser jp = objectMapper.getFactory().createParser(xml);
            ToXmlGenerator toXmlGenerator = xmlMapper.getFactory().createGenerator(w);
            while (jp.nextToken() != null) {
                toXmlGenerator.copyCurrentEvent(jp);
            }
            jp.close();
            toXmlGenerator.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return w.toString();
    }

    public static String json2map2xml(String json) throws IOException {
        Map map = objectMapper.readValue(json, HashMap.class);

        return xmlMapper.writeValueAsString(map);
    }

    @Test
    public void test6() {
        String jsonstr = "{\"msg\":{\"head\":{\"version\":\"1.0\",\"bizcode\":\"1006\",\"senddate\":\"20140827\",\"sendtime\":\"110325\",\"seqid\":\"1\"},\"body\":{\"datalist\":\"wahaha\",\"rstcode\":\"000000\",\"rstmsg\":\"成功\"}}}";
        JsonNode root = JsonUtil.readTree(jsonstr);
        System.out.println(root.path("msg").path("body").path("datalist").asText());

        JSONObject jsonObject = JSON.parseObject(jsonstr);
        JsonNode jsonNode = JsonUtil.valueToTree(jsonObject);
        System.out.println(jsonNode.toString());
        System.out.println(jsonNode.get("msg").get("head"));
        System.out.println(jsonNode.getNodeType());

        String str3 = "[[{\"UploadTime\":1609984620104,\"age\":5,\"applyDate\":1609921962000,\"bussID\":\"37160992196239379\",\"consultationPrice\":0,\"consultationType\":\"1\",\"content\":\"ces\",\"deptID\":\"2J34473o\",\"deptName\":\"儿保科\",\"doctorCertID\":\"\",\"doctorId\":\"10361ZK110008\",\"doctorName\":\"智康测试医生\",\"endDate\":1609924560000,\"mobile\":\"15381189933\",\"onsultationAttribute\":\"1\",\"organID\":\"749FC81F-5E63-20190516A0927\",\"organName\":\"测试机构-122003\",\"patientCertID\":\"330106201510136727\",\"patientCertType\":\"1\",\"patientName\":\"俞敉娜\",\"paymentChannel\":\"9\",\"sex\":0,\"startDate\":1609922485000,\"subjectCode\":\"\",\"subjectName\":\"\",\"unitID\":\"5827290D-52BD-44F0-9EF3-C1C014CE6632\"}]]";
        String str = "{\"a\":" + str3 + "}";
        String str2 = "{\"b\": 1234567892131,\"a\":null}";
        JsonNode jsonNode2 = JsonUtil.readTree(str3);
        System.out.println(jsonNode2.isArray());
        System.out.println(jsonNode2.get(0).get(0).toPrettyString());
        System.out.println(jsonNode2.get(0).get(0).get("UploadTime").asText());

    }

}
