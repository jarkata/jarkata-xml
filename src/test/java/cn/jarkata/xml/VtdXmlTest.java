package cn.jarkata.xml;

import cn.jarkata.commons.utils.FileUtils;
import cn.jarkata.xml.data.DataValue;
import com.ximpleware.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class VtdXmlTest {

    @Test
    public void testParseXml() throws Exception {
        InputStream stream = FileUtils.getStream("packet/xml/encode/test-data.xml");

        XmlFactory factory = new XmlFactory();
        String encode = factory.encode(new DataValue());
        System.out.println(encode);
        stream = new ByteArrayInputStream(encode.getBytes(StandardCharsets.UTF_8));

        Objects.requireNonNull(stream);
        byte[] data = new byte[stream.available()];
        int read = stream.read(data);
        //instantiate VTDGen

        long start = System.currentTimeMillis();
        //and call parse
        VTDGen vg = new VTDGen();
        vg.setDoc(data);
        vg.parse(true);  // set namespace awareness to true
        VTDNav vn = vg.getNav();
        AutoPilot ap = new AutoPilot(vn);
//        ap.declareXPathNameSpace("ns1", "http://purl.org/dc/elements/1.1/");
        ap.selectXPath("//*");
        int result = -1;
        int count = 0;
        while ((result = ap.evalXPath()) != -1) {
//            System.out.print("" + result + "  ");
//            System.out.print("Element name ==> " + vn.toString(result));
            int t = vn.getText(); // get the index of the text (char data or CDATA)
            if (t != -1)
                System.out.println(" Text  ==> " + vn.toNormalizedString(t));
//            System.out.println("\n ============================== ");
            count++;
        }
        long dur = System.currentTimeMillis() - start;
        System.out.println("Total # of element " + count + ",dur=" + dur);
    }
}