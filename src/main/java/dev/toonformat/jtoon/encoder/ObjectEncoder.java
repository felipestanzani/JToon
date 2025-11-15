package dev.toonformat.jtoon.encoder;


import dev.toonformat.jtoon.EncodeOptions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.Collection;

import static dev.toonformat.jtoon.util.Constants.COLON;
import static dev.toonformat.jtoon.util.Constants.SPACE;

/**
 * Handles encoding of JSON objects to TOON format.
 * Recursively encodes nested objects and delegates arrays to ArrayEncoder.
 */
public final class ObjectEncoder {

    private ObjectEncoder() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Encodes an ObjectNode to TOON format.
     *
     * @param value   The ObjectNode to encode
     * @param writer  LineWriter for accumulating output
     * @param depth   Current indentation depth
     * @param options Encoding options
     */
    public static void encodeObject(ObjectNode value, LineWriter writer, int depth, EncodeOptions options) {
        Collection<String> fieldNames = value.propertyNames();

        for (String fildName : fieldNames) {
            JsonNode fieldValue = value.get(fildName);
            encodeKeyValuePair(fildName, fieldValue, writer, depth, options);
        }
    }

    /**
     * Encodes a key-value pair in an object.
     * @param key the key name
     * @param value the value to encode
     * @param writer the LineWriter for accumulating output
     * @param depth the current indentation depth
     * @param options encoding options
     */
    public static void encodeKeyValuePair(String key, JsonNode value, LineWriter writer, int depth,
                                          EncodeOptions options) {
        String encodedKey = PrimitiveEncoder.encodeKey(key);

        if (value.isValueNode()) {
            writer.push(depth, encodedKey + COLON + SPACE
                    + PrimitiveEncoder.encodePrimitive(value, options.delimiter().getValue()));
        } else if (value.isArray()) {
            ArrayEncoder.encodeArray(key, (ArrayNode) value, writer, depth, options);
        } else if (value.isObject()) {
            ObjectNode objValue = (ObjectNode) value;
            if (objValue.isEmpty()) {
                writer.push(depth, encodedKey + COLON);
            } else {
                writer.push(depth, encodedKey + COLON);
                encodeObject(objValue, writer, depth + 1, options);
            }
        }
    }
}
