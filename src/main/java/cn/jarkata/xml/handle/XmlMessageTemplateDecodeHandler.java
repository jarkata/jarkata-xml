package cn.jarkata.xml.handle;

import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模版解析处理器
 */
public class XmlMessageTemplateDecodeHandler extends DefaultHandler {

    private final Logger logger = LoggerFactory.getLogger(XmlMessageTemplateDecodeHandler.class);

    private final Map<String, XmlElement> elementMap = new ConcurrentHashMap<>();
    private XmlElement prev = null;
    private XmlElement current = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        int len = attributes.getLength();
        if (len < 1) {
            if (logger.isDebugEnabled()) {
                logger.debug("节点{}没有属性，不做处理", qName);
            }
            return;
        }
        if (current != null) {
            prev = current;
        }
        if (XmlElement.KEY_DATA.equals(qName)) {
            String name = attributes.getValue(XmlElement.KEY_NAME);
            String value = attributes.getValue(XmlElement.KEY_VALUE);
            prev.setData(name, value);
            current = null;
            return;
        }
        String nodeName = attributes.getValue(XmlElement.KEY_NAME);
        if (StringUtils.isBlank(nodeName)) {
            logger.error("元素{}的name属性不能为空", qName);
            throw new IllegalArgumentException("name property not null ");
        }
        nodeName = StringUtils.trimToEmpty(nodeName);
        if (StringUtils.isBlank(nodeName)) {
            logger.error("该节点:{} 没有name属性", qName);
            throw new IllegalArgumentException("该节点" + qName + "没有name属性");
        }
        current = new XmlElement(qName, attributes);
        current.setListable(XmlElement.KEY_LIST.equals(qName));
        elementMap.put(nodeName, current);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (current != null && qName.equals(current.getNodeType())) {
            current = null;
        }
    }

    @Override
    public void endDocument() {
        if (logger.isDebugEnabled()) {
            logger.debug("解析报文模版数据：{}", elementMap);
        }
    }

    public Map<String, XmlElement> getElementMap() {
        return elementMap;
    }

}