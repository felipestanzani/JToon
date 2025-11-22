package dev.toonformat.jtoon.conformance.model;

public record JsonEncodeTestOptions(
        Integer indent,
        String delimiter,
        String lengthMarker,
        String keyFolding,
        Integer flattenDepth) {
}

