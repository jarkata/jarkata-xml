package cn.jarkata.xml.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class XmlNode {

    private String name;
    private String value;
    private final Map<String, String> attr = new LinkedHashMap<>();

    public XmlNode(String name) {
        this.name = name;
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

    public void setAttr(String key, String value) {
        attr.put(key, value);
    }

    public Map<String, String> getAttr() {
        return attr;
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