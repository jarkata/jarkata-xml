package cn.jarkata.xml;

import cn.jarkata.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存模版信息
 */
public class CacheTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CacheTemplate.class);
    private static final ConcurrentHashMap<String, Map<String, XmlElement>> CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 缓存数据
     *
     * @param key        缓存key
     * @param elementMap 缓存数据
     */
    public static void put(String key, Map<String, XmlElement> elementMap) {
        if (StringUtils.isBlank(key)) {
            logger.error("缓存的key值为空,Key={}", key);
            return;
        }
        Map<String, XmlElement> temp = get(key);
        if (temp != null) {
            return;
        }
        CACHE_MAP.put(key, elementMap);
    }

    /**
     * 读取缓存里的数据
     *
     * @param key 缓存key
     * @return 缓存数据
     */
    public static Map<String, XmlElement> get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return CACHE_MAP.get(key);
    }
}