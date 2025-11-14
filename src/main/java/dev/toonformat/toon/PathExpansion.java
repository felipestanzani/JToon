package dev.toonformat.toon;

/**
 * Path expansion mode for decoding dotted keys.
 */
public enum PathExpansion {
    /**
     * Safe mode: expands dotted keys like "a.b.c" into nested objects.
     * Only expands keys that are valid identifier segments.
     */
    SAFE,
    
    /**
     * Off mode: treats dotted keys as literal keys.
     */
    OFF
}

