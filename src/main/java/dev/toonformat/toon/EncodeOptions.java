package dev.toonformat.toon;

/**
 * Configuration options for encoding data to JToon format.
 * 
 * @param indent       Number of spaces per indentation level (default: 2)
 * @param delimiter    Delimiter to use for tabular array rows and inline
 *                     primitive arrays (default: COMMA)
 * @param lengthMarker Optional marker to prefix array lengths in headers. When
 *                     true, arrays render as [#N] instead of [N] (default:
 *                     false)
 */
public record EncodeOptions(
        int indent,
        Delimiter delimiter,
        boolean lengthMarker) {
    /**
     * Default encoding options: 2 spaces indent, comma delimiter, no length marker
     */
    public static final EncodeOptions DEFAULT = new EncodeOptions(2, Delimiter.COMMA, false);

    /**
     * Creates EncodeOptions with default values.
     */
    public EncodeOptions() {
        this(2, Delimiter.COMMA, false);
    }

    /**
     * Creates EncodeOptions with custom indent, using default delimiter and length
     * marker.
     */
    public static EncodeOptions withIndent(int indent) {
        return new EncodeOptions(indent, Delimiter.COMMA, false);
    }

    /**
     * Creates EncodeOptions with custom delimiter, using default indent and length
     * marker.
     */
    public static EncodeOptions withDelimiter(Delimiter delimiter) {
        return new EncodeOptions(2, delimiter, false);
    }

    /**
     * Creates EncodeOptions with custom length marker, using default indent and
     * delimiter.
     */
    public static EncodeOptions withLengthMarker(boolean lengthMarker) {
        return new EncodeOptions(2, Delimiter.COMMA, lengthMarker);
    }
}
