package dev.toonformat.toon.encoder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.DoubleNode;
import tools.jackson.databind.node.FloatNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.StringNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PrimitiveEncoder utility class.
 * Tests encoding of primitive values, keys, and header formatting.
 */
@Tag("unit")
public class PrimitiveEncoderTest {

    @Nested
    @DisplayName("encodePrimitive - Booleans")
    class EncodePrimitiveBoolean {

        @Test
        @DisplayName("should encode true")
        void testTrue() {
            String result = PrimitiveEncoder.encodePrimitive(BooleanNode.TRUE, ",");
            assertEquals("true", result);
        }

        @Test
        @DisplayName("should encode false")
        void testFalse() {
            String result = PrimitiveEncoder.encodePrimitive(BooleanNode.FALSE, ",");
            assertEquals("false", result);
        }
    }

    @Nested
    @DisplayName("encodePrimitive - Numbers")
    class EncodePrimitiveNumber {

        @Test
        @DisplayName("should encode integer")
        void testInteger() {
            String result = PrimitiveEncoder.encodePrimitive(IntNode.valueOf(42), ",");
            assertEquals("42", result);
        }

        @Test
        @DisplayName("should encode negative integer")
        void testNegativeInteger() {
            String result = PrimitiveEncoder.encodePrimitive(IntNode.valueOf(-100), ",");
            assertEquals("-100", result);
        }

        @Test
        @DisplayName("should encode zero")
        void testZero() {
            String result = PrimitiveEncoder.encodePrimitive(IntNode.valueOf(0), ",");
            assertEquals("0", result);
        }

        @Test
        @DisplayName("should encode long")
        void testLong() {
            String result = PrimitiveEncoder.encodePrimitive(LongNode.valueOf(9999999999L), ",");
            assertEquals("9999999999", result);
        }

        @Test
        @DisplayName("should encode double")
        void testDouble() {
            String result = PrimitiveEncoder.encodePrimitive(DoubleNode.valueOf(3.14), ",");
            assertEquals("3.14", result);
        }

        @Test
        @DisplayName("should encode float")
        void testFloat() {
            String result = PrimitiveEncoder.encodePrimitive(FloatNode.valueOf(2.5f), ",");
            assertEquals("2.5", result);
        }

        @Test
        @DisplayName("should encode decimal node")
        void testDecimal() {
            String result = PrimitiveEncoder.encodePrimitive(DecimalNode.valueOf(new java.math.BigDecimal("123.456")), ",");
            assertEquals("123.456", result);
        }
    }

    @Nested
    @DisplayName("encodePrimitive - Strings")
    class EncodePrimitiveString {

