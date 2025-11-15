package com.felipestanzani.jtoon.encoder;

import java.util.List;

import static com.felipestanzani.jtoon.util.Constants.*;

/**
 * Formats headers for arrays and tables in TOON format.
 */
public final class HeaderFormatter {

    private HeaderFormatter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Configuration for header formatting.
     * 
     * @param length       Array or table length
     * @param key          Optional key prefix
     * @param fields       Optional field names for tabular format
     * @param delimiter    The delimiter being used
     * @param lengthMarker Whether to include # marker before length
     */
    public record HeaderConfig(
            int length,
            String key,
            List<String> fields,
            String delimiter,
            boolean lengthMarker) {
    }

    /**
     * Formats a header for arrays and tables.
     * 
     * @param config Header configuration
     * @return Formatted header string
     */
    public static String format(HeaderConfig config) {
        StringBuilder header = new StringBuilder();

        appendKeyIfPresent(header, config.key());
        appendArrayLength(header, config.length(), config.delimiter(), config.lengthMarker());
        appendFieldsIfPresent(header, config.fields(), config.delimiter());
        header.append(COLON);

        return header.toString();
    }

    /**
     * Legacy method for backward compatibility.
     * Delegates to the record-based format method.
     *
     * @param length Array or table length
     * @param key Optional key prefix
     * @param fields Optional field names for tabular format
     * @param delimiter The delimiter being used
     * @param lengthMarker Whether to include # marker before length
     * @return Formatted header string
     */
    public static String format(
            int length,
            String key,
            List<String> fields,
            String delimiter,
            boolean lengthMarker) {
        HeaderConfig config = new HeaderConfig(length, key, fields, delimiter, lengthMarker);
        return format(config);
    }

    private static void appendKeyIfPresent(StringBuilder header, String key) {
        if (key != null) {
            header.append(PrimitiveEncoder.encodeKey(key));
        }
    }

    private static void appendArrayLength(
            StringBuilder header,
            int length,
            String delimiter,
            boolean lengthMarker) {
        header.append(OPEN_BRACKET);
        
        if (lengthMarker) {
            header.append("#");
        }
        
        header.append(length);
        appendDelimiterIfNotDefault(header, delimiter);
        header.append(CLOSE_BRACKET);
    }

    private static void appendDelimiterIfNotDefault(StringBuilder header, String delimiter) {
        if (!delimiter.equals(COMMA)) {
            header.append(delimiter);
        }
    }

    private static void appendFieldsIfPresent(
            StringBuilder header,
            List<String> fields,
            String delimiter) {
        if (fields == null || fields.isEmpty()) {
            return;
        }

        header.append(OPEN_BRACE);
        String quotedFields = formatFields(fields, delimiter);
        header.append(quotedFields);
        header.append(CLOSE_BRACE);
    }

    private static String formatFields(List<String> fields, String delimiter) {
        return fields.stream()
                .map(PrimitiveEncoder::encodeKey)
                .reduce((a, b) -> a + delimiter + b)
                .orElse("");
    }
}

