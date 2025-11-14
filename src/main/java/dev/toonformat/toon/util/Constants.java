package dev.toonformat.toon.util;

/**
 * Constants used throughout the JToon encoding process.
 */
public final class Constants {

    // List markers
    public static final String LIST_ITEM_MARKER = "-";
    public static final String LIST_ITEM_PREFIX = "- ";

    // Structural characters
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String SPACE = " ";

    // Brackets and braces
    public static final String OPEN_BRACKET = "[";
    public static final String CLOSE_BRACKET = "]";
    public static final String OPEN_BRACE = "{";
    public static final String CLOSE_BRACE = "}";

    // Literals
    public static final String NULL_LITERAL = "null";
    public static final String TRUE_LITERAL = "true";
    public static final String FALSE_LITERAL = "false";

    // Escape characters
    public static final char BACKSLASH = '\\';
    public static final char DOUBLE_QUOTE = '"';

    private Constants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
