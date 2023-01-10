package cn.jarkata.xml.data;

import java.util.*;
import java.util.stream.Collectors;

public final class DefaultDataMap extends HashMap<String, List<XmlNode>> implements DataMap {

    @Override
    public void put(XmlNode xmlNode) {
        List<XmlNode> nodeList = new ArrayList<>();
        if (Objects.nonNull(xmlNode)) {
            nodeList.add(xmlNode);
        }
        super.put(xmlNode.getName(), nodeList);
    }

    @Override
    public List<XmlNode> put(String qName, List<XmlNode> nodeList) {
        return super.put(qName, nodeList);
    }

    @Override
    public List<XmlNode> getNodeList() {
        List<XmlNode> nodeList = new ArrayList<>();
        for (List<XmlNode> nodes : super.values()) {
            nodeList.addAll(nodes);
        }
        return nodeList;
    }

    @Override
    public String getValue(String key) {
        XmlNode xmlNode = getNode(key);
        if (Objects.nonNull(xmlNode)) {
            return xmlNode.getValue();
        }
        return null;
    }

    @Override
    public XmlNode getNode(String key) {
        List<XmlNode> nodeList = get(key);
        if (Objects.nonNull(nodeList) && nodeList.size() == 1) {
            return nodeList.get(0);
        }
        throw new IllegalArgumentException(key + " node size more than 1");
    }

    @Override
    public List<String> getValues(String key) {
        return get(key).stream().map(XmlNode::getValue).collect(Collectors.toList());
    }

    @Override
    public List<XmlNode> get(Object key) {
        return super.get(key);
    }

    @Override
    public List<XmlNode> getOrDefault(Object key) {
        return super.getOrDefault(key, new ArrayList<>());
    }

}