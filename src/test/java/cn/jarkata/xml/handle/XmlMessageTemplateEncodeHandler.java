package cn.jarkata.xml.handle;

import cn.jarkata.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 模版解析处理器
 */
public class XmlMessageTemplateEncodeHandler extends DefaultHandler {

    private final Logger logger = LoggerFactory.getLogger(XmlMessageTemplateEncodeHandler.class);

    private XmlElement rootElement = null;
    private XmlElement current = null;
    private XmlElement prevElement = null;
    private final Deque<XmlElement> deque = new LinkedBlockingDeque<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (current != null) {
            prevElement = current;
        }
        if (XmlElement.KEY_DATA.equals(qName)) {
            current = null;
            String name = attributes.getValue(XmlElement.KEY_NAME);
            String value = attributes.getValue(XmlElement.KEY_VALUE);
            prevElement.setData(name, value);
            return;
        }
        current = new XmlElement(qName, attributes);
        if (XmlElement.KEY_LIST.equals(qName)) {
            current.setListable(true);
        } else {
            current.setListable(false);
        }
        deque.add(current);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (prevElement.getNodeType().equals(qName)) {
            XmlElement currentEnd = deque.pollLast();
            if (qName.equals(XmlElement.KEY_RESPONSE) || qName.equals(XmlElement.KEY_REQUEST)) {
                rootElement = currentEnd;
                prevElement = null;
                return;
            }
            //将队列中添加的元素移除
            prevElement = deque.pollLast();
            prevElement.getChildren().put(currentEnd.getName(), currentEnd);
            deque.add(prevElement);
            return;
        }
        if (current == null) {
            logger.debug("元素：{} 未做转换", qName);
            return;
        }
        if (current.getNodeType().equals(qName)) {
            prevElement.getChildren().put(current.getName(), current);
            current = null;
            //将队列中添加的元素移除
            XmlElement element = deque.pollLast();
            if (logger.isDebugEnabled()) {
                logger.debug("出队元素={}", element);
            }
        }
    }

    @Override
    public void endDocument() {
        if (logger.isDebugEnabled()) {
            logger.debug("返回报文模版结构：{}", rootElement);
        }
    }

    public XmlElement getRootElement() {
        return rootElement;
    }

}