package cn.jarkata.xml.handle;

import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;

public abstract class AbstractDecodeHandler extends DefaultHandler {

    public abstract Map<String, Object> getData();

}