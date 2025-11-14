package dev.toonformat.toon.normalizer;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.DoubleNode;
import tools.jackson.databind.node.FloatNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.ShortNode;
import tools.jackson.databind.node.StringNode;
import tools.jackson.module.afterburner.AfterburnerModule;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Normalizes Java objects to Jackson JsonNode representation.
 * Handles Java-specific types like LocalDateTime, Optional, Stream, etc.
 */
public final class JsonNormalizer {
    public final static ObjectMapper MAPPER;

    static {
        MAPPER = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.ALWAYS))
                .addModule(new AfterburnerModule().setUseValueClassLoader(true)) // Speeds up Jackson by 20–40% in most real-world cases
                // .disable(MapperFeature.DEFAULT_VIEW_INCLUSION) in Jackson 3 this is default disabled
                // .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) in Jackson 3 this is default disabled
                .defaultTimeZone(TimeZone.getTimeZone("UTC")) // set a default timezone for dates
                .build();
    }

    private static final List<Function<Object, JsonNode>> NORMALIZERS = List.of(
            JsonNormalizer::tryNormalizePrimitive,
            JsonNormalizer::tryNormalizeBigNumber,
            JsonNormalizer::tryNormalizeTemporal,
            JsonNormalizer::tryNormalizeCollection,
            JsonNormalizer::tryNormalizePojo);

    private JsonNormalizer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Parses a JSON string into a JsonNode using the shared ObjectMapper.
     * <p>
     * This centralizes JSON parsing concerns to keep the public API thin and
     * maintain separation of responsibilities between parsing, normalization,
     * and encoding.
     * </p>
     *
     * @param json The JSON string to parse (must be non-blank)
     * @return Parsed JsonNode
     * @throws IllegalArgumentException if the input is blank or not valid JSON
     */
    public static JsonNode parse(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid JSON");
        }
        try {
            return MAPPER.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON", e);
        }
    }

    /**
     * Normalizes any Java object to a JsonNode.
     *
     * @param value The value to normalize
     * @return The normalized JsonNode
     */
    public static JsonNode normalize(Object value) {
        if (value == null) {
            return NullNode.getInstance();
        } else if (value instanceof JsonNode) {
            return (JsonNode) value;
        } else if (value instanceof Optional<?>) {
            return normalize(((Optional<?>) value).orElse(null));
        } else if (value instanceof Stream<?>) {
            return normalize(((Stream<?>) value).collect(Collectors.toList()));
        } else if (value.getClass().isArray()) {
            return normalizeArray(value);
        } else {
            return normalizeWithStrategy(value);
        }
    }

    /**
     * Attempts normalization using chain of responsibility pattern.
     */
    private static JsonNode normalizeWithStrategy(Object value) {
        return NORMALIZERS.stream()
                .map(normalizer -> normalizer.apply(value))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(NullNode.getInstance());
    }

    /**
     * Attempts to normalize primitive types and their wrappers.
     * Returns null if the value is not a primitive type.
     */
    private static JsonNode tryNormalizePrimitive(Object value) {
        if (value instanceof String) {
            return StringNode.valueOf((String) value);
        } else if (value instanceof Boolean) {
            return BooleanNode.valueOf((Boolean) value);
        } else if (value instanceof Integer) {
            return IntNode.valueOf((Integer) value);
        } else if (value instanceof Long) {
            return LongNode.valueOf((Long) value);
        } else if (value instanceof Double) {
            return normalizeDouble((Double) value);
        } else if (value instanceof Float) {
            return normalizeFloat((Float) value);
        } else if (value instanceof Short) {
            return ShortNode.valueOf((Short) value);
        } else if (value instanceof Byte) {
            return IntNode.valueOf((Byte) value);
        } else {
            return null;
        }
    }

    /**
     * Normalizes Double values handling special cases.
     */
    private static JsonNode normalizeDouble(Double value) {
        if (!Double.isFinite(value)) {
            return NullNode.getInstance();
        }
        if (value == 0.0) {
            return IntNode.valueOf(0);
        }
        return tryConvertToLong(value)
                .orElse(DoubleNode.valueOf(value));
    }

    /**
     * Normalizes Float values handling special cases.
     */
    private static JsonNode normalizeFloat(Float value) {
        return Float.isFinite(value)
                ? FloatNode.valueOf(value)
                : NullNode.getInstance();
    }

    /**
     * Attempts to convert a double to a long if it's a whole number.
     */
    private static Optional<JsonNode> tryConvertToLong(Double value) {
        if (value != Math.floor(value)) {
            return Optional.empty();
        }
        if (value > Long.MAX_VALUE || value < Long.MIN_VALUE) {
            return Optional.empty();
        }
        long longVal = value.longValue();
        return longVal == value
                ? Optional.of(LongNode.valueOf(longVal))
                : Optional.empty();
    }

    /**
     * Attempts to normalize BigInteger and BigDecimal.
     * Returns null if the value is not a big number type.
     */
    private static JsonNode tryNormalizeBigNumber(Object value) {
        if (value instanceof BigInteger) {
            return normalizeBigInteger((BigInteger) value);
        } else if (value instanceof BigDecimal) {
            return DecimalNode.valueOf((BigDecimal) value);
        } else {
            return null;
        }
    }

    /**
     * Normalizes BigInteger, converting to long if within range.
     */
    private static JsonNode normalizeBigInteger(BigInteger value) {
        boolean fitsInLong = value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0
                && value.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) >= 0;
        return fitsInLong
                ? LongNode.valueOf(value.longValue())
                : StringNode.valueOf(value.toString());
    }

    /**
     * Attempts to normalize temporal types (date/time) to ISO strings.
     * Returns null if the value is not a temporal type.
     */
    private static JsonNode tryNormalizeTemporal(Object value) {
        if (value instanceof LocalDateTime) {
            return formatTemporal((LocalDateTime) value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else if (value instanceof LocalDate) {
            return formatTemporal((LocalDate) value, DateTimeFormatter.ISO_LOCAL_DATE);
        } else if (value instanceof LocalTime) {
            return formatTemporal((LocalTime) value, DateTimeFormatter.ISO_LOCAL_TIME);
        } else if (value instanceof ZonedDateTime) {
            return formatTemporal((ZonedDateTime) value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        } else if (value instanceof OffsetDateTime) {
            return formatTemporal((OffsetDateTime) value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else if (value instanceof Instant) {
            return StringNode.valueOf(((Instant) value).toString());
        } else if (value instanceof java.util.Date) {
            return StringNode.valueOf(((java.util.Date) value).toInstant().toString());
        } else {
            return null;
        }
    }

    /**
     * Helper method to format temporal values consistently.
     */
    private static <T> JsonNode formatTemporal(T temporal, DateTimeFormatter formatter) {
        return StringNode.valueOf(formatter.format((java.time.temporal.TemporalAccessor) temporal));
    }

    /**
     * Attempts to normalize collections (Collection and Map).
     * Returns null if the value is not a collection type.
     */
    private static JsonNode tryNormalizeCollection(Object value) {
        if (value instanceof Collection<?>) {
            return normalizeCollection((Collection<?>) value);
        } else if (value instanceof Map<?, ?>) {
            return normalizeMap((Map<?, ?>) value);
        } else {
            return null;
        }
    }

    /**
     * Normalizes a Collection to an ArrayNode.
     */
    private static ArrayNode normalizeCollection(Collection<?> collection) {
        ArrayNode arrayNode = MAPPER.createArrayNode();
        collection.forEach(item -> arrayNode.add(normalize(item)));
        return arrayNode;
    }

    /**
     * Normalizes a Map to an ObjectNode.
     */
    private static ObjectNode normalizeMap(Map<?, ?> map) {
        ObjectNode objectNode = MAPPER.createObjectNode();
        map.forEach((key, value) -> objectNode.set(String.valueOf(key), normalize(value)));
        return objectNode;
    }

    /**
     * Attempts to normalize POJOs using Jackson's default conversion.
     * Returns null for non-serializable objects.
     */
    private static JsonNode tryNormalizePojo(Object value) {
        try {
            return MAPPER.valueToTree(value);
        } catch (IllegalArgumentException e) {
            return NullNode.getInstance();
        }
    }

    /**
     * Normalizes arrays to ArrayNode.
     */
    private static JsonNode normalizeArray(Object array) {
        if (array instanceof int[]) {
            int[] arr = (int[]) array;
            return buildArrayNode(arr.length, i -> IntNode.valueOf(arr[i]));
        } else if (array instanceof long[]) {
            long[] arr = (long[]) array;
            return buildArrayNode(arr.length, i -> LongNode.valueOf(arr[i]));
        } else if (array instanceof double[]) {
            double[] arr = (double[]) array;
            return buildArrayNode(arr.length, i -> normalizeDoubleElement(arr[i]));
        } else if (array instanceof float[]) {
            float[] arr = (float[]) array;
            return buildArrayNode(arr.length, i -> normalizeFloatElement(arr[i]));
        } else if (array instanceof boolean[]) {
            boolean[] arr = (boolean[]) array;
            return buildArrayNode(arr.length, i -> BooleanNode.valueOf(arr[i]));
        } else if (array instanceof byte[]) {
            byte[] arr = (byte[]) array;
            return buildArrayNode(arr.length, i -> IntNode.valueOf(arr[i]));
        } else if (array instanceof short[]) {
            short[] arr = (short[]) array;
            return buildArrayNode(arr.length, i -> ShortNode.valueOf(arr[i]));
        } else if (array instanceof char[]) {
            char[] arr = (char[]) array;
            return buildArrayNode(arr.length, i -> StringNode.valueOf(String.valueOf(arr[i])));
        } else if (array instanceof Object[]) {
            Object[] arr = (Object[]) array;
            return buildArrayNode(arr.length, i -> normalize(arr[i]));
        } else {
            return MAPPER.createArrayNode();
        }
    }

    /**
     * Builds an ArrayNode using a functional approach.
     */
    private static ArrayNode buildArrayNode(int length, IntFunction<JsonNode> mapper) {
        ArrayNode arrayNode = MAPPER.createArrayNode();
        for (int i = 0; i < length; i++) {
            arrayNode.add(mapper.apply(i));
        }
        return arrayNode;
    }

    /**
     * Normalizes a single double element from an array.
     */
    private static JsonNode normalizeDoubleElement(double value) {
        return Double.isFinite(value)
                ? DoubleNode.valueOf(value)
                : NullNode.getInstance();
    }

    /**
     * Normalizes a single float element from an array.
     */
    private static JsonNode normalizeFloatElement(float value) {
        return Float.isFinite(value)
                ? FloatNode.valueOf(value)
                : NullNode.getInstance();
    }
}
