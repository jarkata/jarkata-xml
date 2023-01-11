package cn.jarkata.xml.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class XmlNode {
    private String name;
    private String value;
    private String nodeType;
    private final Map<String, List<XmlNode>> children = new LinkedHashMap<>();

    private final Map<String, String> attr = new LinkedHashMap<>();

    public XmlNode(String name) {
        this.name = name;
    }

    public XmlNode(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setAttr(String key, String value) {
        attr.put(key, value);
    }

    public Map<String, String> getAttr() {
        return attr;
    }

    public Map<String, List<XmlNode>> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "XmlNode{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", attr=" + attr +
                '}';
    }
}