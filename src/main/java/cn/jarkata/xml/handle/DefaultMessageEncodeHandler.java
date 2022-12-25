package cn.jarkata.xml.handle;

import cn.jarkata.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 默认处理报文
 */
public class DefaultMessageEncodeHandler {

    private final Logger logger = LoggerFactory.getLogger(DefaultMessageEncodeHandler.class);
    private final static List<String> infoKey = new ArrayList<String>();

    /*
     * 响应报文头
     */
    static {

    }

    /**
     * 处理handler
     *
     * @param handler
     * @param message
     */
    public void handler(TransformerHandler handler, Map<String, Object> message) throws Exception {
        if (message == null) {
            logger.error("报文消息为空");
            return;
        }
        //组织报文头
        handler.startElement(null, null, "message", null);
        handler.startElement(null, null, "header", null);
        toInfoXml(handler, message);
        handler.endElement(null, null, "header");
        //组织报文体
        handler.startElement(null, null, "body", null);
        toBodyXml(handler, message);
        handler.endElement(null, null, "body");
        handler.endElement(null, null, "message");
    }

    /**
     * i组织报文头
     *
     * @param handler
     * @param message
     * @throws Exception
     */
    private void toInfoXml(TransformerHandler handler, Map<String, Object> message) throws Exception {
        for (Map.Entry<String, Object> entry : message.entrySet()) {
            if (!infoKey.contains(entry.getKey())) {
                continue;
            }
            handler.startElement(null, null, entry.getKey(), null);
            Object messageValue = entry.getValue();
            String value = null;
            if (messageValue != null) {
                value = messageValue.toString();
            }
            value = StringUtils.defaultIfBlank(value, " ");
            handler.processingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING, value);
            char[] valueCharArray = value.toCharArray();
            handler.characters(valueCharArray, 0, valueCharArray.length);
            handler.endElement(null, null, entry.getKey());
        }
    }

    /**
     * 组织报文体
     * 只支持一层list，不支持在list中嵌套list
     *
     * @param handler
     * @param message
     * @throws Exception
     */
    private void toBodyXml(TransformerHandler handler, Map<String, Object> message) throws Exception {
        //循环输出报文
        for (Map.Entry<String, Object> entry : message.entrySet()) {
            Object messageValue = entry.getValue();
            if (messageValue == null) {
                continue;
            }
            if (infoKey.contains(entry.getKey())) {
                continue;
            }
            //如果map中的value为list，则循环map展示。
            if (messageValue instanceof List) {
                List<Map<String, Object>> messageList = (List<Map<String, Object>>) messageValue;
                getListTemplate(handler, messageList, entry.getKey());

                //如果数据为map，则标签数据嵌套，需要递归调用
            } else if (messageValue instanceof Map) {
                Map<String, Object> subMessage = (Map<String, Object>) messageValue;
                handler.startElement(null, null, entry.getKey(), null);
                toBodyXml(handler, subMessage);
                handler.endElement(null, null, entry.getKey());
            } else {
                String value = messageValue.toString();
                value = StringUtils.defaultIfBlank(value, " ");
                handler.processingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING, value);
                handler.startElement(null, null, entry.getKey(), null);
                char[] valueCharArray = value.toCharArray();
                handler.characters(valueCharArray, 0, valueCharArray.length);
                handler.endElement(null, null, entry.getKey());
            }
        }
    }

    /**
     * 获取list结构的模版
     *
     * @param handler
     * @param messageList
     * @param key
     * @throws Exception
     */
    private void getListTemplate(TransformerHandler handler, List<Map<String, Object>> messageList, String key) throws Exception {
        for (Map<String, Object> messageMap : messageList) {
            handler.startElement(null, null, key, null);
            for (Map.Entry<String, Object> entry : messageMap.entrySet()) {
                Object messageValue = entry.getValue();
                handler.startElement(null, null, entry.getKey(), null);
                String value = null;
                if (messageValue != null) {
                    value = messageValue.toString();
                }
                value = StringUtils.defaultIfBlank(value, " ");
                handler.processingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING, value);
                char[] valueCharArray = value.toCharArray();
                handler.characters(valueCharArray, 0, valueCharArray.length);
                handler.endElement(null, null, entry.getKey());
            }
            handler.endElement(null, null, key);
        }
    }
}