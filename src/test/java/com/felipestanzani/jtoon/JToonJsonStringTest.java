package com.felipestanzani.jtoon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@DisplayName("JToon.encodeJson - JSON string entry point")
public class JToonJsonStringTest {

    @Nested
    @DisplayName("happy paths")
    class HappyPaths {

        @Test
        @DisplayName("encodes simple object")
        void encodesSimpleObject() {
            String json = "{\"id\":123,\"name\":\"Ada\"}";
            String result = JToon.encodeJson(json);
            assertEquals("id: 123\nname: Ada", result);
        }

        @Test
        @DisplayName("encodes primitive array inline")
        void encodesPrimitiveArray() {
            String json = "{\"tags\":[\"admin\",\"ops\",\"dev\"]}";
            String result = JToon.encodeJson(json);
            assertEquals("tags[3]: admin,ops,dev", result);
        }

        @Test
        @DisplayName("encodes uniform objects as tabular array")
        void encodesTabularArray() {
            String json = "{\"items\":[{\"sku\":\"A1\",\"qty\":2,\"price\":9.99},{\"sku\":\"B2\",\"qty\":1,\"price\":14.5}]}";
            String result = JToon.encodeJson(json);
            String expected = String.join("\n",
                    "items[2]{sku,qty,price}:",
                    "  A1,2,9.99",
                    "  B2,1,14.5");
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("supports custom options with pipe delimiter and length marker")
        void encodesWithCustomOptions() {
            String json = "{\"tags\":[\"reading\",\"gaming\",\"coding\"],\"items\":[{\"sku\":\"A1\",\"qty\":2,\"price\":9.99},{\"sku\":\"B2\",\"qty\":1,\"price\":14.5}]}";
            EncodeOptions options = new EncodeOptions(2, Delimiter.PIPE, true);
            String result = JToon.encodeJson(json, options);

            String expected = String.join("\n",
                    "tags[#3|]: reading|gaming|coding",
                    "items[#2|]{sku|qty|price}:",
                    "  A1|2|9.99",
                    "  B2|1|14.5");

            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("errors")
    class Errors {

        @Test
        @DisplayName("throws on invalid JSON")
        void throwsOnInvalidJson() {
            assertThrows(IllegalArgumentException.class, () -> JToon.encodeJson("{invalid}"));
        }

        @Test
        @DisplayName("throws on blank JSON")
        void throwsOnBlankJson() {
            assertThrows(IllegalArgumentException.class, () -> JToon.encodeJson("   \n \t  "));
        }
    }
}


