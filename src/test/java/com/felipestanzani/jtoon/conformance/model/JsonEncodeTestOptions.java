package com.felipestanzani.jtoon.conformance.model;

public record JsonEncodeTestOptions(
        Integer indent,
        String delimiter,
        String lengthMarker) {
}

