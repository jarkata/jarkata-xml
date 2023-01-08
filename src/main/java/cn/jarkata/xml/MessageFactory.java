package cn.jarkata.xml;

import cn.jarkata.xml.data.DataValue;

import java.io.InputStream;

public interface MessageFactory {

    /**
     * 解码报文
     *
     * @param message xml数据
     * @return 解析之后的map数据
     */
    DataValue decode(InputStream message) throws Exception;

    /**
     * 生成报文
     *
     * @param message
     * @return XML数据
     */
    String encode(DataValue message) throws Exception;

}