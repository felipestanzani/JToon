package com.felipestanzani.jtoon.conformance.model;

public record JsonDecodeTestOptions(
        Integer indent,
        String delimiter,
        String lengthMarker,
        Boolean strict,
        String expandPaths) {
}

