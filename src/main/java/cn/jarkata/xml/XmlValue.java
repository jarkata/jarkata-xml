package cn.jarkata.xml;

import java.util.LinkedHashMap;
import java.util.Map;

public class XmlValue {

    private Object value;

    private final Map<String, String> attr = new LinkedHashMap<>();

    public Map<String, XmlValue> children = new LinkedHashMap<>();

    public XmlValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void put(String key, String value) {
        attr.put(key, value);
    }

    public String getAttr(String key) {
        return attr.get(key);
    }

    public void addChildren(String key, XmlValue value) {
        children.put(key, value);
    }

    public XmlValue get(String key) {
        return children.get(key);
    }

    @Override
    public String toString() {
        return "XmlValue{" +
                "value=" + value +
                ", attr=" + attr +
                '}';
    }
}