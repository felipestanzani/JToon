package com.felipestanzani.jtoon.decoder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for PrimitiveDecoder utility class.
 * Tests decoding of primitive values, keys, and header formatting.
 */
@Tag("unit")
public class PrimitiveDecoderTest {

    @Test
    @DisplayName("throws unsupported Operation Exception for calling the constructor")
    void throwsOnConstructor() throws NoSuchMethodException {
        final Constructor<PrimitiveDecoder> constructor = PrimitiveDecoder.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException thrown =
                assertThrows(InvocationTargetException.class, constructor::newInstance);

        final Throwable cause = thrown.getCause();
        assertInstanceOf(UnsupportedOperationException.class, cause);
        assertEquals("Utility class cannot be instantiated", cause.getMessage());
    }
}
