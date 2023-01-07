package cn.jarkata.xml.handle;

import cn.jarkata.xml.data.DataValue;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DecodeXmlHandler extends DefaultHandler {

    private final DataValue dataValue = new DataValue();


    StringBuilder builder;

    private final Deque<String> deque = new ConcurrentLinkedDeque<>();

    private String startQName;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
//        System.out.println("startElement:::" + uri + ",localName=" + localName + ",qName=" + qName + ",attributes=" + attributes);
        deque.push(qName);
        startQName = qName;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (Objects.isNull(builder)) {
            builder = new StringBuilder();
        }
        builder.append(ch, start, length);
//        System.out.println("==========start==");

//        System.out.println(new String(ch, start, length).trim());
//        System.out.println("start=" + start + ",len=" + length);
//        System.out.println("=========end===");
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        try {
            String qNameKey;
            Iterator<String> stringIterator = deque.descendingIterator();
            StringBuilder qNameBuilder = new StringBuilder("/");
            while (stringIterator.hasNext()) {
                String next = stringIterator.next();
                qNameBuilder.append("/").append(next);
            }
            qNameKey = qNameBuilder.toString();
            if (qNameKey.endsWith(qName) && startQName.equals(qName)) {
                String dataVal = builder.toString().trim();
                Object value = dataValue.get(qNameKey);
                if (Objects.isNull(value)) {
                    dataValue.put(qNameKey, dataVal);
                    return;
                }
                if (value instanceof List) {
                    List<Object> dataList = (List<Object>) value;
                    dataList.add(dataVal);
                    dataValue.put(qNameKey, dataList);
                } else {
                    List<Object> dataList = new ArrayList<>();
                    dataList.add(value);
                    dataList.add(dataVal);
                    dataValue.put(qNameKey, dataList);
                }
            }
        } finally {
            deque.pop();
            builder = null;
        }

    }


    public DataValue getDataValue() {
        return dataValue;
    }
}