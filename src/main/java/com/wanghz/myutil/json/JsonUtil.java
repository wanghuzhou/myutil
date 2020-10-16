package com.wanghz.myutil.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wanghz.myutil.common.exception.MyUtilRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;


/**
 * Json支持工具类
 *
 * @author wanghz
 */
public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setTimeZone(TimeZone.getDefault())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);;

    private JsonUtil() {
    }

    static {
        // null值转为空字符串
        /*OBJECT_MAPPER.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
                arg1.writeString("");
            }
        });*/
//        OBJECT_MAPPER.getTypeFactory().constructParametricType(Long.class, String.class);
        // long类型转为String
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        OBJECT_MAPPER.registerModule(simpleModule);
    }

    /**
     * json字符串转 map
     *
     * @param jsonStr JSON字符串
     * @return Map对象
     */
    public static <K, V> Map<K, V> parseMap(String jsonStr) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<Map<K, V>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("Json格式化错误", e);
            throw new MyUtilRuntimeException(e);
        }
    }

    /**
     * 实体类转Map
     *
     * @param object 实体类
     * @return Map<String, String>
     */
    public static <K, V> Map<K, V> parseMap(Object object) {
        try {
            String jsonStr = OBJECT_MAPPER.writeValueAsString(object);
            return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<Map<K, V>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("Json格式化错误", e);
            throw new MyUtilRuntimeException(e);
        }
    }

    /**
     * json字符串转 List
     *
     * @param jsonStr JSON字符串
     * @return List对象
     */
    public static <T> List<T> parseList(String jsonStr) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<List<T>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("Json格式化错误", e);
            throw new MyUtilRuntimeException(e);
        }
    }


    /**
     * Json字符串转换成Pojo对象
     *
     * @param jsonStr Json字符串
     * @param clazz   pojo类型
     * @param <T>     泛型Class类型
     * @return 转换完毕Pojo的List对象
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Json转换出错", e);
            throw new MyUtilRuntimeException(e);
        }
    }

    /**
     * Pojo对象转换成JSON字符串
     *
     * @param object pojo对象
     * @return Json字符串
     */
    public static String toJSONString(Object object) {
//        return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Json转换出错", e);
            throw new MyUtilRuntimeException(e);
        }
    }

    /**
     * 解析复杂嵌套json
     * @param jsonStr json字符串
     * @return JsonNode
     */
    public static JsonNode readTree(String jsonStr) {
        try {
            return OBJECT_MAPPER.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            logger.error("Json转换出错", e);
            throw new MyUtilRuntimeException(e);
        }
    }

    /**
     * 解析复杂嵌套json
     * @param object json对象
     * @return JsonNode
     */
    public static JsonNode readTree(Object object) {
        try {
            String jsonStr = OBJECT_MAPPER.writeValueAsString(object);
            return OBJECT_MAPPER.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            logger.error("Json转换出错", e);
            throw new MyUtilRuntimeException(e);
        }
    }


}