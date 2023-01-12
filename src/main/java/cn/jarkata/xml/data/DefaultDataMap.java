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
        Collection<List<XmlNode>> lists = super.values();
        for (List<XmlNode> nodes : lists) {
            nodeList.addAll(nodes);
        }
        return nodeList;
    }

    /**
     * @param key 按照XPath的风格赋值key
     * @return 节点的值
     */
    @Override
    public String getValue(String key) {
        XmlNode xmlNode = getNodeByXpath(key);
        if (Objects.nonNull(xmlNode)) {
            return xmlNode.getValue();
        }
        return null;
    }

    private XmlNode getNodeByXpath(String key) {
        String[] split = key.split("/");
        XmlNode node = getNode(split[1]);
        for (int index = 2; index < split.length; index++) {
            String subKey = split[index];
            List<XmlNode> nodeList = Objects.requireNonNull(node).getChildren().get(subKey);
            for (XmlNode xmlNode : nodeList) {
                node = xmlNode;
            }
        }
        return node;
    }

    @Override
    public XmlNode getNode(String key) {
        List<XmlNode> nodeList = get(key);
        if (Objects.nonNull(nodeList) && nodeList.size() == 1) {
            return nodeList.get(0);
        }
        return null;
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