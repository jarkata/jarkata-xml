package cn.jarkata.xml.handle;

import cn.jarkata.xml.data.DataMap;
import cn.jarkata.xml.data.DefaultDataMap;
import cn.jarkata.xml.data.XmlNode;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class XmlMsgDecodeHandler extends DefaultHandler {

    private final DataMap dataValue = new DefaultDataMap();

    private StringBuilder builder;

    private final Deque<XmlNode> deque = new ConcurrentLinkedDeque<>();

    private final List<String> ignoreElement;

    public XmlMsgDecodeHandler() {
        this(new ArrayList<>(0));
    }

    public XmlMsgDecodeHandler(List<String> ignoreElement) {
        dataValue.clear();
        this.ignoreElement = ignoreElement;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        XmlNode node = new XmlNode(qName);
        int length = attributes.getLength();
        for (int index = 0; index < length; index++) {
            String attributesValue = attributes.getValue(index);
            String attributesQName = attributes.getQName(index);
            String attributesType = attributes.getType(index);
            node.setNodeType(attributesType);
            node.setAttr(attributesQName, attributesValue);
        }
        deque.push(node);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (Objects.isNull(builder)) {
            builder = new StringBuilder();
        }
        builder.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        try {
            XmlNode xmlNode = deque.pop();
            if (ignoreElement.contains(qName)) {
                return;
            }
            if (xmlNode.getName().equals(qName)) {
                String dataVal = builder.toString().trim();
                xmlNode.setValue(dataVal);
                List<XmlNode> nodeList = dataValue.getOrDefault(qName);
                nodeList.add(xmlNode);
                dataValue.put(qName, nodeList);
            }
        } finally {
            builder = null;
        }

    }


    public DataMap getDataValue() {
        return dataValue;
    }
}