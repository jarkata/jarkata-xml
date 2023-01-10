package cn.jarkata.xml;

import cn.jarkata.xml.data.DataMap;

import java.io.InputStream;

public interface MessageFactory {

    /**
     * 解码报文
     *
     * @param message xml数据
     * @return 解析之后的map数据
     */
    DataMap decode(InputStream message) throws Exception;

    /**
     * 生成报文
     *
     * @param message 报文数据
     * @return XML数据
     */
    String encode(DataMap message) throws Exception;

}