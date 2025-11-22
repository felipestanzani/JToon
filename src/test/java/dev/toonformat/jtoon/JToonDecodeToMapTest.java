package dev.toonformat.jtoon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class JToonDecodeToMapTest {

    @Test
    @DisplayName("should return a Map<String, Object>")
    void simpleMapDecode() {
        String toon = """
                    id: 123
                    name: Ada
                    active: true
                    """;
        Map<String, Object> map = JToon.decodeToMap(toon);
        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertNotNull(map.get("id"));
        assertNotNull(map.get("name"));
        assertNotNull(map.get("active"));
    }

    @Test
    @DisplayName("if the given input is null an empty Map is returned")
    void nullInput() {
        Map<String, Object> map = JToon.decodeToMap(null);
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("if the given input is an invalid toon string, and empty Map is returned")
    void nullResult() {
        String toon = "This String is invalid";
        Map<String, Object> map = JToon.decodeToMap(toon);
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }
}
