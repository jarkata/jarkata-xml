package cn.jarkata.xml.handle;


import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.MessageHandler;
import cn.jarkata.xml.XmlElement;

import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成xml报文
 */
public class XmlMessageEncodeHandler {

    /**
     * 根据模版组织数据
     *
     * @param elementMap
     * @param transformerHandler
     * @param message
     * @throws Exception
     */
    public void handler(Map<String, XmlElement> elementMap, TransformerHandler transformerHandler, Map<String, Object> message, MessageHandler xmlMessageDataHandler) throws Exception {
        for (Map.Entry<String, XmlElement> entry : elementMap.entrySet()) {
            XmlElement element = entry.getValue();
            Map<String, XmlElement> childrenElement = element.getChildren();
            //如果元素为list结构的数据，则先根据转换值获取message中数据集合的大小
            if (element.isListable()) {
                String eleKey = StringUtils.defaultIfBlank(element.getProperty(), element.getName());
                eleKey = StringUtils.trimToEmpty(eleKey);
                Object data = null;
                if (StringUtils.isNotBlank(eleKey)) {
                    data = message.get(eleKey);
                }
                if (data != null && data instanceof List) {
                    @SuppressWarnings("unchecked") List<Map<String, Object>> list = (List<Map<String, Object>>) data;
                    //循环数据集合
                    for (Map<String, Object> childrenMessage : list) {
                        xmlMessageDataHandler.start(element.getName(), element);
                        //在循环内部使用配置的模版输出数据
                        transformerHandler.startElement(null, null, element.getName(), null);
                        getListTemplate(childrenElement, transformerHandler, childrenMessage, xmlMessageDataHandler);
                        transformerHandler.endElement(null, null, element.getName());
                        xmlMessageDataHandler.end(element.getName(), element);
                    }
                } else {
                    xmlMessageDataHandler.start(element.getName(), element);
                    transformerHandler.startElement(null, null, element.getName(), null);
                    getListTemplate(childrenElement, transformerHandler, new HashMap<String, Object>(), xmlMessageDataHandler);
                    transformerHandler.endElement(null, null, element.getName());
                    xmlMessageDataHandler.end(element.getName(), element);
                }
                return;
            }
            //用于输出日志
            xmlMessageDataHandler.start(element.getName(), element);
            //如果元素非list结构的数据，则使用递归调用输出数据结构
            transformerHandler.startElement(null, null, element.getName(), null);
            //子元素为空，则标识该节点为数据节点，将数据输出
            if (childrenElement.isEmpty()) {
                String eleKey = StringUtils.defaultIfBlank(element.getProperty(), element.getName());
                eleKey = StringUtils.trimToEmpty(eleKey);
                Object data = null;
                if (StringUtils.isNotBlank(eleKey)) {
                    data = message.get(eleKey);
                }
                String value = null;
                if (data != null) {
                    value = data.toString();
                }
                value = StringUtils.defaultIfBlank(value, " ");
                value = element.getData(value);
                //该handler用于输出日志
                xmlMessageDataHandler.data(value, element);
                transformerHandler.processingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING, value);
                char[] valueCharArray = value.toCharArray();
                transformerHandler.characters(valueCharArray, 0, valueCharArray.length);
            } else {
                //如果有子节点，则递归调用，输出下一个子节点
                handler(childrenElement, transformerHandler, message, xmlMessageDataHandler);
            }
            transformerHandler.endElement(null, null, element.getName());
            //用于输出日志
            xmlMessageDataHandler.end(element.getName(), element);
        }
    }

    /**
     * 处理list结构的数据
     *
     * @param elementMap
     * @param handler
     * @param message
     * @throws Exception
     */
    private void getListTemplate(Map<String, XmlElement> elementMap, TransformerHandler handler, Map<String, Object> message, MessageHandler messageDataHandler) throws Exception {
        for (Map.Entry<String, XmlElement> entry : elementMap.entrySet()) {

            XmlElement element = entry.getValue();
            messageDataHandler.start(element.getName(), element);
            handler.startElement(null, null, element.getName(), null);
            String eleKey = StringUtils.defaultIfBlank(element.getProperty(), element.getName());
            eleKey = StringUtils.trimToEmpty(eleKey);
            Object data = message.get(eleKey);
            String value = null;
            if (data != null) {
                value = data.toString();
            }
            value = StringUtils.defaultIfBlank(value, " ");
            value = element.getData(value);
            messageDataHandler.data(value, element);
            handler.processingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING, value);
            char[] valueCharArray = value.toCharArray();
            handler.characters(valueCharArray, 0, valueCharArray.length);
            handler.endElement(null, null, element.getName());
            messageDataHandler.end(element.getName(), element);
        }
    }
}