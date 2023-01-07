package cn.jarkata.xml.handle;

import cn.jarkata.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认报文解析，将报文解析出后，节点名称直接作为key值写入map
 * 本方法解析xml报文是按照行解析，
 */
public class DefaultMessageDecodeHandler extends AbstractDecodeHandler {

    private final Logger logger = LoggerFactory.getLogger(DefaultMessageDecodeHandler.class);

    //存放所有报文中解析出来的数据
    private final Map<String, Object> params = new LinkedHashMap<>();
    //在存在list数据时，存放子节点中的数据
    private Map<String, String> currentMap = null;
    //存放list结构的数据
    private List<Map<String, String>> list = null;
    //前一个节点的数据
    private String prevName = null;
    //后一个节点的数据
    private String currentName = null;
    /**
     * 默认当前节点的数据，在取值结束后，该节点的值会赋值为null，读取下一个节点时，再次赋值
     */
    private StringBuffer buffer = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (currentMap == null) {
            currentMap = new LinkedHashMap<>();
        }
        if (StringUtils.isNotBlank(currentName)) {
            prevName = currentName;
        }
        currentName = qName;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String data = new String(ch, start, length);
        if (buffer == null) {
            buffer = new StringBuffer();
        }
        buffer.append(ch, start, length);
    }

    /**
     * 如果一个节点，开始后，读取完数据，立即结束，则表明该节点为数据节点。
     * 例如 <ORD_NO>1231321</ORD_NO>
     *
     * @param uri
     * @param localName
     * @param qName
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(prevName)) {
            Object obj = params.get(prevName);
            if (obj == null) {
                params.put(prevName, currentMap);
            } else {
                if (obj instanceof Map) {
                    Map<String, String> tempMap = (Map<String, String>) obj;
                    list = new ArrayList<>();
                    list.add(tempMap);
                    params.put(prevName, list);
                } else if (obj instanceof List) {
                    list = (List<Map<String, String>>) obj;
                }
                list.add(currentMap);
            }
            currentMap = null;
        }
        if (qName.equals(currentName)) {
            String currentData = StringUtils.trimToEmpty(buffer.toString());
            currentMap.put(currentName, currentData);
            currentName = null;
        }
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> infoMap = (Map<String, Object>) params.get("INFO");
        params.putAll(infoMap);
        params.remove("INFO");
        Object bodyObject = params.get("BODY");
        Map<String, Object> bodyMap = null;
        if (bodyObject instanceof Map) {
            bodyMap = (Map<String, Object>) bodyObject;
        }
        if (bodyMap != null) {
            params.putAll(bodyMap);
            params.remove("BODY");
        }
        return params;
    }
}