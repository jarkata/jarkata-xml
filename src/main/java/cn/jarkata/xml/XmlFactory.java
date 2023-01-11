package cn.jarkata.xml;

import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.data.DataMap;
import cn.jarkata.xml.handle.XmlMsgDecodeHandler;
import cn.jarkata.xml.handle.XmlMsgEncodeHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class XmlFactory implements MessageFactory {

    /**
     * 解析XML数据
     *
     * @param stream xml数据
     * @return 数据
     * @throws Exception 解析数据发生异常
     */
    @Override
    public DataMap decode(InputStream stream) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        XmlMsgDecodeHandler handler = new XmlMsgDecodeHandler(new ArrayList<>());
        saxParser.parse(stream, handler);
        return handler.getDataValue();
    }

    /**
     * 生成XML数据
     *
     * @param message 报文数据
     * @return XML豹纹
     * @throws Exception 异常
     */
    @Override
    public String encode(DataMap message) throws Exception {

        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler transformerHandler = factory.newTransformerHandler();
        Transformer transformer = transformerHandler.getTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outputStream);
        transformerHandler.setResult(result);
        //文档开头
        transformerHandler.startDocument();
        XmlMsgEncodeHandler msgEncodeHandler = new XmlMsgEncodeHandler(transformerHandler);
        msgEncodeHandler.encodeXmlMsg(message);
        //生成xml文件结束
        transformerHandler.endDocument();
        String xml = outputStream.toString(StandardCharsets.UTF_8.name());
        xml = StringUtils.trimToEmpty(xml);
        return xml;
    }

}