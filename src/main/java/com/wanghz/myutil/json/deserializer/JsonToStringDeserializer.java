package com.wanghz.myutil.json.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * 自定义json反序列化规则<br/>
 * 将json数据反序列化为String
 *
 * @author whz
 * @date 2022/6/21 13:27
 */
public class JsonToStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        if (p == null) {
            return null;
        }
        JsonNode node = p.getCodec().readTree(p);
        if (node == null) {
            return null;
        }
        return node.toString();
    }
}
