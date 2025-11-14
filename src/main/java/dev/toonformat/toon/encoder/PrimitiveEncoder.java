package dev.toonformat.toon.encoder;

import dev.toonformat.toon.util.StringEscaper;
import dev.toonformat.toon.util.StringValidator;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.List;

import static dev.toonformat.toon.util.Constants.*;

/**
 * Encodes primitive values and object keys for TOON format.
 * Delegates validation to StringValidator, escaping to StringEscaper,
 * and header formatting to HeaderFormatter.
 */
public final class PrimitiveEncoder {

    private PrimitiveEncoder() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Encodes a primitive JsonNode value.
     */
    public static String encodePrimitive(JsonNode value, String delimiter) {
        return switch (value.getNodeType()) {
            case BOOLEAN -> String.valueOf(value.asBoolean());
            case NUMBER -> encodeNumber(value);
            case STRING -> encodeStringLiteral(value.asString(), delimiter);
            default -> NULL_LITERAL;
        };
    }

    /**
     * Encodes a number JsonNode to plain decimal format (no scientific notation).
     * Ensures LLM-safe output by converting all numbers to plain decimal
     * representation.
     */
    private static String encodeNumber(JsonNode value) {
        if (value.isIntegralNumber()) {
            return value.asString();
        }

        double doubleValue = value.asDouble();
        BigDecimal decimal = BigDecimal.valueOf(doubleValue);
        String plainString = decimal.toPlainString();

        return stripTrailingZeros(plainString);
    }

    /**
     * Strips trailing zeros from decimal numbers while preserving single zero after
     * decimal point.
     * Examples: "1.500" -> "1.5", "1.0" -> "1", "0.000001" -> "0.000001"
     */
    private static String stripTrailingZeros(String value) {
        if (!value.contains(".")) {
            return value;
        }

        String stripped = value.replaceAll("0+$", "");

        if (stripped.endsWith(".")) {
            stripped = stripped.substring(0, stripped.length() - 1);
        }

        return stripped;
    }

    /**
     * Encodes a string literal, quoting if necessary.
     * Delegates validation to StringValidator and escaping to StringEscaper.
     */
    public static String encodeStringLiteral(String value, String delimiter) {
        if (StringValidator.isSafeUnquoted(value, delimiter)) {
            return value;
        }

        return DOUBLE_QUOTE + StringEscaper.escape(value) + DOUBLE_QUOTE;
    }

    /**
     * Encodes an object key, quoting if necessary.
     * Delegates validation to StringValidator and escaping to StringEscaper.
     */
    public static String encodeKey(String key) {
        if (StringValidator.isValidUnquotedKey(key)) {
            return key;
        }

        return DOUBLE_QUOTE + StringEscaper.escape(key) + DOUBLE_QUOTE;
    }

    /**
     * Joins encoded primitive values with the specified delimiter.
     */
    public static String joinEncodedValues(List<JsonNode> values, String delimiter) {
        return values.stream()
                .map(v -> encodePrimitive(v, delimiter))
                .reduce((a, b) -> a + delimiter + b)
                .orElse("");
    }

    /**
     * Formats a header for arrays and tables.
     * Delegates to HeaderFormatter for implementation.
     * 
     * @param length       Array length
     * @param key          Optional key prefix
     * @param fields       Optional field names for tabular format
     * @param delimiter    The delimiter being used
     * @param lengthMarker Whether to include # marker before length
     * @return Formatted header string
     */
    public static String formatHeader(
            int length,
            String key,
            List<String> fields,
            String delimiter,
            boolean lengthMarker) {
        return HeaderFormatter.format(length, key, fields, delimiter, lengthMarker);
    }
}
