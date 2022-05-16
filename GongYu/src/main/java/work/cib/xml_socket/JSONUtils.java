package work.cib.xml_socket;

import java.util.HashMap;
import java.util.Map;

import work.cib.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JSONUtils {

    /**Bean对象转JSON */
    public static String beanToJson(Object object, String dataFormatString) {
        if (object != null) {
            if (StringUtils.isEmpty(dataFormatString)) {
                return JSONObject.toJSONString(object);
            }
            return JSON.toJSONStringWithDateFormat(object, dataFormatString);
        } else {
            return null;
        }
    }

    /**json字符串转map*/
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsonToMap(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSON.parseObject(json, Map.class);
    }

    /** Bean对象转JSON*/
    public static String beanToJson(Object object) {
        if (object != null) {
            return JSON.toJSONString(object);
        } else {
            return null;
        }
    }

    /**String转JSON字符串*/
    public static String stringToJsonByFastjson(String key, String value) {
        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return beanToJson(map, null);
    }

    /**将json字符串转换成对象*/
    public static Object jsonToBean(String json, Object clazz) {
        if (StringUtils.isEmpty(json) || clazz == null) {
            return null;
        }
        return JSON.parseObject(json, clazz.getClass());
    }

}


