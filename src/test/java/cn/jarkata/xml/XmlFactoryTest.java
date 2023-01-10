package cn.jarkata.xml;


import cn.jarkata.commons.utils.FileUtils;
import cn.jarkata.xml.data.DataValue;
import cn.jarkata.xml.data.XmlNode;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XmlFactoryTest {

    @Test
    public void testEncode() throws Exception {
        XmlFactory factory = new XmlFactory();
        DataValue message = new DataValue();
        XmlNode xmlNode = new XmlNode("root");
        xmlNode.setValue("testValue");
        message.put(xmlNode);
        String encode = factory.encode(message);
        System.out.println(encode);
        DataValue dataValue = factory.decode(new ByteArrayInputStream(encode.getBytes(StandardCharsets.UTF_8)));
        System.out.println(dataValue);
        String value = dataValue.getValue("root");
        System.out.println(value);
    }

    @Test
    public void testDecode() throws Exception {

        InputStream stream = FileUtils.getStream("packet/xml/encode/test-data.xml");
        ByteArrayOutputStream outputStream = FileUtils.toByteStream(stream);
        String message = outputStream.toString(StandardCharsets.UTF_8.name());
        long start = System.currentTimeMillis();
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
        XmlFactory factory = new XmlFactory();
        DataValue dataValue = factory.decode(arrayInputStream);

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

        XmlFactory factory = new XmlFactory();
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
        DataValue value = factory.decode(arrayInputStream);
        System.out.println(value);

        long dur = System.currentTimeMillis() - start;

        System.out.println(dur);

    }

    @Test
    public void testBatchDecode() throws Exception {

        InputStream stream = FileUtils.getStream("packet/xml/encode/test-data.xml");
        ByteArrayOutputStream outputStream = FileUtils.toByteStream(stream);
        String message = outputStream.toString(StandardCharsets.UTF_8.name());
        long start = System.currentTimeMillis();
        XmlFactory factory = new XmlFactory();

        for (int index = 0; index < 1000; index++) {
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
            factory.decode(arrayInputStream);
        }

        long dur = System.currentTimeMillis() - start;

        System.out.println(dur);

    }
}