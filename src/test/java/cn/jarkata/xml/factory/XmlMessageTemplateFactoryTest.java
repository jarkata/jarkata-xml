package cn.jarkata.xml.factory;

import cn.jarkata.xml.MessageTemplateFactory;
import cn.jarkata.xml.XmlElement;
import org.junit.Test;

public class XmlMessageTemplateFactoryTest {

    @Test
    public void testGetElement() {
        MessageTemplateFactory messageTemplateFactory = new XmlMessageTemplateFactory("test.xml");
        XmlElement rootElement = messageTemplateFactory.getRootElement();
        System.out.println(rootElement);
    }

}