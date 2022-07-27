package com.utiltest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.wanghz.myutil.json.JsonUtil;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTest {

    @Test
    public void test1() {
        String str = "{\"b\":\"b\",\"a\":null}";
        Map<String, String> map = JsonUtil.parseMap(str);
        System.out.println(JsonUtil.toJSONString(map));

        String str2 = "{\"b\": 1234567892131,\"a\":null}";
        Map<String, Object> map2 = JsonUtil.parseMap(str2);
        System.out.println(JsonUtil.toJSONString(map2));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "val1");
        JSONObject jsonObject1 = new JSONObject(jsonObject);
        jsonObject1.put("key", "val2");
        System.out.println(jsonObject.toJSONString());
        System.out.println(jsonObject1.toJSONString());
        System.out.println(jsonObject1.getJSONObject("dd"));

    }

    @Test
    public void test2() {
        Map<String, Object> map2 = new HashMap<>();
        map2.put("date", new Date());
        String str = JsonUtil.toJSONString(map2);
        String str2 = JSON.toJSONString(map2);
        System.out.println(str);
        System.out.println(str2);

        String str3 = "[[{\"UploadTime\":1609984620104,\"age\":5,\"applyDate\":1609921962000,\"bussID\":\"37160992196239379\",\"consultationPrice\":0,\"consultationType\":\"1\",\"content\":\"ces\",\"deptID\":\"2J34473o\",\"deptName\":\"儿保科\",\"doctorCertID\":\"\",\"doctorId\":\"10361ZK110008\",\"doctorName\":\"智康测试医生\",\"endDate\":1609924560000,\"mobile\":\"15381189933\",\"onsultationAttribute\":\"1\",\"organID\":\"749FC81F-5E63-20190516A0927\",\"organName\":\"测试机构-122003\",\"patientCertID\":\"330106201510136727\",\"patientCertType\":\"1\",\"patientName\":\"俞敉娜\",\"paymentChannel\":\"9\",\"sex\":0,\"startDate\":1609922485000,\"subjectCode\":\"\",\"subjectName\":\"\",\"unitID\":\"5827290D-52BD-44F0-9EF3-C1C014CE6632\"}]]";
        List<Object> list = JsonUtil.parseList(str3);
        System.out.println(JsonUtil.toJSONString(list));
    }

    @Test
    public void test3(){
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

    @Test
    public void test4(){
        String str = "{\"a\":\"aaa\", \"b\":[{\"a\":\"1\"},{\"a\":\"2\"}]}";
        Abc abc = JsonUtil.parseObject(str, Abc.class);
        System.out.println(abc);
    }
}
