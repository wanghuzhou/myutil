package com.wanghz.myutil.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wanghz.myutil.common.exception.MyUtilRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


/**
 * Json支持工具类
 *
 * @author wanghz
 */
public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setTimeZone(TimeZone.getDefault())
            // 非空
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            // 小驼峰
//            .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
            // 大小写不敏感
//            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            // 忽略注解
//            .configure(MapperFeature.USE_ANNOTATIONS, false)
            // 忽略未知字段
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 序列化显示class类型
//            .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

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
//        OBJECT_MAPPER.registerModule(simpleModule).registerModule(new JavaTimeModule());
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
    public static <K, V> Map<K, V> convertMap(Object object) {
        return OBJECT_MAPPER.convertValue(object, new TypeReference<>() {
        });
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
     * @return 转换完毕Pojo对象
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
     * Json字符串转换成Pojo对象，方便泛型嵌套的对象
     *
     * @param jsonStr       Json字符串
     * @param typeReference 类型参考
     * @param <T>           泛型Class类型
     * @return 转换完毕Pojo对象
     */
    public static <T> T parseObject(String jsonStr, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("Json转换出错", e);
            return null;
        }
    }

    /**
     * Pojo对象转换成JSON字符串
     *
     * @param object pojo对象
     * @return Json字符串
     */
    public static String toJSONString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Json转换出错", e);
            throw new MyUtilRuntimeException(e);
        }
    }

    /**
     * 解析json字符串，返回JsonNode树
     *
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
     * 解析json byte数组，返回JsonNode树
     *
     * @param bytes json byte数组
     * @return JsonNode
     */
    public static JsonNode readTree(byte[] bytes) {
        try {
            return OBJECT_MAPPER.readTree(bytes);
        } catch (IOException e) {
            logger.error("Json转换出错", e);
            throw new MyUtilRuntimeException(e);
        }
    }

    /**
     * JsonNode转实体类
     *
     * @param jsonNode  json树
     * @param valueType 实体类型
     * @return 实体类
     */
    public static <T> T treeToValue(JsonNode jsonNode, Class<T> valueType) {
        try {
            return OBJECT_MAPPER.treeToValue(jsonNode, valueType);
        } catch (JsonProcessingException e) {
            logger.error("Json转换出错", e);
            throw new MyUtilRuntimeException(e);
        }
    }

    /**
     * 实体类转JsonNode
     *
     * @param object 实体类
     * @return 继承JsonNode泛型
     */
    public static <T extends JsonNode> T valueToTree(Object object) {
        return OBJECT_MAPPER.valueToTree(object);
    }


}
