import com.felipestanzani.jtoon.JToon;

public class TestXml {
    public static void main(String[] args) {
        // Test simple XML
        String xml1 = "<user><id>123</id><name>Ada</name></user>";
        String toon1 = JToon.encodeXml(xml1);
        System.out.println("XML: " + xml1);
        System.out.println("TOON: " + toon1);
        System.out.println();

        // Test XML with attributes
        String xml2 = "<user id=\"123\"><name>Ada</name></user>";
        String toon2 = JToon.encodeXml(xml2);
        System.out.println("XML: " + xml2);
        System.out.println("TOON: " + toon2);
        System.out.println();

        // Test XML array
        String xml3 = "<users><user><id>1</id><name>Ada</name></user><user><id>2</id><name>Bob</name></user></users>";
        String toon3 = JToon.encodeXml(xml3);
        System.out.println("XML: " + xml3);
        System.out.println("TOON: " + toon3);
    }
}
