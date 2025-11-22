package dev.toonformat.jtoon.encoder;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Recursively flattens a JSON object or array into a single-level object.
 */
public class Flatten {

    private Flatten() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Represents the result of a key-folding operation.
     *
     * @param foldedKey    the final folded dot-separated key
     * @param remainder    the tail value remaining after folding, or null
     * @param leafValue    the leaf JSON value of the folded chain
     * @param segmentCount number of folded segments
     */
    public record FoldResult(String foldedKey,
                             JsonNode remainder,
                             JsonNode leafValue,
                             int segmentCount) {
    }

    /**
     * Represents the result of the Collect segments of the single-key chain
     *
     * @param segments  collected single-key object
     * @param tail      the tail node (if any)
     * @param leafValue the leaf JsonValue
     */
    private record ChainResult(List<String> segments, JsonNode tail, JsonNode leafValue) {
    }

    /**
     * Attempts to fold a JSON object chain starting with the given key.
     * Folding proceeds only when:
     * - Safe mode is enabled
     * - The value is an {@link ObjectNode}
     * - The object chain consists of nested objects with exactly one key each
     * - All segments satisfy safe identifier rules
     * - No key collisions occur with sibling keys or previously used dotted keys
     * - The configured depth limit is not exceeded
     *
     * @param key             the starting key to fold
     * @param value           the Jackson {@link JsonNode} associated with the key
     * @param siblings        set of sibling keys for collision detection
     * @param rootLiteralKeys optional set of dotted keys at the root level to avoid collisions
     * @param pathPrefix      optional parent dotted path (for absolute collision checks)
     * @param remainingDepth  the remaining depth of the object
     * @return a {@link FoldResult}, or null if folding is not possible
     */
    public static FoldResult tryFoldKeyChain(String key,
                                             JsonNode value,
                                             Set<String> siblings,
                                             Set<String> rootLiteralKeys,
                                             String pathPrefix,
                                             Integer remainingDepth) {
        // Must be an object to begin folding
        if (!value.isObject()) {
            return null;
        }

        // start chain from absolute key
        String absKey = (pathPrefix == null) ? key : pathPrefix + "." + key;

        // Collect segments of the single-key chain
        final ChainResult chain = collectSingleKeyChain(absKey, value, remainingDepth);

        // Minimum of 2 segments required to justify folding
        if (chain.segments.size() < 2) {
            return null;
        }

        // Validate safe identifier segments
        // This rules:
        //  - First character must be a letter or underscore
        //  - Remaining characters must be alphanumeric or underscore
        //  - No dots, hyphens, or special characters are allowed
        for (String seg : chain.segments) {
            if (seg == null || seg.isEmpty() || !seg.matches("(?i)^[A-Z_]\\w*$")) {
                return null;
            }
        }

        // Build folded key
        String foldedKey = String.join(".", chain.segments);

        // Compute absolute dotted path
        String absolutePath =
                (pathPrefix != null && !pathPrefix.isEmpty())
                        ? pathPrefix + "." + foldedKey
                        : foldedKey;

        // Detect collisions with sibling keys
        if (siblings.contains(foldedKey)) {
            return null;
        }

        // Detect collisions with literal dotted keys at root scope
        if (rootLiteralKeys != null && rootLiteralKeys.contains(absolutePath)) {
            return null;
        }
        return new FoldResult(
                foldedKey,
                chain.tail,
                chain.leafValue,
                chain.segments.size()
        );
    }

    /**
     * Traverses nested single-key {@link ObjectNode} values, collecting the
     * sequence of keys until one of the following occurs:
     * - A non-object value is encountered
     * - An object with zero or more than one key is encountered
     * - An empty object is encountered (treated as a leaf)
     * - The maximum folding depth is reached
     *
     * @param startKey   the initial key
     * @param startValue the JSON value associated with the key
     * @param maxDepth   maximum number of allowed segments
     * @return a {@link ChainResult} containing segments, tail, and leafValue
     */
    private static ChainResult collectSingleKeyChain(String startKey, JsonNode startValue, int maxDepth) {
        // normalize absolute key to its local segment
        String localStartKey = startKey.contains(".")
                ? startKey.substring(startKey.lastIndexOf('.') + 1)
                : startKey;

        final List<String> segments = new ArrayList<>();
        segments.add(localStartKey);

        JsonNode currentValue = startValue;
        // track depth of folding
        int depthCounter = 1;

        while (true) {
            if (!currentValue.isObject()
                    || currentValue.size() != 1
                    || depthCounter >= maxDepth
            ) {
                break;
            }

            final ObjectNode obj = (ObjectNode) currentValue;
            final Iterator<String> it = obj.propertyNames().iterator();

            // No keys that mines empty object leaf
            if (!it.hasNext()) {
                return new ChainResult(segments, null, currentValue);
            }

            final String nextKey = it.next();

            // More than one key mines stop chain here
            if (it.hasNext()) {
                break;
            }

            final JsonNode nextValue = obj.get(nextKey);
            segments.add(nextKey);
            currentValue = nextValue;

            depthCounter++;
        }

        // Determine whether this is a tail or leaf
        if (currentValue.isObject()) {
            final ObjectNode obj = (ObjectNode) currentValue;
            if (obj.isEmpty()) {
                // empty object is a leaf
                return new ChainResult(segments, null, currentValue);
            }

            // If the object has exactly ONE key, it should be part of the chain,
            // not a tail. So treat it as a leaf.
            if (obj.size() == 1) {
                return new ChainResult(segments, null, currentValue);
            }

            // Otherwise it has extra keys that mines this is a legitimate tail
            return new ChainResult(segments, currentValue, null);
        }

        // Primitive or array leaf
        return new ChainResult(segments, null, currentValue);
    }

}
