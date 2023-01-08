package cn.jarkata.xml;


import cn.jarkata.commons.utils.FileUtils;
import cn.jarkata.xml.data.DataValue;
import cn.jarkata.xml.data.XmlNode;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XmlFactoryTest {

    @Test
    public void testDecode() throws ParserConfigurationException, IOException, SAXException {

        InputStream stream = FileUtils.getStream("packet/xml/encode/test-data.xml");
        ByteArrayOutputStream outputStream = FileUtils.toByteStream(stream);
        String message = outputStream.toString(StandardCharsets.UTF_8.name());
        long start = System.currentTimeMillis();
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
        DataValue dataValue = XmlFactory.decode(arrayInputStream);

        long dur = System.currentTimeMillis() - start;
        System.out.println(dataValue);
        String merchantId = dataValue.getValue("MERCHANT_ID");
        System.out.println(merchantId);
        List<String> values = dataValue.getValues("element");
        System.out.println(values);
        System.out.println(dur);

    }

    @Test
    public void testServersDecode() throws Exception {

        InputStream stream = FileUtils.getStream("packet/xml/encode/servers.xml");
        ByteArrayOutputStream outputStream = FileUtils.toByteStream(stream);
        String message = outputStream.toString(StandardCharsets.UTF_8.name());
        long start = System.currentTimeMillis();


        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
        DataValue value = XmlFactory.decode(arrayInputStream);
        System.out.println(value);

        long dur = System.currentTimeMillis() - start;

        System.out.println(dur);

    }

    @Test
    public void testBatchDecode() throws ParserConfigurationException, IOException, SAXException {

        InputStream stream = FileUtils.getStream("packet/xml/encode/test-data.xml");
        ByteArrayOutputStream outputStream = FileUtils.toByteStream(stream);
        String message = outputStream.toString(StandardCharsets.UTF_8.name());
        long start = System.currentTimeMillis();


        for (int index = 0; index < 1000; index++) {
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
            XmlFactory.decode(arrayInputStream);
        }

        long dur = System.currentTimeMillis() - start;

        System.out.println(dur);

    }
}