package cn.jarkata.xml;

import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.data.DataMap;
import cn.jarkata.xml.data.XmlNode;
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

    public DataMap decode(InputStream stream) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        XmlMsgDecodeHandler handler = new XmlMsgDecodeHandler(new ArrayList<>());
        saxParser.parse(stream, handler);
        return handler.getDataValue();
    }

    @Override
    public String encode(DataMap message) throws Exception {

        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler transformerHandler = factory.newTransformerHandler();
        Transformer transformer = transformerHandler.getTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outputStream);
        transformerHandler.setResult(result);
        transformerHandler.startDocument();

        XmlMsgEncodeHandler msgEncodeHandler = new XmlMsgEncodeHandler(transformerHandler);

        for (XmlNode xmlNode : message.getNodeList()) {
            msgEncodeHandler.buildPerElement(xmlNode, xmlNode.getValue());
        }

        //生成xml文件结束
        transformerHandler.endDocument();
        String xml = outputStream.toString(StandardCharsets.UTF_8.name());
        xml = StringUtils.trimToEmpty(xml);
        return xml;
    }

}