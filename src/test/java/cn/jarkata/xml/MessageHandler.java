package cn.jarkata.xml;

/**
 * 处理报文元素
 */
public interface MessageHandler {
    /**
     * 处理开始元素
     *
     * @param qName      节点名称
     * @param xmlElement 节点元素信息
     */
    void start(String qName, XmlElement xmlElement);

    /**
     * 处理数据
     *
     * @param data       节点数据
     * @param xmlElement 节点元素信息
     */
    void data(String data, XmlElement xmlElement);

    /**
     * 处理结束元素
     *
     * @param qName      节点名称
     * @param xmlElement 节点元素
     */
    void end(String qName, XmlElement xmlElement);
}