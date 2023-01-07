package cn.jarkata.xml.handle;

import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.MessageHandler;
import cn.jarkata.xml.MessageTemplateFactory;
import cn.jarkata.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 解码报文
 */
public class XmlMessageDecodeHandler extends AbstractDecodeHandler {

    private final Logger logger = LoggerFactory.getLogger(XmlMessageDecodeHandler.class);
    private final MessageTemplateFactory messageTemplateFactory;
    private final MessageHandler messageDataHandler;
    /**
     * 存放list结构的数据
     */
    private List<Map<String, Object>> list = null;
    /**
     * 存放所有报文中解析出来的数据
     */
    private final Map<String, Object> objectMap = new ConcurrentHashMap<>();
    private Map<String, Object> currentMap = null;
    /**
     * 存放上一个元素，真正存放的是离数据及元素最近的上一级元素。
     */
    private XmlElement prevElement;
    /**
     * 获取当前节点的元素信息
     */
    private XmlElement currentElement;
    /**
     * 当前节点的数据
     */
    private StringBuffer currentBufferData = null;
    /**
     * 在ignoreList中的元素类型，不做为map的key。直接将其包含的数据放入root map
     */
    private final List<String> ignoreList = new ArrayList<>();

    {
        ignoreList.add(XmlElement.KEY_BODY);
        ignoreList.add(XmlElement.KEY_INFO);
        ignoreList.add(XmlElement.KEY_WRAP);
    }

    public XmlMessageDecodeHandler(MessageTemplateFactory messageTemplateFactory, MessageHandler messageDataHandler) {
        this.messageTemplateFactory = messageTemplateFactory;
        this.messageDataHandler = messageDataHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (currentMap == null) {
            currentMap = new ConcurrentHashMap<>(8);
        }
        if (currentElement != null) {
            prevElement = currentElement;
        }
        currentElement = messageTemplateFactory.getElement(qName);
        if (currentElement == null) {
            logger.debug("该元素{}未配置转换信息", qName);
        }
        if (currentElement != null && currentElement.isListable()) {
            objectMap.putAll(currentMap);
            currentMap = new ConcurrentHashMap<>();
        }
        messageDataHandler.start(qName, currentElement);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (currentBufferData == null) {
            currentBufferData = new StringBuffer();
        }
        currentBufferData.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String currentData = null;
        if (currentBufferData != null) {
            currentData = StringUtils.trimToEmpty(currentBufferData.toString());
        }
        //输出报文的数据
        messageDataHandler.data(currentData, currentElement);
        currentBufferData = null;
        String prevName = null;
        if (prevElement != null) {
            prevName = prevElement.getName();
        }
        //当遇到结束标签时会发生
        if (StringUtils.isNotBlank(qName) && qName.equals(prevName)) {
            //输出报文结束标签
            messageDataHandler.end(qName, prevElement);
            //在ignoreList中包含的标签类型，将其内包含的数据直接放入根map中
            if (ignoreList.contains(prevElement.getNodeType())) {
                objectMap.putAll(currentMap);
                currentMap = null;
                return;
            }
            String eleKey = StringUtils.defaultString(prevElement.getProperty(), prevName);
            eleKey = StringUtils.trimToEmpty(eleKey);
            //如果上一个标签非list，则直接将其值放入map
            if (!prevElement.isListable()) {
                objectMap.put(eleKey, currentMap);
                currentMap = null;
                return;
            }
            //处理list的情况
            Object obj = objectMap.get(prevName);
            if (obj == null) {
                list = new ArrayList<>();
            }
            if (obj instanceof List) {
                list = (List<Map<String, Object>>) obj;
            }
            list.add(currentMap);
            objectMap.put(eleKey, list);
            currentMap = null;
            return;
        }
        //输出报文结束标签
        messageDataHandler.end(qName, currentElement);
        if (currentElement == null) {
            logger.debug("当前元素节点信息为空；当前QName={}", qName);
            return;
        }
        String elementName = currentElement.getName();
        if (StringUtils.isNotBlank(qName) && qName.equals(elementName)) {
            String eleKey = StringUtils.defaultString(currentElement.getProperty(), elementName);
            eleKey = StringUtils.trimToEmpty(eleKey);
            if (StringUtils.isNotBlank(currentData)) {
                currentData = currentElement.getData(currentData);
                currentMap.put(eleKey, currentData);
            }
            currentElement = null;
        }
    }

    @Override
    public Map<String, Object> getData() {
        if (logger.isDebugEnabled()) {
            logger.debug("解析出的数据：{}", objectMap);
        }
        return objectMap;
    }
}