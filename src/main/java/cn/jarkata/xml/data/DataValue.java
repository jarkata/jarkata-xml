package cn.jarkata.xml.data;

import java.util.HashMap;
import java.util.List;

public class DataValue extends HashMap<String, Object> {
    

    public List<DataValue> dataList;

    public List<DataValue> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataValue> dataList) {
        this.dataList = dataList;
    }
}