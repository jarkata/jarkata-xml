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

    private XmlNode currentStartNode;

    private final Deque<XmlNode> deque = new ConcurrentLinkedDeque<>();

    private final List<String> ignoreElement;

    public XmlMsgDecodeHandler(List<String> ignoreElement) {
        dataValue.clear();
        this.ignoreElement = ignoreElement;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        XmlNode currentNode = new XmlNode(qName);
        int length = attributes.getLength();
        for (int index = 0; index < length; index++) {
            String attributesValue = attributes.getValue(index);
            String attributesQName = attributes.getQName(index);
            String attributesType = attributes.getType(index);
            currentNode.setNodeType(attributesType);
            currentNode.setAttr(attributesQName, attributesValue);
        }
        currentStartNode = currentNode;
        deque.push(currentNode);
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

            XmlNode prevNode = deque.pop();
            if (ignoreElement.contains(qName)) {
                return;
            }
            if (prevNode.getName().equals(qName)) {
                String dataVal = builder.toString().trim();
                prevNode.setValue(dataVal);
                List<XmlNode> nodeList = dataValue.getOrDefault(qName);
                if (Objects.isNull(currentStartNode)) {
                    List<XmlNode> valueNodeList = dataValue.getNodeList();
                    Map<String, List<XmlNode>> nodeChildren = prevNode.getChildren();
                    nodeChildren.put(qName, valueNodeList);
                    dataValue.clear();
                }
                nodeList.add(prevNode);
                dataValue.put(qName, nodeList);

                currentStartNode = null;
            }
        } finally {
            builder = null;
        }

    }


    public DataMap getDataValue() {
        return dataValue;
    }
}