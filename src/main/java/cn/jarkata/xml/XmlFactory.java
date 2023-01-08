package cn.jarkata.xml;

import cn.jarkata.xml.data.DataValue;
import cn.jarkata.xml.handle.XmlMsgDecodeHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class XmlFactory implements MessageFactory {

    public DataValue decode(InputStream stream) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        XmlMsgDecodeHandler handler = new XmlMsgDecodeHandler(new ArrayList<>());
        saxParser.parse(stream, handler);
        return handler.getDataValue();
    }

    @Override
    public String encode(Map<String, Object> message) {
        return null;
    }

}