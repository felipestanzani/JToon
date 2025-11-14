package dev.toonformat.toon.conformance.model;

import java.util.List;

public record EncodeTestFixture(String version,
                                String category,
                                String description,
                                List<JsonEncodeTestCase> tests) {
}
