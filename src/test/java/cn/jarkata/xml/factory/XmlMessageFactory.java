package cn.jarkata.xml.factory;

import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.MessageFactory;
import cn.jarkata.xml.MessageHandler;
import cn.jarkata.xml.MessageTemplateFactory;
import cn.jarkata.xml.XmlElement;
import cn.jarkata.xml.handle.XmlMessageDecodeHandler;
import cn.jarkata.xml.handle.XmlMessageEncodeHandler;
import cn.jarkata.xml.handle.AbstractDecodeHandler;
import cn.jarkata.xml.handle.XmlMessageDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 解析报文
 */
public class XmlMessageFactory implements MessageFactory {

    private final Logger logger = LoggerFactory.getLogger(XmlMessageFactory.class);
    /**
     * 默认的xml声明
     */
    private static final String XML_DEFAULT_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    private final static String URL_SPLIT = "/";
    private final static String DECODE_DEFAULT_TEMPLATE_DIRECTORY = "decode";
    private final static String ENCODE_DEFAULT_TEMPLATE_DIRECTORY = "encode";
    private final static String MESSAGE_TEMPLATE_EXT = ".xml";

    /**
     * 解码报文
     *
     * @param message     请求的xml报文
     * @param templateUrl 请求的地址
     * @return 返回解析后map结构的数据
     */
    @Override
    public Map<String, Object> decode(String message, String templateUrl) {
        return decodeRequest(message, templateUrl, DECODE_DEFAULT_TEMPLATE_DIRECTORY);
    }

    /**
     * 解码报文
     *
     * @param message     报文数据
     * @param messageType 使用表示decode或encode表示
     * @param templateUrl 模版地址
     * @return 解析之后的数据
     */
    @Override
    public Map<String, Object> decode(String message, String templateUrl, String messageType) {
        return decodeRequest(message, templateUrl, messageType);
    }

    /**
     * 生成报文
     *
     * @param message     生成报文的原始数据
     * @param templateUrl 模版文件的路径
     * @return 返回生成的xml报文
     */
    @Override
    public String encode(Map<String, Object> message, String templateUrl) {
        return encode(message, templateUrl, ENCODE_DEFAULT_TEMPLATE_DIRECTORY);
    }

    /**
     * 编码报文
     *
     * @param message
     * @param templateUrl
     * @return
     */
    @Override
    public String encode(Map<String, Object> message, String templateUrl, String messageType) {
        if (message == null) {
            logger.error("报文消息为空");
            return "";
        }
        try {
            return encodeResponse(message, templateUrl, messageType);
        } catch (Throwable e) {
            logger.error("生成xml报文报错", e);
        }
        return "";
    }

