package com.felipestanzani.jtoon;

/**
 * Delimiter options for tabular array rows and inline primitive arrays.
 */
public enum Delimiter {
    /**
     * Comma delimiter (,) - default option
     */
    COMMA(","),

    /**
     * Tab delimiter (\t)
     */
    TAB("\t"),

    /**
     * Pipe delimiter (|)
     */
    PIPE("|");

    private final String value;

    Delimiter(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of this delimiter.
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
