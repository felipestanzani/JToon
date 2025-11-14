package dev.toonformat.toon.encoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Line writer that accumulates indented lines for building the final output.
 */
public final class LineWriter {
    private final List<String> lines = new ArrayList<>();
    private final String indentationString;

    /**
     * Creates a LineWriter with the specified indentation size.
     * 
     * @param indentSize Number of spaces per indentation level
     */
    public LineWriter(int indentSize) {
        this.indentationString = " ".repeat(indentSize);
    }

    /**
     * Adds a line with the specified depth and content.
     * 
     * @param depth   Indentation depth (0 = no indentation)
     * @param content Line content to add
     */
    public void push(int depth, String content) {
        String indent = indentationString.repeat(depth);
        lines.add(indent + content);
    }

    /**
     * Joins all accumulated lines with newlines.
     * 
     * @return The complete output string
     */
    @Override
    public String toString() {
        return String.join("\n", lines);
    }
}