    /**
     * 解码请求信息
     *
     * @param inputXml    请求的xml报文
     * @param templateUrl 请求报文的模版文件路径
     * @return 返回解析后的xml报文
     */
    private Map<String, Object> decodeRequest(String inputXml, String templateUrl, String messageType) {
        try {
            String templatePath = getMessagePath(templateUrl, messageType);
            InputStream inputStream = toInputStream(inputXml);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            AbstractDecodeHandler handler;
            MessageHandler messageDataHandler = new XmlMessageDataHandler();
            MessageTemplateFactory xmlMessageTemplateFactory = new XmlMessageTemplateFactory(templatePath);
            if (xmlMessageTemplateFactory.isTemplateExist()) {
                handler = new XmlMessageDecodeHandler(xmlMessageTemplateFactory, messageDataHandler);
            } else {
                throw new IllegalArgumentException("模版不存在");
            }
            if (inputStream != null) {
                saxParser.parse(inputStream, handler);
            }
            try {
                logger.info("商户请求报文：{}", messageDataHandler);
            } catch (Throwable ex) {
                logger.warn("输出报文日志错误：{}", ex.getMessage());
            }
            return handler.getData();
        } catch (Throwable ex) {
            logger.error("解析报文错误：", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 编码响应信息
     *
     * @param message     生成报文所需的数据
     * @param templateUrl 模版文件的路径
     * @param messageType 模版文件的路径
     * @return 生成的xml报文
     */
    private String encodeResponse(Map<String, Object> message, String templateUrl, String messageType) throws Exception {
        String templatePath = getMessagePath(templateUrl, messageType);
        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler transformerHandler = factory.newTransformerHandler();
        Transformer transformer = transformerHandler.getTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Result result = new StreamResult(outputStream);
        transformerHandler.setResult(result);
        transformerHandler.startDocument();
        MessageTemplateFactory xmlMessageTemplateFactory = new XmlMessageTemplateFactory(templatePath);
        //开始生成xml文件
        boolean exist = xmlMessageTemplateFactory.isTemplateExist();
        if (exist) {
            XmlElement root = xmlMessageTemplateFactory.getRootElement();
            XmlMessageDataHandler messageDataHandler = new XmlMessageDataHandler();
            messageDataHandler.start(root.getName(), root);
            transformerHandler.startElement(null, null, root.getName(), null);
            XmlMessageEncodeHandler encodeHandler = new XmlMessageEncodeHandler();
            encodeHandler.handler(root.getChildren(), transformerHandler, message, messageDataHandler);
            transformerHandler.endElement(null, null, root.getName());
            messageDataHandler.end(root.getName(), root);
            logger.info("生成的xml报文：{}", messageDataHandler.getLogger());
        } else {
            throw new IllegalArgumentException("模版不存在");
        }
        //生成xml文件结束
        transformerHandler.endDocument();
        String xml = outputStream.toString(StandardCharsets.UTF_8.name());
        xml = StringUtils.trimToEmpty(xml);
        if (logger.isDebugEnabled()) {
            logger.debug("生成的xml报文：{}", xml);
        }
        return xml.replaceAll("> <", "><");
    }

    /**
     * 响应报文模版的路径
     *
     * @param url 请求地址
     * @return 返回响应报文的路径
     */
    private String getEncodePath(String url) {
        return getMessagePath(url, ENCODE_DEFAULT_TEMPLATE_DIRECTORY);
    }

    /**
     * 构建模版文件的路径
     *
     * @param url 请求地址
     * @return 请求报文的模版路径
     */
    private String getDecodePath(String url) {
        return getMessagePath(url, DECODE_DEFAULT_TEMPLATE_DIRECTORY);
    }

    /**
     * 将xml报文的字符串转换为输入流
     *
     * @param inputXml 输入的xml报文
     */
    private InputStream toInputStream(String inputXml) {
        if (StringUtils.isBlank(inputXml)) {
            logger.error("报文体内容为空：{}", inputXml);
            return null;
        }
        //预处理xml
        int index = inputXml.lastIndexOf("?>");
        if (index > 0) {
            inputXml = inputXml.substring(index + 2);
            inputXml = XML_DEFAULT_HEAD + inputXml;
        }
        return new ByteArrayInputStream(inputXml.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 对请求地址做URL处理，trim掉url前后的"/"字符
     *
     * @param url 请求的地址
     * @return 处理后的url
     */
    private String trimUrl(String url) {
        url = StringUtils.trimToEmpty(url);
        if (url.startsWith(URL_SPLIT)) {
            url = url.substring(1);
        }
        if (url.endsWith(URL_SPLIT)) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * 获取报文路径
     *
     * @param url         请求地址
     * @param messageType 类型 ,decode:请求报文，encode：响应报文
     * @return 字符串，构建好的路径
     */
    private String getMessagePath(String url, String messageType) {
        if (StringUtils.isBlank(url)) {
            logger.error("请求地址为空");
            return null;
        }
        url = trimUrl(url);
        StringBuilder builder = new StringBuilder();
        builder.append("packet/xml/");
        builder.append(messageType);
        builder.append(URL_SPLIT);
        builder.append(url.replace(URL_SPLIT, "_"));
        builder.append(MESSAGE_TEMPLATE_EXT);
        if (logger.isDebugEnabled()) {
            logger.debug("构建请求报文解析路径：{}", builder);
        }
        return builder.toString();
    }
}