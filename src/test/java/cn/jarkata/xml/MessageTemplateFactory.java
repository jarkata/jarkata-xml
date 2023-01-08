package cn.jarkata.xml;

/**
 * 报文解析模版
 */
public interface MessageTemplateFactory {

    /**
     * 根据元素名称获取元素信息
     *
     * @param elementName
     * @return
     */
    XmlElement getElement(String elementName);

    /**
     * 获取根元素
     *
     * @return
     */
    XmlElement getRootElement();

    /**
     * 判断模版是否存在
     *
     * @return
     */
    boolean isTemplateExist();
}