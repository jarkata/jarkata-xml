package cn.jarkata.xml;


import cn.jarkata.commons.utils.FileUtils;
import cn.jarkata.xml.data.DataMap;
import cn.jarkata.xml.data.DefaultDataMap;
import cn.jarkata.xml.data.XmlNode;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XmlFactoryTest {

    @Test
    public void testEncode() throws Exception {
        XmlFactory factory = new XmlFactory();
        DefaultDataMap message = new DefaultDataMap();
        XmlNode xmlNode = new XmlNode("root");
        xmlNode.setValue("testValue");
        Map<String, List<XmlNode>> nodeChildren = xmlNode.getChildren();
        XmlNode child1 = new XmlNode("child1", "23423");
        Map<String, List<XmlNode>> child1Children = child1.getChildren();
        child1Children.put("child11", Arrays.asList(new XmlNode("child11", "data1"), new XmlNode("child13", "data13")));
        child1Children.put("child12", Arrays.asList(new XmlNode("child12", "data2"), new XmlNode("child14", "data24")));
        nodeChildren.put("child1", Collections.singletonList(child1));
        nodeChildren.put("child2", Collections.singletonList(new XmlNode("child2", "23423")));
        message.put(xmlNode);
        String encode = factory.encode(message);
        System.out.println("博文：" + encode);
        DataMap dataValue = factory.decode(new ByteArrayInputStream(encode.getBytes(StandardCharsets.UTF_8)));
        System.out.println(dataValue);
        String value = dataValue.getValue("child11");
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
        DataMap dataValue = factory.decode(arrayInputStream);

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
        DataMap value = factory.decode(arrayInputStream);
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