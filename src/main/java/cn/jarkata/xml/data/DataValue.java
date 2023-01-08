package cn.jarkata.xml.data;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataValue extends HashMap<String, List<XmlNode>> {

    public String getValue(String key) {
        XmlNode xmlNode = getNode(key);
        if (Objects.nonNull(xmlNode)) {
            return xmlNode.getValue();
        }
        return null;
    }

    public XmlNode getNode(String key) {
        List<XmlNode> nodeList = get(key);
        if (Objects.nonNull(nodeList) && nodeList.size() == 1) {
            return nodeList.get(0);
        }
        return null;
    }

    public List<String> getValues(String key) {
        return get(key).stream().map(XmlNode::getValue).collect(Collectors.toList());
    }

    @Override
    public List<XmlNode> get(Object key) {
        return super.get(key);
    }

}