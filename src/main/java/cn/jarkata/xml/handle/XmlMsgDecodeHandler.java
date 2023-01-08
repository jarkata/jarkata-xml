package cn.jarkata.xml.handle;

import cn.jarkata.xml.data.DataValue;
import cn.jarkata.xml.data.XmlNode;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class XmlMsgDecodeHandler extends DefaultHandler {

    private final DataValue dataValue = new DataValue();

    private StringBuilder builder;

    private final Deque<XmlNode> deque = new ConcurrentLinkedDeque<>();

    private String startQName;

    private List<String> ignoreElement = new ArrayList<>();

    public XmlMsgDecodeHandler() {
        dataValue.clear();
    }

    public XmlMsgDecodeHandler(List<String> ignoreElement) {
        this();
        this.ignoreElement = ignoreElement;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        XmlNode node = new XmlNode(qName);
        int length = attributes.getLength();
        for (int index = 0; index < length; index++) {
            String attributesValue = attributes.getValue(index);
            String attributesQName = attributes.getQName(index);
            node.setAttr(attributesQName, attributesValue);
        }
        deque.push(node);
        startQName = qName;
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
            if (xmlNode.getName().equals(qName) && startQName.equals(qName)) {
                String dataVal = builder.toString().trim();
                xmlNode.setValue(dataVal);
                List<XmlNode> nodeList = dataValue.get(qName);
                if (Objects.isNull(nodeList)) {
                    ArrayList<XmlNode> data = new ArrayList<>();
                    data.add(xmlNode);
                    dataValue.put(qName, data);
                    return;
                }
                nodeList.add(xmlNode);
                dataValue.put(qName, nodeList);
            }
        } finally {
            builder = null;
        }

    }


    public DataValue getDataValue() {
        return dataValue;
    }
}