package util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * @author xuzc
 * datetime 18/3/13 下午5:58
 */
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public JsonUtil() {
    }

    public static String toJson(Object obj) {
        try {
            return getJsonMapper().writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("toJson出错：" + e.getMessage());
        }
    }

    public static <T> T readObject(String json, Class<T> clz) {
        try {
            return getJsonMapper().readValue(json, clz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("readObject出错：" + e.getMessage());
        }
    }

    /**
     * 利用jsonObject处理返回map
     */
    public static Map<String, Object> fromJson(String json) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) getJsonMapper().readValue(json, HashMap.class);
            return result;
        } catch (Exception e) {
            throw new Exception("参数错误");
        }
    }

    public static JsonNode toJsonNode(String json) throws Exception {
        if (StringUtils.isBlank(json)) {
            return null;
        } else {
            try {
                return objectMapper.readTree(json);
            } catch (Exception var2) {
                throw new Exception("from json error,json=" + json);
            }
        }
    }

    public static ObjectMapper getJsonMapper() {
        return objectMapper;
    }

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
