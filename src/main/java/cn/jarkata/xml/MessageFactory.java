package cn.jarkata.xml;

import java.util.Map;

public interface MessageFactory {

    /**
     * 解码报文
     *
     * @param message     xml数据
     * @param templateUrl xml模版
     * @return 解析之后的map数据
     */
    Map<String, Object> decode(String message, String templateUrl);

    /**
     * 解码报文
     *
     * @param message
     * @param messageType 使用表示decode或encode表示
     * @param templateUrl
     * @return
     */
    Map<String, Object> decode(String message, String templateUrl, String messageType);

    /**
     * 生成报文
     *
     * @param message
     * @param templateUrl
     * @return
     */
    String encode(Map<String, Object> message, String templateUrl);

    /**
     * 编码报文
     *
     * @param message
     * @param messageType 使用表示decode或encode表示
     * @param templateUrl
     * @return
     */
    String encode(Map<String, Object> message, String templateUrl, String messageType);
}