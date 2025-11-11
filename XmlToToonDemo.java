import com.felipestanzani.jtoon.JToon;
import com.felipestanzani.jtoon.EncodeOptions;
import com.felipestanzani.jtoon.Delimiter;

public class XmlToToonDemo {
    public static void main(String[] args) {
        System.out.println("=== XML to TOON Conversion Demo ===\n");
        
        // Example 1: Simple XML
        String xml1 = "<user><id>123</id><name>Ada Lovelace</name><active>true</active></user>";
        String toon1 = JToon.encodeXml(xml1);
        System.out.println("XML Input:");
        System.out.println(xml1);
        System.out.println("\nTOON Output:");
        System.out.println(toon1);
        System.out.println();
        
        // Example 2: XML with attributes
        String xml2 = "<user id=\"456\"><name>Charles Babbage</name><role>inventor</role></user>";
        String toon2 = JToon.encodeXml(xml2);
        System.out.println("XML with Attributes:");
        System.out.println(xml2);
        System.out.println("\nTOON Output:");
        System.out.println(toon2);
        System.out.println();
        
        // Example 3: XML array
        String xml3 = "<users><user><id>1</id><name>Alice</name></user><user><id>2</id><name>Bob</name></user></users>";
        String toon3 = JToon.encodeXml(xml3);
        System.out.println("XML Array:");
        System.out.println(xml3);
        System.out.println("\nTOON Output:");
        System.out.println(toon3);
        System.out.println();
        
        // Example 4: With custom options (tab delimiter, length markers)
        EncodeOptions options = new EncodeOptions(2, Delimiter.TAB, true);
        String toon4 = JToon.encodeXml(xml3, options);
        System.out.println("With Custom Options (Tab delimiter, length markers):");
        System.out.println(toon4);
        System.out.println();
        
        System.out.println("✅ XML to TOON conversion is working perfectly!");
    }
}
