package dev.toonformat.toon.conformance.model;

import java.util.List;

public record DecodeTestFixture(String version,
                                String category,
                                String description,
                                List<JsonDecodeTestCase> tests) {
}
