package cn.jarkata.xml.handle;

import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.data.XmlNode;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.Map;
import java.util.Objects;

public class XmlMsgEncodeHandler {

    private final TransformerHandler transformerHandler;

    public XmlMsgEncodeHandler(TransformerHandler transformerHandler) {
        this.transformerHandler = transformerHandler;
    }

    public void buildPerElement(XmlNode xmlNode, Object data) throws SAXException {
        Objects.requireNonNull(xmlNode, "Xml节点对象为空");
        String value = Objects.toString(data, null);
        value = StringUtils.defaultIfBlank(value, " ");
        char[] dataArr = value.toCharArray();

        AttributesImpl attributes = new AttributesImpl();
        Map<String, String> nodeAttr = xmlNode.getAttr();
        for (Map.Entry<String, String> entry : nodeAttr.entrySet()) {
            String entryKey = entry.getKey();
            String entryValue = entry.getValue();
            attributes.addAttribute("", "", entryKey, "CDATA", entryValue);
        }

        transformerHandler.startElement(null, null, xmlNode.getName(), attributes);
        transformerHandler.characters(dataArr, 0, dataArr.length);
        transformerHandler.endElement(null, null, xmlNode.getName());
    }

}