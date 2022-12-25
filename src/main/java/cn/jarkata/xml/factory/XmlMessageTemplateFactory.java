package cn.jarkata.xml.factory;

import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.CacheTemplate;
import cn.jarkata.xml.MessageTemplateFactory;
import cn.jarkata.xml.XmlElement;
import cn.jarkata.xml.handle.XmlMessageTemplateDecodeHandler;
import cn.jarkata.xml.handle.XmlMessageTemplateEncodeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Map;

/**
 * XML报文模版解析处理
 */
public class XmlMessageTemplateFactory implements MessageTemplateFactory {

    private final Logger logger = LoggerFactory.getLogger(XmlMessageTemplateFactory.class);
    /**
     * 模版地址
     */
    private String templatePath;

    public XmlMessageTemplateFactory(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public XmlElement getElement(String key) {
        if (StringUtils.isBlank(key)) {
            logger.error("获取元素：Key={}", key);
            return null;
        }
        Map<String, XmlElement> elementMap = CacheTemplate.get(templatePath);
        if (elementMap != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("缓存中元素信息：查找的key={},原map={}", key, elementMap);
            }
            XmlElement element = elementMap.get(key);
            if (logger.isDebugEnabled()) {
                logger.debug("使用缓存：{}={}", key, element);
            }
            return element;
        }
        XmlMessageTemplateDecodeHandler handler = getTemplateDecodeHandler();
        if (handler == null) {
            logger.error("根据模版地址：{} 未找到模版信息", templatePath);
            throw new IllegalArgumentException(" Based [" + templatePath + "] Not Found Template ");
        }
        elementMap = handler.getElementMap();
        if (elementMap != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("待转换的元素信息：{}", elementMap);
            }
            CacheTemplate.put(templatePath, elementMap);
            XmlElement element = elementMap.get(key);
            if (logger.isDebugEnabled()) {
                logger.debug("未使用缓存：{}={}", key, element);
            }
            return element;
        } else {
            logger.error("根据模版地址：{} 获取模版信息失败", templatePath);
        }
        return null;
    }

    @Override
    public XmlElement getRootElement() {
        XmlMessageTemplateEncodeHandler handler = getTemplateEncodeHandler();
        if (handler == null) {
            logger.error("根据模版地址：{} 未找到模版信息", templatePath);
            throw new IllegalArgumentException(" Based [" + templatePath + "] Not Found Template ");
        }
        XmlElement rootElement = handler.getRootElement();
        if (rootElement != null) {
        } else {
            logger.error("根据模版地址：{} 获取模版信息失败", templatePath);
        }
        return rootElement;
    }

    @Override
    public boolean isTemplateExist() {
        if (StringUtils.isBlank(templatePath)) {
            return false;
        }
        InputStream inputStream = getInputStream(templatePath);
        if (inputStream != null) {
            return true;
        } else {
            logger.info("模版文件[{}]不存在", templatePath);
        }
        return false;
    }

    private InputStream getInputStream(String templatePath) {
        if (StringUtils.isBlank(templatePath)) {
            return null;
        }
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
        if (inputStream == null) {
            inputStream = XmlMessageTemplateFactory.class.getClassLoader().getResourceAsStream(templatePath);
        }
        return inputStream;
    }

    private XmlMessageTemplateDecodeHandler getTemplateDecodeHandler() {
        InputStream inputStream = getInputStream(templatePath);
        if (inputStream == null) {
            logger.warn("模版文件不存在，模版地址为：{}", templatePath);
            return null;
        }
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XmlMessageTemplateDecodeHandler handler = new XmlMessageTemplateDecodeHandler();
            saxParser.parse(inputStream, handler);
            return handler;
        } catch (Throwable ex) {
            logger.error("解析报文模版错误", ex);
        }
        return null;
    }

    /**
     * 解析模版
     */
    private XmlMessageTemplateEncodeHandler getTemplateEncodeHandler() {
        InputStream inputStream = getInputStream(templatePath);
        if (inputStream == null) {
            logger.warn("模版文件不存在，模版地址为：{}", templatePath);
            return null;
        }
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XmlMessageTemplateEncodeHandler handler = new XmlMessageTemplateEncodeHandler();
            saxParser.parse(inputStream, handler);
            return handler;
        } catch (Throwable ex) {
            logger.error("解析报文模版错误", ex);
        }
        return null;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }
}