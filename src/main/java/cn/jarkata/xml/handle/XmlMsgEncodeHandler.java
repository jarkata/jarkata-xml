package cn.jarkata.xml.handle;

import cn.jarkata.commons.utils.StringUtils;
import cn.jarkata.xml.data.DataMap;
import cn.jarkata.xml.data.XmlNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class XmlMsgEncodeHandler {

    private final TransformerHandler transformerHandler;

    public XmlMsgEncodeHandler(TransformerHandler transformerHandler) {
        this.transformerHandler = transformerHandler;
    }

    public void encodeXmlMsg(DataMap message) throws Exception {
        List<XmlNode> nodeList = message.getNodeList();
        if (nodeList.isEmpty()) {
            return;
        }
        XmlNode rootNode = nodeList.get(0);
        buildMsg(rootNode);
    }

    private void buildMsg(XmlNode xmlNode) throws Exception {
        Map<String, List<XmlNode>> nodeChildren = xmlNode.getChildren();
        if (nodeChildren.isEmpty()) {
            String nodeValue = xmlNode.getValue();
            buildPerElement(xmlNode, nodeValue);
            return;
        }

        try {
            startElement(xmlNode);
            for (Map.Entry<String, List<XmlNode>> nodeEntry : nodeChildren.entrySet()) {
                List<XmlNode> nodeEntryValue = nodeEntry.getValue();
                for (XmlNode node : nodeEntryValue) {
                    buildMsg(node);
                }
            }
        } finally {
            endElement(xmlNode);
        }

    }

    private void buildPerElement(XmlNode xmlNode, Object data) throws Exception {
        Objects.requireNonNull(xmlNode, "Xml节点对象为空");
        try {
            startElement(xmlNode);
            writeData(data);
        } finally {
            endElement(xmlNode);
        }
    }

    private void startElement(XmlNode xmlNode) throws SAXException {
        Attributes attributes = makeAttr(xmlNode.getAttr());
        transformerHandler.startElement(null, null, xmlNode.getName(), attributes);
    }


    private void writeData(Object data) throws SAXException {
        String value = Objects.toString(data, null);
        value = StringUtils.defaultIfBlank(value, " ");
        char[] dataArr = value.toCharArray();
        transformerHandler.characters(dataArr, 0, dataArr.length);
    }

    private void endElement(XmlNode xmlNode) throws SAXException {
        transformerHandler.endElement(null, null, xmlNode.getName());
    }


    /**
     * 组织XML节点的属性
     *
     * @param nodeAttr 节点属性信息
     * @return 属性对象
     */
    private Attributes makeAttr(Map<String, String> nodeAttr) {
        AttributesImpl attributes = new AttributesImpl();
        for (Map.Entry<String, String> entry : nodeAttr.entrySet()) {
            String entryKey = entry.getKey();
            String entryValue = entry.getValue();
            attributes.addAttribute("", "", entryKey, "CDATA", entryValue);
        }
        return attributes;
    }

}