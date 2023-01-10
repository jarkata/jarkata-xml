package cn.jarkata.xml.data;

import java.util.List;

public interface DataMap {

    void put(XmlNode xmlNode);

    List<XmlNode> put(String qName, List<XmlNode> xmlNode);

    List<XmlNode> getNodeList();

    void clear();

    String getValue(String key);

    XmlNode getNode(String key);

    List<String> getValues(String key);

    List<XmlNode> get(Object key);

    List<XmlNode> getOrDefault(Object key);

}