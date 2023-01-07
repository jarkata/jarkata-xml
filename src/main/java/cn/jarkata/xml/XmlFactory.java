package cn.jarkata.xml;

import cn.jarkata.xml.data.DataValue;
import cn.jarkata.xml.handle.DecodeXmlHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

public class XmlFactory {

    public static DataValue decode(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DecodeXmlHandler handler = new DecodeXmlHandler();
        saxParser.parse(stream, handler);
        return handler.getDataValue();
    }

}