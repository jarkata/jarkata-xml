package cn.jarkata.xml.handle;

import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.MessageHandler;
import cn.jarkata.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志输出处理器
 */
public class XmlMessageDataHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(XmlMessageDataHandler.class);
    private final StringBuilder builder = new StringBuilder();

    @Override
    public void start(String qName, XmlElement element) {
        boolean logAble = true;
        if (element != null) {
            logAble = element.isShowLog();
        }
        if (!logAble) {
            return;
        }
        builder.append("\n<");
        builder.append(qName);
        builder.append(">");
    }

    @Override
    public void data(String data, XmlElement element) {
        if (logger.isDebugEnabled()) {
            logger.debug("数据：{}", data);
        }
        boolean logable = true;
        if (element != null) {
            logable = element.isShowLog();
        }
        if (!logable) {
            return;
        }
        data = StringUtils.trimToEmpty(data);
        if (element != null) {
            String pattern = element.getHidePattern();
            data = getData(pattern, data, element);
        }
        builder.append(data);
    }

    @Override
    public void end(String qName, XmlElement element) {
        boolean logable = true;
        if (element != null) {
            logable = element.isShowLog();
        }
        if (!logable) {
            return;
        }
        builder.append("</");
        builder.append(qName);
        builder.append(">");
    }

    /**
     * 输出重新组织后的报文内容
     *
     * @return 返回日志数据
     */
    public String getLogger() {
        return builder.toString();
    }

    private String getData(String pattern, String data, XmlElement element) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        return data;
    }

}