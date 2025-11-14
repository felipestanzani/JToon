package com.felipestanzani.jtoon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class JToonDecodeTest {

    @Nested
    @DisplayName("Primitives")
    class Primitives {

        @Test
        @DisplayName("should decode null")
        void testNull() {
            Object result = JToon.decode("value: null");
            assertInstanceOf(Map.class, result);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertNull(map.get("value"));
        }

        @Test
        @DisplayName("should decode booleans")
        void testBooleans() {
            Object result1 = JToon.decode("active: true");
            @SuppressWarnings("unchecked")
            Map<String, Object> map1 = (Map<String, Object>) result1;
            assertEquals(true, map1.get("active"));

            Object result2 = JToon.decode("active: false");
            @SuppressWarnings("unchecked")
            Map<String, Object> map2 = (Map<String, Object>) result2;
            assertEquals(false, map2.get("active"));
        }

        @Test
        @DisplayName("should decode integers")
        void testIntegers() {
            Object result = JToon.decode("count: 42");
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertEquals(42L, map.get("count"));
        }

        @Test
        @DisplayName("should decode floating point numbers")
        void testFloatingPoint() {
            Object result = JToon.decode("price: 3.14");
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertEquals(3.14, (Double) map.get("price"), 0.0001);
        }

        @Test
        @DisplayName("should decode unquoted strings")
        void testUnquotedStrings() {
            Object result = JToon.decode("name: Ada");
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertEquals("Ada", map.get("name"));
        }

        @Test
        @DisplayName("should decode quoted strings")
        void testQuotedStrings() {
            Object result = JToon.decode("note: \"hello, world\"");
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertEquals("hello, world", map.get("note"));
        }

        @Test
        @DisplayName("should decode strings with escape sequences")
        void testEscapedStrings() {
            Object result = JToon.decode("text: \"line1\\nline2\"");
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertEquals("line1\nline2", map.get("text"));
        }
    }

    @Nested
    @DisplayName("Simple Objects")
    class SimpleObjects {

        @Test
        @DisplayName("should decode simple object")
        void testSimpleObject() {
            String toon = """
                id: 123
                name: Ada
                active: true
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertEquals(123L, map.get("id"));
            assertEquals("Ada", map.get("name"));
            assertEquals(true, map.get("active"));
        }

        @Test
        @DisplayName("should decode object with quoted keys")
        void testQuotedKeys() {
            String toon = "\"full name\": Alice";
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertEquals("Alice", map.get("full name"));
        }

        @Test
        @DisplayName("should decode object with special character keys")
        void testSpecialCharacterKeys() {
            String toon = "\"order:id\": 42";
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertEquals(42L, map.get("order:id"));
        }
    }

    @Nested
    @DisplayName("Nested Objects")
    class NestedObjects {

        @Test
        @DisplayName("should decode nested object")
        void testNestedObject() {
            String toon = """
                user:
                  id: 123
                  name: Ada
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) map.get("user");
            assertEquals(123L, user.get("id"));
            assertEquals("Ada", user.get("name"));
        }

        @Test
        @DisplayName("should decode deeply nested object")
        void testDeeplyNestedObject() {
            String toon = """
                user:
                  id: 123
                  contact:
                    email: ada@example.com
                    phone: "555-1234"
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) map.get("user");
            @SuppressWarnings("unchecked")
            Map<String, Object> contact = (Map<String, Object>) user.get("contact");
            assertEquals("ada@example.com", contact.get("email"));
            assertEquals("555-1234", contact.get("phone"));
        }
    }

    @Nested
    @DisplayName("Primitive Arrays")
    class PrimitiveArrays {

        @Test
        @DisplayName("should decode inline primitive array")
        void testInlinePrimitiveArray() {
            String toon = "tags[3]: reading,gaming,coding";
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> tags = (List<Object>) map.get("tags");
            assertEquals(3, tags.size());
            assertEquals("reading", tags.get(0));
            assertEquals("gaming", tags.get(1));
            assertEquals("coding", tags.get(2));
        }

        @Test
        @DisplayName("should decode multiline primitive array")
        void testMultilinePrimitiveArray() {
            String toon = """
                tags[3]:
                  reading,gaming,coding
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> tags = (List<Object>) map.get("tags");
            assertEquals(3, tags.size());
            assertEquals("reading", tags.get(0));
            assertEquals("gaming", tags.get(1));
            assertEquals("coding", tags.get(2));
        }

        @Test
        @DisplayName("should decode array with mixed primitives")
        void testMixedPrimitiveArray() {
            String toon = "values[4]: 42,3.14,\"true\",null";
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> values = (List<Object>) map.get("values");
            assertEquals(42L, values.get(0));
            assertEquals(3.14, (Double) values.get(1), 0.0001);
            assertEquals("true", values.get(2));
            assertNull(values.get(3));
        }

        @Test
        @DisplayName("should decode empty array")
        void testEmptyArray() {
            String toon = "items[0]:";
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> items = (List<Object>) map.get("items");
            assertEquals(0, items.size());
        }
    }

    @Nested
    @DisplayName("Tabular Arrays")
    class TabularArrays {

        @Test
        @DisplayName("should decode tabular array")
        void testTabularArray() {
            String toon = """
                users[2]{id,name,role}:
                  1,Alice,admin
                  2,Bob,user
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> users = (List<Object>) map.get("users");
            assertEquals(2, users.size());

            @SuppressWarnings("unchecked")
            Map<String, Object> user1 = (Map<String, Object>) users.get(0);
            assertEquals(1L, user1.get("id"));
            assertEquals("Alice", user1.get("name"));
            assertEquals("admin", user1.get("role"));

            @SuppressWarnings("unchecked")
            Map<String, Object> user2 = (Map<String, Object>) users.get(1);
            assertEquals(2L, user2.get("id"));
            assertEquals("Bob", user2.get("name"));
            assertEquals("user", user2.get("role"));
        }

        @Test
        @DisplayName("should decode tabular array with mixed types")
        void testTabularArrayMixedTypes() {
            String toon = """
                items[2]{sku,qty,price}:
                  A1,2,9.99
                  B2,1,14.5
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> items = (List<Object>) map.get("items");

            @SuppressWarnings("unchecked")
            Map<String, Object> item1 = (Map<String, Object>) items.get(0);
            assertEquals("A1", item1.get("sku"));
            assertEquals(2L, item1.get("qty"));
            assertEquals(9.99, (Double) item1.get("price"), 0.0001);
        }

        @Test
        @DisplayName("should decode tabular array with quoted values")
        void testTabularArrayQuotedValues() {
            String toon = """
                items[2]{id,name}:
                  1,"First Item"
                  2,"Second, Item"
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> items = (List<Object>) map.get("items");

            @SuppressWarnings("unchecked")
            Map<String, Object> item2 = (Map<String, Object>) items.get(1);
            assertEquals("Second, Item", item2.get("name"));
        }
    }

    @Nested
    @DisplayName("List Arrays")
    class ListArrays {

        @Test
        @DisplayName("should decode list array with simple items")
        void testSimpleListArray() {
            String toon = """
                items[2]:
                  - first
                  - second
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> items = (List<Object>) map.get("items");
            assertEquals("first", items.get(0));
            assertEquals("second", items.get(1));
        }

        @Test
        @DisplayName("should decode list array with object items")
        void testListArrayWithObjects() {
            String toon = """
                items[2]:
                  - id: 1
                    name: First
                  - id: 2
                    name: Second
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> items = (List<Object>) map.get("items");

            @SuppressWarnings("unchecked")
            Map<String, Object> item1 = (Map<String, Object>) items.get(0);
            assertEquals(1L, item1.get("id"));
            assertEquals("First", item1.get("name"));

            @SuppressWarnings("unchecked")
            Map<String, Object> item2 = (Map<String, Object>) items.get(1);
            assertEquals(2L, item2.get("id"));
            assertEquals("Second", item2.get("name"));
        }
    }

    @Nested
    @DisplayName("Delimiter Support")
    class DelimiterSupport {

        @Test
        @DisplayName("should decode comma-delimited array")
        void testCommaDelimiter() {
            String toon = "tags[3]: a,b,c";
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> tags = (List<Object>) map.get("tags");
            assertEquals(3, tags.size());
        }

        @Test
        @DisplayName("should decode tab-delimited array")
        void testTabDelimiter() {
            String toon = "tags[3\t]:\ta\tb\tc";
            DecodeOptions options = DecodeOptions.withDelimiter(Delimiter.TAB);
            Object result = JToon.decode(toon, options);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> tags = (List<Object>) map.get("tags");
            assertEquals(3, tags.size());
            assertEquals("a", tags.get(0));
            assertEquals("b", tags.get(1));
            assertEquals("c", tags.get(2));
        }

        @Test
        @DisplayName("should decode pipe-delimited array")
        void testPipeDelimiter() {
            String toon = "tags[3|]: a|b|c";
            DecodeOptions options = DecodeOptions.withDelimiter(Delimiter.PIPE);
            Object result = JToon.decode(toon, options);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> tags = (List<Object>) map.get("tags");
            assertEquals(3, tags.size());
        }
    }

    @Nested
    @DisplayName("Complex Structures")
    class ComplexStructures {

        @Test
        @DisplayName("should decode object with nested arrays")
        void testObjectWithNestedArrays() {
            String toon = """
                user:
                  id: 123
                  name: Ada
                  tags[2]: dev,admin
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) map.get("user");
            @SuppressWarnings("unchecked")
            List<Object> tags = (List<Object>) user.get("tags");
            assertEquals(2, tags.size());
        }

        @Test
        @DisplayName("should decode array of nested objects")
        void testArrayOfNestedObjects() {
            String toon = """
                users[2]{id,name}:
                  1,Alice
                  2,Bob
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Object> users = (List<Object>) map.get("users");
            assertEquals(2, users.size());
        }

        @Test
        @DisplayName("should decode mixed content at root level")
        void testMixedRootContent() {
            String toon = """
                id: 123
                name: Ada
                tags[2]: dev,admin
                active: true
                """;
            Object result = JToon.decode(toon);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) result;
            assertEquals(123L, map.get("id"));
            assertEquals("Ada", map.get("name"));
            assertEquals(true, map.get("active"));
            @SuppressWarnings("unchecked")
            List<Object> tags = (List<Object>) map.get("tags");
            assertEquals(2, tags.size());
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("should handle empty input")
        void testEmptyInput() {
            assertEquals(Collections.emptyMap(), JToon.decode(""));
            assertEquals(Collections.emptyMap(), JToon.decode("   "));
            assertEquals(Collections.emptyMap(), JToon.decode(null));
        }

        @Test
        @DisplayName("should throw in strict mode for invalid array header")
        void testStrictModeError() {
            String toon = "[invalid]";  // Invalid array header format
            DecodeOptions options = DecodeOptions.withStrict(true);
            assertThrows(IllegalArgumentException.class, () -> JToon.decode(toon, options));
        }

        @Test
        @DisplayName("should return null in lenient mode for invalid array header")
        void testLenientMode() {
            String toon = "[invalid]";  // Invalid array header format
            DecodeOptions options = DecodeOptions.withStrict(false);
            var result = JToon.decode(toon, options);
            assertEquals(Collections.emptyList(), result);
        }
    }

    @Nested
    @DisplayName("DecodeToJson")
    class DecodeToJson {

        @Test
        @DisplayName("should decode to JSON string")
        void testDecodeToJson() {
            String toon = """
                id: 123
                name: Ada
                """;
            String json = JToon.decodeToJson(toon);
            assertNotNull(json);
            assertTrue(json.contains("123"));
            assertTrue(json.contains("Ada"));
        }

        @Test
        @DisplayName("should decode complex structure to JSON")
        void testComplexDecodeToJson() {
            String toon = """
                users[2]{id,name}:
                  1,Alice
                  2,Bob
                """;
            String json = JToon.decodeToJson(toon);
            assertNotNull(json);
            assertTrue(json.contains("users"));
            assertTrue(json.contains("Alice"));
            assertTrue(json.contains("Bob"));
        }
    }
}
