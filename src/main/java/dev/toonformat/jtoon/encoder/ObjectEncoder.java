package dev.toonformat.jtoon.encoder;


import dev.toonformat.jtoon.EncodeOptions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

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
     * @param value           The ObjectNode to encode
     * @param writer          LineWriter for accumulating output
     * @param depth           Current indentation depth
     * @param options         Encoding options
     * @param rootLiteralKeys optional set of dotted keys at the root level to avoid collisions
     * @param pathPrefix      optional parent dotted path (for absolute collision checks)
     * @param remainingDepth  optional override for the remaining depth
     */
    public static void encodeObject(ObjectNode value, LineWriter writer, int depth, EncodeOptions options, Set<String> rootLiteralKeys, String pathPrefix, Integer remainingDepth) {
        Collection<String> fieldNames = value.propertyNames();

        // At root level (depth 0), collect all literal dotted keys for collision checking
        if (depth == 0 && rootLiteralKeys != null) {
            rootLiteralKeys.clear();
            fieldNames.stream()
                    .filter(k -> k.contains("."))
                    .forEach(rootLiteralKeys::add);
        }
        int effectiveFlattenDepth = remainingDepth != null ? remainingDepth : options.flattenDepth();

        for (String fieldName : fieldNames) {
            JsonNode fieldValue = value.get(fieldName);
            Set<String> siblings = fieldNames.stream()
                    .map(fn -> pathPrefix == null ? fn : pathPrefix + "." + fn)
                    .collect(Collectors.toSet());
            encodeKeyValuePair(fieldName, fieldValue, writer, depth, options, siblings, rootLiteralKeys, pathPrefix, effectiveFlattenDepth);
        }
    }

    /**
     * Encodes a key-value pair in an object.
     *
     * @param key             the key name
     * @param value           the value to encode
     * @param writer          the LineWriter for accumulating output
     * @param depth           the current indentation depth
     * @param options         encoding options
     * @param siblings        set of sibling keys for collision detection
     * @param rootLiteralKeys optional set of dotted keys at the root level to avoid collisions
     * @param pathPrefix      optional parent dotted path (for absolute collision checks)
     * @param flattenDepth    optional override for depth limit
     */
    public static void encodeKeyValuePair(String key,
                                          JsonNode value,
                                          LineWriter writer,
                                          int depth,
                                          EncodeOptions options,
                                          Set<String> siblings,
                                          Set<String> rootLiteralKeys,
                                          String pathPrefix,
                                          Integer flattenDepth
    ) {
        String encodedKey = PrimitiveEncoder.encodeKey(key);
        String currentPath = pathPrefix != null ? pathPrefix + "." + key : key;
        int effectiveFlattenDepth = flattenDepth != null && flattenDepth > 0 ? flattenDepth : options.flattenDepth();

        int remainingDepth = effectiveFlattenDepth - depth;

        // Attempt key folding when enabled
        if (options.flatten() && !siblings.isEmpty() && remainingDepth > 0) {
            Flatten.FoldResult foldResult = Flatten.tryFoldKeyChain(key, value, siblings, rootLiteralKeys, pathPrefix, remainingDepth);
            if (foldResult != null) {
                // prevent second folding pass
                siblings.remove(key);
                siblings.remove(foldResult.foldedKey());

                String encodedFoldedKey = PrimitiveEncoder.encodeKey(foldResult.foldedKey());

                JsonNode remainder = foldResult.remainder();
                // Case 1: Fully folded to a leaf value
                if (remainder == null) {
                    // The folded chain ended at a leaf (primitive, array, or empty object)
                    JsonNode leafValue = foldResult.leafValue();
                    if (leafValue.isValueNode()) {
                        String primitiveEncodedFoldedKey = PrimitiveEncoder.encodeKey(foldResult.foldedKey());
                        writer.push(depth, indentedLine(depth, primitiveEncodedFoldedKey + ": " + PrimitiveEncoder.encodePrimitive(foldResult.leafValue(), options.delimiter().getValue()), options.indent()));
                        return;
                    } else if (leafValue.isArray()) {
                        ArrayEncoder.encodeArray(foldResult.foldedKey(), (ArrayNode) leafValue, writer, depth, options);
                        return;
                    } else if (leafValue.isObject()) {
                        // Always write the folded key first
                        writer.push(depth, indentedLine(depth, encodedFoldedKey + ":", options.indent()));
                        if (!leafValue.isEmpty()) {
                            encodeObject((ObjectNode) leafValue, writer, depth + 1, options, rootLiteralKeys, null, null);
                        }
                    }
                }

                // Case 2: Partially folded with a tail object
                if (remainder != null && remainder.isObject()) {
                    writer.push(depth, indentedLine(depth, encodedFoldedKey + ":", options.indent()));
                    String foldedPath = pathPrefix != null ? pathPrefix + "." + foldResult.foldedKey() : foldResult.foldedKey();
                    int newRemainingDepth = remainingDepth - foldResult.segmentCount();
                    if (newRemainingDepth <= 0) {
                        // Pass "-1" if remainingDepth is exhausted and set the encoding in the option to false.
                        // to encode normally without flattening
                        newRemainingDepth = -1;
                        options = new EncodeOptions(options.indent(), options.delimiter(), options.lengthMarker(), false, options.flattenDepth());
                    }
                    encodeObject((ObjectNode) remainder, writer, depth + 1, options, rootLiteralKeys, foldedPath, newRemainingDepth);
                }

                return;
            }
        }

        if (value.isValueNode()) {
            writer.push(depth, encodedKey + COLON + SPACE + PrimitiveEncoder.encodePrimitive(value, options.delimiter().getValue()));
        } else if (value.isArray()) {
            ArrayEncoder.encodeArray(key, (ArrayNode) value, writer, depth, options);
        } else if (value.isObject()) {
            ObjectNode objValue = (ObjectNode) value;
            writer.push(depth, encodedKey + COLON);
            if (!objValue.isEmpty()) {
                encodeObject(objValue, writer, depth + 1, options, rootLiteralKeys, currentPath, effectiveFlattenDepth);
            }
        }
    }

    private static String indentedLine(int depth, String content, int indentSize) {
        return "%s%s".formatted(" ".repeat(indentSize * depth), content);
    }
}
