import com.felipestanzani.jtoon.JToon;

public class DebugXml {
    public static void main(String[] args) {
        String xml1 = "<person><name>Ada</name><age>30</age><active>true</active></person>";
        String result1 = JToon.encodeXml(xml1);
        System.out.println("XML1 result:");
        System.out.println(result1);
        System.out.println();
        
        String xml2 = "<product><id>101</id><name>Laptop</name><price>999.99</price><inStock>true</inStock></product>";
        String result2 = JToon.encodeXml(xml2);
        System.out.println("XML2 result:");
        System.out.println(result2);
        System.out.println();
        
        String xml3 = "<user><person><name>Bob</name><age>25</age><active>false</active></person></user>";
        String result3 = JToon.encodeXml(xml3);
        System.out.println("XML3 result:");
        System.out.println(result3);
    }
}
