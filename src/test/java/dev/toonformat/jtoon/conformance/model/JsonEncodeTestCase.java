package dev.toonformat.jtoon.conformance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

public record JsonEncodeTestCase(String name,
                                 JsonNode input,
                                 String expected,
                                 @JsonProperty("specSection") String spec,
                                 @JsonIgnore String note,
                                 JsonEncodeTestOptions options) {
}