        @Test
        @DisplayName("should encode simple string unquoted")
        void testSimpleString() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf("hello"), ",");
            assertEquals("hello", result);
        }

        @Test
        @DisplayName("should quote string with comma when using comma delimiter")
        void testStringWithComma() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf("a,b"), ",");
            assertEquals("\"a,b\"", result);
        }

        @Test
        @DisplayName("should not quote string with comma when using pipe delimiter")
        void testStringWithCommaUsingPipe() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf("a,b"), "|");
            assertEquals("a,b", result);
        }

        @Test
        @DisplayName("should quote empty string")
        void testEmptyString() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf(""), ",");
            assertEquals("\"\"", result);
        }

        @Test
        @DisplayName("should quote string that looks like boolean")
        void testBooleanLikeString() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf("true"), ",");
            assertEquals("\"true\"", result);
        }

        @Test
        @DisplayName("should quote string that looks like null")
        void testNullLikeString() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf("null"), ",");
            assertEquals("\"null\"", result);
        }

        @Test
        @DisplayName("should quote string that looks like number")
        void testNumberLikeString() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf("123"), ",");
            assertEquals("\"123\"", result);
        }
    }

    @Nested
    @DisplayName("encodePrimitive - Null")
    class EncodePrimitiveNull {

        @Test
        @DisplayName("should encode null")
        void testNull() {
            String result = PrimitiveEncoder.encodePrimitive(NullNode.getInstance(), ",");
            assertEquals("null", result);
        }
    }

    @Nested
    @DisplayName("encodeStringLiteral")
    class EncodeStringLiteral {

        @Test
        @DisplayName("should encode simple string without quotes")
        void testSimpleString() {
            String result = PrimitiveEncoder.encodeStringLiteral("hello world", ",");
            assertEquals("hello world", result);
        }

        @Test
        @DisplayName("should quote and escape string with quotes")
        void testStringWithQuotes() {
            String result = PrimitiveEncoder.encodeStringLiteral("say \"hi\"", ",");
            assertEquals("\"say \\\"hi\\\"\"", result);
        }

        @Test
        @DisplayName("should quote string with leading space")
        void testLeadingSpace() {
            String result = PrimitiveEncoder.encodeStringLiteral(" hello", ",");
            assertEquals("\" hello\"", result);
        }

        @Test
        @DisplayName("should quote string with trailing space")
        void testTrailingSpace() {
            String result = PrimitiveEncoder.encodeStringLiteral("hello ", ",");
            assertEquals("\"hello \"", result);
        }

        @Test
        @DisplayName("should quote string with colon")
        void testColon() {
            String result = PrimitiveEncoder.encodeStringLiteral("key:value", ",");
            assertEquals("\"key:value\"", result);
        }

        @Test
        @DisplayName("should quote string with active delimiter")
        void testDelimiter() {
            String result = PrimitiveEncoder.encodeStringLiteral("a,b,c", ",");
            assertEquals("\"a,b,c\"", result);
        }

        @Test
        @DisplayName("should not quote string with inactive delimiter")
        void testInactiveDelimiter() {
            String result = PrimitiveEncoder.encodeStringLiteral("a|b|c", ",");
            assertEquals("a|b|c", result);
        }
    }

    @Nested
    @DisplayName("encodeKey")
    class EncodeKey {

        @Test
        @DisplayName("should encode simple key without quotes")
        void testSimpleKey() {
            String result = PrimitiveEncoder.encodeKey("name");
            assertEquals("name", result);
        }

        @Test
        @DisplayName("should encode key with underscores without quotes")
        void testKeyWithUnderscore() {
            String result = PrimitiveEncoder.encodeKey("user_name");
            assertEquals("user_name", result);
        }

        @Test
        @DisplayName("should encode key with dots without quotes")
        void testKeyWithDots() {
            String result = PrimitiveEncoder.encodeKey("com.example.key");
            assertEquals("com.example.key", result);
        }

        @Test
        @DisplayName("should quote key with spaces")
        void testKeyWithSpaces() {
            String result = PrimitiveEncoder.encodeKey("full name");
            assertEquals("\"full name\"", result);
        }

        @Test
        @DisplayName("should quote numeric key")
        void testNumericKey() {
            String result = PrimitiveEncoder.encodeKey("123");
            assertEquals("\"123\"", result);
        }

        @Test
        @DisplayName("should quote key starting with hyphen")
        void testKeyWithLeadingHyphen() {
            String result = PrimitiveEncoder.encodeKey("-key");
            assertEquals("\"-key\"", result);
        }

        @Test
        @DisplayName("should quote empty key")
        void testEmptyKey() {
            String result = PrimitiveEncoder.encodeKey("");
            assertEquals("\"\"", result);
        }

        @Test
        @DisplayName("should quote key with special characters")
        void testKeyWithSpecialChars() {
            String result = PrimitiveEncoder.encodeKey("key:value");
            assertEquals("\"key:value\"", result);
        }
    }

    @Nested
    @DisplayName("joinEncodedValues")
    class JoinEncodedValues {

        @Test
        @DisplayName("should join primitive values with comma")
        void testJoinWithComma() {
            List<JsonNode> values = List.of(
                    IntNode.valueOf(1),
                    StringNode.valueOf("hello"),
                    BooleanNode.TRUE);
            String result = PrimitiveEncoder.joinEncodedValues(values, ",");
            assertEquals("1,hello,true", result);
        }

        @Test
        @DisplayName("should join values with pipe delimiter")
        void testJoinWithPipe() {
            List<JsonNode> values = List.of(
                    IntNode.valueOf(1),
                    StringNode.valueOf("test"),
                    IntNode.valueOf(2));
            String result = PrimitiveEncoder.joinEncodedValues(values, "|");
            assertEquals("1|test|2", result);
        }

        @Test
        @DisplayName("should join values with tab delimiter")
        void testJoinWithTab() {
            List<JsonNode> values = List.of(
                    StringNode.valueOf("a"),
                    StringNode.valueOf("b"),
                    StringNode.valueOf("c"));
            String result = PrimitiveEncoder.joinEncodedValues(values, "\t");
            assertEquals("a\tb\tc", result);
        }

        @Test
        @DisplayName("should handle empty list")
        void testEmptyList() {
            List<JsonNode> values = List.of();
            String result = PrimitiveEncoder.joinEncodedValues(values, ",");
            assertEquals("", result);
        }

        @Test
        @DisplayName("should handle single value")
        void testSingleValue() {
            List<JsonNode> values = List.of(IntNode.valueOf(42));
            String result = PrimitiveEncoder.joinEncodedValues(values, ",");
            assertEquals("42", result);
        }

        @Test
        @DisplayName("should quote values containing delimiter")
        void testQuoteDelimiter() {
            List<JsonNode> values = List.of(
                    StringNode.valueOf("a,b"),
                    StringNode.valueOf("c,d"));
            String result = PrimitiveEncoder.joinEncodedValues(values, ",");
            assertEquals("\"a,b\",\"c,d\"", result);
        }

        @Test
        @DisplayName("should handle null values")
        void testNullValues() {
            List<JsonNode> values = List.of(
                    IntNode.valueOf(1),
                    NullNode.getInstance(),
                    IntNode.valueOf(2));
            String result = PrimitiveEncoder.joinEncodedValues(values, ",");
            assertEquals("1,null,2", result);
        }
    }

    @Nested
    @DisplayName("formatHeader")
    class FormatHeader {

        @Test
        @DisplayName("should format simple array header")
        void testSimpleHeader() {
            String result = PrimitiveEncoder.formatHeader(5, "items", null, ",", false);
            assertEquals("items[5]:", result);
        }

        @Test
        @DisplayName("should format tabular header")
        void testTabularHeader() {
            List<String> fields = List.of("id", "name");
            String result = PrimitiveEncoder.formatHeader(3, "users", fields, ",", false);
            assertEquals("users[3]{id,name}:", result);
        }

        @Test
        @DisplayName("should format header with length marker")
        void testWithLengthMarker() {
            String result = PrimitiveEncoder.formatHeader(5, "data", null, ",", true);
            assertEquals("data[#5]:", result);
        }

        @Test
        @DisplayName("should format header with pipe delimiter")
        void testPipeDelimiter() {
            List<String> fields = List.of("x", "y");
            String result = PrimitiveEncoder.formatHeader(2, "points", fields, "|", false);
            assertEquals("points[2|]{x|y}:", result);
        }

        @Test
        @DisplayName("should format header without key")
        void testWithoutKey() {
            String result = PrimitiveEncoder.formatHeader(3, null, null, ",", false);
            assertEquals("[3]:", result);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration")
    class EdgeCasesIntegration {

        @Test
        @DisplayName("should handle Unicode in string encoding")
        void testUnicode() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf("Hello 世界"), ",");
            assertEquals("Hello 世界", result);
        }

        @Test
        @DisplayName("should handle emoji in string encoding")
        void testEmoji() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf("Hello 🌍"), ",");
            assertEquals("Hello 🌍", result);
        }

        @Test
        @DisplayName("should handle complex escaped string")
        void testComplexEscaping() {
            String result = PrimitiveEncoder.encodePrimitive(StringNode.valueOf("line1\nline2\ttab"), ",");
            assertEquals("\"line1\\nline2\\ttab\"", result);
        }

        @Test
        @DisplayName("should join mixed type values correctly")
        void testMixedTypes() {
            List<JsonNode> values = List.of(
                    IntNode.valueOf(123),
                    StringNode.valueOf("text"),
                    BooleanNode.FALSE,
                    NullNode.getInstance(),
                    DoubleNode.valueOf(3.14));
            String result = PrimitiveEncoder.joinEncodedValues(values, ",");
            assertEquals("123,text,false,null,3.14", result);
        }
    }
}

