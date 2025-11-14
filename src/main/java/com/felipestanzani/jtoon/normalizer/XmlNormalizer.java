package com.felipestanzani.jtoon.normalizer;

import tools.jackson.databind.JsonNode;
import tools.jackson.dataformat.xml.XmlMapper;

/**
 * Normalizes XML strings to Jackson JsonNode representation.
 * Converts XML structure to JSON-compatible format for TOON encoding.
 */
public final class XmlNormalizer {

    private static final XmlMapper XML_MAPPER = XmlMapper.builder().build();

    private XmlNormalizer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Parses an XML string into a JsonNode using the shared XmlMapper.
     * <p>
     * This centralizes XML parsing concerns to keep the public API thin and
     * maintain separation of responsibilities between parsing, normalization,
     * and encoding.
     * </p>
     *
     * @param xml The XML string to parse (must be valid XML)
     * @return Parsed JsonNode
     * @throws IllegalArgumentException if the input is blank or not valid XML
     */
    public static JsonNode parse(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid XML");
        }
        try {
            return XML_MAPPER.readTree(xml);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid XML", e);
        }
    }
}
