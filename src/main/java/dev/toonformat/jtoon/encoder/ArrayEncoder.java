package dev.toonformat.jtoon.encoder;


import dev.toonformat.jtoon.EncodeOptions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static dev.toonformat.jtoon.util.Constants.*;

/**
 * Handles encoding of JSON arrays to TOON format.
 * Orchestrates array encoding by detecting array types and delegating to specialized encoders.
 */
public final class ArrayEncoder {

    private ArrayEncoder() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Main entry point for array encoding.
     * Detects array type and delegates to appropriate encoding method.
     *
     * @param key     Optional key prefix
     * @param value   ArrayNode to encode
     * @param writer  LineWriter for output
     * @param depth   Indentation depth
     * @param options Encoding options
     */
    public static void encodeArray(String key, ArrayNode value, LineWriter writer, int depth, EncodeOptions options) {
        if (value.isEmpty()) {
            String header = PrimitiveEncoder.formatHeader(0, key, null, options.delimiter().getValue(),
                    options.lengthMarker());
            writer.push(depth, header);
            return;
        }

        // Primitive array
        if (isArrayOfPrimitives(value)) {
            encodeInlinePrimitiveArray(key, value, writer, depth, options);
            return;
        }

        // Array of arrays (all primitives)
        if (isArrayOfArrays(value)) {
            boolean allPrimitiveArrays = StreamSupport.stream(value.spliterator(), false)
                    .filter(JsonNode::isArray)
                    .allMatch(ArrayEncoder::isArrayOfPrimitives);

            if (allPrimitiveArrays) {
                encodeArrayOfArraysAsListItems(key, value, writer, depth, options);
                return;
            }
        }

        // Array of objects
        if (isArrayOfObjects(value)) {
            var header = TabularArrayEncoder.detectTabularHeader(value);
            if (!header.isEmpty()) {
                TabularArrayEncoder.encodeArrayOfObjectsAsTabular(key, value, header, writer, depth, options);
            } else {
                encodeMixedArrayAsListItems(key, value, writer, depth, options);
            }
            return;
        }

        // Mixed array: fallback to expanded format
        encodeMixedArrayAsListItems(key, value, writer, depth, options);
    }

    /**
     * Checks if an array contains only primitive values.
     * @param array for testing that all items are primitives
     */
    public static boolean isArrayOfPrimitives(JsonNode array) {
        if (!array.isArray()) {
            return false;
        }
        for (JsonNode item : array) {
            if (!item.isValueNode()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if an array contains only arrays.
     */
    public static boolean isArrayOfArrays(JsonNode array) {
        if (!array.isArray()) {
            return false;
        }
        for (JsonNode item : array) {
            if (!item.isArray()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if an array contains only objects.
     */
    public static boolean isArrayOfObjects(JsonNode array) {
        if (!array.isArray()) {
            return false;
        }
        for (JsonNode item : array) {
            if (!item.isObject()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Encodes a primitive array inline: key[N]: v1,v2,v3
     */
    private static void encodeInlinePrimitiveArray(String prefix, ArrayNode values, LineWriter writer, int depth,
            EncodeOptions options) {
        String formatted = formatInlineArray(values, options.delimiter().getValue(), prefix, options.lengthMarker());
        writer.push(depth, formatted);
    }

    /**
     * Formats an inline primitive array with header and values.
     */
    public static String formatInlineArray(ArrayNode values, String delimiter, String prefix, boolean lengthMarker) {
        List<JsonNode> valueList = new ArrayList<>();
        values.forEach(valueList::add);

        String header = PrimitiveEncoder.formatHeader(values.size(), prefix, null, delimiter, lengthMarker);
        String joinedValue = PrimitiveEncoder.joinEncodedValues(valueList, delimiter);

        // Only add space if there are values
        if (values.isEmpty()) {
            return header;
        }
        return header + SPACE + joinedValue;
    }

    /**
     * Encodes an array of primitive arrays as list items.
     */
    private static void encodeArrayOfArraysAsListItems(String prefix, ArrayNode values, LineWriter writer, int depth,
            EncodeOptions options) {
        String header = PrimitiveEncoder.formatHeader(values.size(), prefix, null, options.delimiter().getValue(),
                options.lengthMarker());
        writer.push(depth, header);

        for (JsonNode arr : values) {
            if (arr.isArray() && isArrayOfPrimitives(arr)) {
                String inline = formatInlineArray((ArrayNode) arr, options.delimiter().getValue(), null,
                        options.lengthMarker());
                writer.push(depth + 1, LIST_ITEM_PREFIX + inline);
            }
        }
    }

    /**
     * Encodes a mixed array (non-uniform) as list items.
     */
    private static void encodeMixedArrayAsListItems(String prefix, ArrayNode items, LineWriter writer, int depth,
            EncodeOptions options) {
        String header = PrimitiveEncoder.formatHeader(items.size(), prefix, null, options.delimiter().getValue(),
                options.lengthMarker());
        writer.push(depth, header);

        for (JsonNode item : items) {
            if (item.isValueNode()) {
                // Direct primitive as list item
                writer.push(depth + 1,
                        LIST_ITEM_PREFIX + PrimitiveEncoder.encodePrimitive(item, options.delimiter().getValue()));
            } else if (item.isArray()) {
                // Direct array as list item
                if (isArrayOfPrimitives(item)) {
                    String inline = formatInlineArray((ArrayNode) item, options.delimiter().getValue(), null,
                            options.lengthMarker());
                    writer.push(depth + 1, LIST_ITEM_PREFIX + inline);
                }
            } else if (item.isObject()) {
                // Object as list item - delegate to ListItemEncoder
                ListItemEncoder.encodeObjectAsListItem((ObjectNode) item, writer,
                        depth + 1, options);
            }
        }
    }
}

