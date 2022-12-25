package cn.jarkata.xml;

import cn.jarkata.commons.utils.StringUtils;
import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模版元素
 */
public class XmlElement {

    public final static String KEY_REQUEST = "request";
    public final static String KEY_RESPONSE = "response";
    public final static String KEY_INFO = "info";
    public final static String KEY_BODY = "body";
    public final static String KEY_LIST = "list";
    public final static String KEY_WRAP = "wrap";
    /**
     * element节点的属性
     */
    public final static String KEY_NAME = "name";
    public final static String KEY_PROPERTY = "property";
    /**
     * 元素节点名称，用于存放转换的信息
     */
    public final static String KEY_DATA = "data";
    /**
     * data节点的属性
     */
    public final static String KEY_VALUE = "value";
    private final static String KEY_PROP_SHOW_LOG = "show-log";
    private final static String KEY_PROP_HIDDEN_PATTERN = "hide-pattern";
    /**
     * 常量 true
     */
    private final static String CONSTANT_TRUE = "true";
    /**
     * 常量false
     */
    private final static String CONSTANT_FALSE = "false";

    /**
     * 节点类型
     */
    private String nodeType;
    /**
     * 元素名称
     */
    private String name;
    /**
     * 元素属性，主要用于转换名称
     */
    private String property;
    /**
     * 判断元素的数据结构是否为list
     */
    private boolean listable = false;
    /**
     * 是否输出日志，默认输出日志
     */
    private boolean showLog = true;
    /**
     * 隐位模式
     */
    private String hidePattern = null;
    /**
     * 存放常量数据的数据结构
     */
    private final Map<String, String> data = new HashMap<>();
    /**
     * 存放子元素
     */
    private Map<String, XmlElement> children = new LinkedHashMap<>();

    public String getData(String key) {
        if (StringUtils.isBlank(key)) {
            return key;
        }
        String value = data.get(key);
        if (StringUtils.isBlank(value)) {
            return key;
        }
        return value;
    }

    public void setData(String key, String value) {
        this.data.put(key, value);
    }

    public XmlElement(String nodeType, String name, String property) {
        this.name = name;
        this.property = property;
        this.nodeType = nodeType;
    }

    public XmlElement(String qName, Attributes attributes) {
        this.nodeType = qName;
        /*
         * 标签中的name属性的值
         */
        this.name = StringUtils.trimToNull(attributes.getValue(KEY_NAME));
        this.property = StringUtils.trimToNull(attributes.getValue(KEY_PROPERTY));
        String showLog = attributes.getValue(KEY_PROP_SHOW_LOG);
        this.showLog = getShowLogProp(StringUtils.trimToEmpty(showLog));
        this.hidePattern = StringUtils.trimToNull(attributes.getValue(KEY_PROP_HIDDEN_PATTERN));
    }

    /**
     * 获取是否输出日志的变量
     *
     * @param showLogValue 是否输出日志的变量
     * @return 是否true/false
     */
    private boolean getShowLogProp(String showLogValue) {
        boolean logable = true;
        boolean valid = StringUtils.isNotBlank(showLogValue) && (CONSTANT_TRUE.equalsIgnoreCase(showLogValue) || CONSTANT_FALSE.equalsIgnoreCase(showLogValue));
        if (valid) {
            logable = Boolean.parseBoolean(showLogValue);
        }
        return logable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Map<String, XmlElement> getChildren() {
        if (children == null) {
            children = new ConcurrentHashMap<>();
        }
        return children;
    }

    public void setChildren(Map<String, XmlElement> children) {
        this.children.putAll(children);
    }

    public boolean isListable() {
        return listable;
    }

    public void setListable(boolean listable) {
        this.listable = listable;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public String getHidePattern() {
        return hidePattern;
    }

    public void setHidePattern(String hidePattern) {
        this.hidePattern = hidePattern;
    }

    @Override
    public String toString() {
        return "Element{" + "nodeType='" + nodeType + '\'' +
                ", name='" + name + '\'' +
                ", property='" + property + '\'' +
                ", listable=" + listable +
                ", showLog=" + showLog +
                ", hidePattern='" + hidePattern + '\'' +
                ", data=" + data +
                ", children=" + children +
                '}';
    }
}