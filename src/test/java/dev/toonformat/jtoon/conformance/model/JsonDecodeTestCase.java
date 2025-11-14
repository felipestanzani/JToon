package dev.toonformat.jtoon.conformance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

public record JsonDecodeTestCase(String name,
                                 JsonNode input,
                                 JsonNode expected,
                                 String specSection,
                                 @JsonIgnore String note,
                                 JsonDecodeTestOptions options,
                                 @JsonProperty("shouldError") Boolean shouldError) {
}
