![TOON logo with step‑by‑step guide](./.github/og.png)

# JToon - Token-Oriented Object Notation (TOON)

[![Build](https://github.com/felipestanzani/jtoon/actions/workflows/build.yml/badge.svg)](https://github.com/felipestanzani/jtoon/actions/workflows/build.yml)
[![Release](https://github.com/felipestanzani/jtoon/actions/workflows/release.yml/badge.svg)](https://github.com/felipestanzani/jtoon/actions/workflows/release.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.felipestanzani/jtoon.svg)](https://central.sonatype.com/artifact/com.felipestanzani/jtoon)

**Token-Oriented Object Notation** is a compact, human-readable format designed for passing structured data to Large Language Models with significantly reduced token usage.

TOON excels at **uniform complex objects** – multiple fields per row, same structure across items. It borrows YAML's indentation-based structure for nested objects and CSV's tabular format for uniform data rows, then optimizes both for token efficiency in LLM contexts.

## Why TOON?

AI is becoming cheaper and more accessible, but larger context windows allow for larger data inputs as well. **LLM tokens still cost money** – and standard JSON is verbose and token-expensive:

```json
{
  "users": [
    { "id": 1, "name": "Alice", "role": "admin" },
    { "id": 2, "name": "Bob", "role": "user" }
  ]
}
```

TOON conveys the same information with **fewer tokens**:

```
users[2]{id,name,role}:
  1,Alice,admin
  2,Bob,user
```

*Test the differences on [THIS online playground](https://www.curiouslychase.com/playground/format-tokenization-exploration)*

<details>
<summary>Another reason</summary>

[![xkcd: Standards](https://imgs.xkcd.com/comics/standards_2x.png)](https://xkcd.com/927/)

</details>

## Benchmarks

> **Learn more:** For complete format specification, rules, and additional benchmarks, see [TOON-SPECIFICATION.md](TOON-SPECIFICATION.md).

### Token Efficiency Example

TOON typically achieves **30–60% fewer tokens than JSON**. Here's a quick summary:

```
Total across 4 datasets        ████████████░░░░░░░░░░░░░  13,418 tokens
                               vs JSON: 26,379  💰 49.1% saved
                               vs XML:  30,494  💰 56.0% saved
```

**See [TOON-SPECIFICATION.md](TOON-SPECIFICATION.md#benchmarks) for detailed benchmark results and LLM retrieval accuracy tests.**

## Installation

### Maven Central

JToon is available on Maven Central. Add it to your project using your preferred build tool:

**Gradle (Groovy DSL):**

```gradle
dependencies {
    implementation 'com.felipestanzani:jtoon:0.1.2'
}
```

**Gradle (Kotlin DSL):**

```kotlin
dependencies {
    implementation("com.felipestanzani:jtoon:0.1.2")
}
```

**Maven:**

```xml
<dependency>
    <groupId>com.felipestanzani</groupId>
    <artifactId>jtoon</artifactId>
    <version>0.1.2</version>
</dependency>
```

> **Note:** See the [latest version](https://central.sonatype.com/artifact/com.felipestanzani/jtoon) on Maven Central (also shown in the badge above).

### Alternative: Manual Installation

You can also download the JAR directly from the [GitHub Releases](https://github.com/felipestanzani/jtoon/releases) page and add it to your project's classpath.

## Quick Start

```java
import com.felipestanzani.jtoon.JToon;
import java.util.*;

record User(int id, String name, List<String> tags, boolean active, List<?> preferences) {}
record Data(User user) {}

User user = new User(123, "Ada", List.of("reading", "gaming"), true, List.of());
Data data = new Data(user);

System.out.println(JToon.encode(data));
```

Output:

```
user:
  id: 123
  name: Ada
  tags[2]: reading,gaming
  active: true
  preferences[0]:
```

## TOON Format Basics

> **Complete specification:** For detailed formatting rules, quoting rules, and comprehensive examples, see [TOON-SPECIFICATION.md](TOON-SPECIFICATION.md).

TOON uses indentation-based structure (like YAML) combined with efficient tabular format for uniform arrays (like CSV):

**Simple objects:**

```
id: 123
name: Ada
```

**Nested objects:**

```
user:
  id: 123
  name: Ada
```

**Primitive arrays:**

```
tags[3]: admin,ops,dev
```

**Tabular arrays** (uniform objects with same fields):

```
items[2]{sku,qty,price}:
  A1,2,9.99
  B2,1,14.5
```

## Type Conversions

Some Java-specific types are automatically normalized for LLM-safe output:

| Input Type | Output |
|---|---|
| Number (finite) | Decimal form; `-0` → `0`; whole numbers as integers |
| Number (`NaN`, `±Infinity`) | `null` |
| `BigInteger` | Integer if within Long range, otherwise string (no quotes) |
| `BigDecimal` | Decimal number |
| `LocalDateTime` | ISO date-time string in quotes |
| `LocalDate` | ISO date string in quotes |
| `LocalTime` | ISO time string in quotes |
| `ZonedDateTime` | ISO zoned date-time string in quotes |
| `OffsetDateTime` | ISO offset date-time string in quotes |
| `Instant` | ISO instant string in quotes |
| `java.util.Date` | ISO instant string in quotes |
| `Optional<T>` | Unwrapped value or `null` if empty |
| `Stream<T>` | Materialized to array |
| `Map` | Object with string keys |
| `Collection`, arrays | Arrays |

Number normalization examples:

```
-0    → 0
1e6   → 1000000
1e-6  → 0.000001
```

## API

### `JToon.encode(Object value): String`

### `JToon.encode(Object value, EncodeOptions options): String`

### `JToon.encodeJson(String json): String`

### `JToon.encodeJson(String json, EncodeOptions options): String`

### `JToon.encodeXml(String xml): String`

### `JToon.encodeXml(String xml, EncodeOptions options): String`

Converts any Java object or JSON-string to TOON format.

**Parameters:**

- `value` – Any Java object (Map, List, primitive, or nested structure). Non-serializable values are converted to `null`. Java temporal types are converted to ISO strings, Optional is unwrapped, and Stream is materialized.
- `options` – Optional encoding options (`EncodeOptions` record):
  - `indent` – Number of spaces per indentation level (default: `2`)
  - `delimiter` – Delimiter enum for array values and tabular rows: `Delimiter.COMMA` (default), `Delimiter.TAB`, or `Delimiter.PIPE`
  - `lengthMarker` – Boolean to prefix array lengths with `#` (default: `false`)

For `encodeJson` overloads:

- `json` – A valid JSON string to be parsed and encoded. Invalid or blank JSON throws `IllegalArgumentException`.

For `encodeXml` overloads:

- `xml` – A valid XML string to be parsed and encoded. Invalid or blank XML throws `IllegalArgumentException`.

**Returns:**

A TOON-formatted string with no trailing newline or spaces.

**Example:**

```java
import com.felipestanzani.jtoon.JToon;
import java.util.*;

record Item(String sku, int qty, double price) {}
record Data(List<Item> items) {}

Item item1 = new Item("A1", 2, 9.99);
Item item2 = new Item("B2", 1, 14.5);
Data data = new Data(List.of(item1, item2));

System.out.println(JToon.encode(data));
```

**Output:**

```
items[2]{sku,qty,price}:
  A1,2,9.99
  B2,1,14.5
```

#### Encode a plain JSON string

```java
String json = """
{
  "user": {
    "id": 123,
    "name": "Ada",
    "tags": ["reading", "gaming"]
  }
}
""";
System.out.println(JToon.encodeJson(json));
```

Output:

```
user:
  id: 123
  name: Ada
  tags[2]: reading,gaming
```

#### Encode XML

```java
String xml = "<user><name>John</name><age>25</age></user>";
System.out.println(JToon.encodeXml(xml));
```

Output:

```
user:
  name: John
  age: 25
```

#### XML to TOON Conversion Use Cases

XML to TOON conversion is particularly useful in scenarios where:

- Legacy System Integration**: Converting XML APIs or data feeds from older systems to TOON for efficient LLM processing
- Configuration Files**: Transforming XML configuration files to TOON format for AI-assisted configuration analysis
- Data Exchange**: Converting XML data exchange formats to TOON for reduced token usage in AI conversations
- Log Analysis**: Processing XML formatted logs and converting them to TOON for AI-powered log analysis
- Web Services**: Converting SOAP XML responses or REST XML payloads to TOON for AI interpretation

For example, converting a complex XML document:
```xml
<company>
  <name>TechCorp</name>
  <departments>
    <department>
      <name>Engineering</name>
      <employees>50</employees>
    </department>
    <department>
      <name>Marketing</name>
      <employees>20</employees>
    </department>
  </departments>
</company>
```

To TOON:
```
company:
  name: TechCorp
  departments[2]{name,employees}:
    Engineering,50
    Marketing,20
```
This conversion provides significant token savings while maintaining the hierarchical structure of the original XML.

#### Delimiter Options

The `delimiter` option allows you to choose between comma (default), tab, or pipe delimiters for array values and tabular rows. Alternative delimiters can provide additional token savings in specific contexts.

##### Tab Delimiter (`\t`)

Using tab delimiters instead of commas can reduce token count further, especially for tabular data:

```java
import com.felipestanzani.jtoon.*;
import java.util.*;

record Item(String sku, String name, int qty, double price) {}
record Data(List<Item> items) {}

Item item1 = new Item("A1", "Widget", 2, 9.99);
Item item2 = new Item("B2", "Gadget", 1, 14.5);
Data data = new Data(List.of(item1, item2));

EncodeOptions options = new EncodeOptions(2, Delimiter.TAB, false);
System.out.println(JToon.encode(data, options));
```

**Output:**

```
items[2 ]{sku name qty price}:
  A1 Widget 2 9.99
  B2 Gadget 1 14.5
```

**Benefits:**

- Tabs are single characters and often tokenize more efficiently than commas.
- Tabs rarely appear in natural text, reducing the need for quote-escaping.
- The delimiter is explicitly encoded in the array header, making it self-descriptive.

**Considerations:**

- Some terminals and editors may collapse or expand tabs visually.
- String values containing tabs will still require quoting.

##### Pipe Delimiter (`|`)

Pipe delimiters offer a middle ground between commas and tabs:

```java
// Using the same Item and Data records from above
EncodeOptions options = new EncodeOptions(2, Delimiter.PIPE, false);
System.out.println(JToon.encode(data, options));
```

**Output:**

```
items[2|]{sku|name|qty|price}:
  A1|Widget|2|9.99
  B2|Gadget|1|14.5
```

#### Length Marker Option

The `lengthMarker` option adds an optional hash (`#`) prefix to array lengths to emphasize that the bracketed value represents a count, not an index:

```java
import com.felipestanzani.jtoon.*;
import java.util.*;

record Item(String sku, int qty, double price) {}
record Data(List<String> tags, List<Item> items) {}

Item item1 = new Item("A1", 2, 9.99);
Item item2 = new Item("B2", 1, 14.5);
Data data = new Data(List.of("reading", "gaming", "coding"), List.of(item1, item2));

System.out.println(JToon.encode(data, new EncodeOptions(2, Delimiter.COMMA, true)));
// tags[#3]: reading,gaming,coding
// items[#2]{sku,qty,price}:
//   A1,2,9.99
//   B2,1,14.5

// Works with custom delimiters
System.out.println(JToon.encode(data, new EncodeOptions(2, Delimiter.PIPE, true)));
// tags[#3|]: reading|gaming|coding
// items[#2|]{sku|qty|price}:
//   A1|2|9.99
//   B2|1|14.5
```

### `JToon.decode(String toon): Object`

### `JToon.decode(String toon, DecodeOptions options): Object`

### `JToon.decodeToJson(String toon): String`

### `JToon.decodeToJson(String toon, DecodeOptions options): String`

Converts TOON-formatted strings back to Java objects or JSON.

**Parameters:**

- `toon` – TOON-formatted input string
- `options` – Optional decoding options (`DecodeOptions` record):
  - `indent` – Number of spaces per indentation level (default: `2`)
  - `delimiter` – Expected delimiter: `Delimiter.COMMA` (default), `Delimiter.TAB`, or `Delimiter.PIPE`
  - `strict` – Boolean for validation mode. When `true` (default), throws `IllegalArgumentException` on invalid input. When `false`, returns `null` on errors.

**Returns:**

For `decode`: A Java object (`Map` for objects, `List` for arrays, primitives for scalars, or `null`)

For `decodeToJson`: A JSON string representation

**Example:**

```java
import com.felipestanzani.jtoon.JToon;

String toon = """
    users[2]{id,name,role}:
      1,Alice,admin
      2,Bob,user
    """;

// Decode to Java objects
Object result = JToon.decode(toon);

// Decode directly to JSON string
String json = JToon.decodeToJson(toon);
```

#### Round-Trip Conversion

```java
import com.felipestanzani.jtoon.*;
import java.util.*;

// Original data
Map<String, Object> data = new LinkedHashMap<>();
data.put("id", 123);
data.put("name", "Ada");
data.put("tags", Arrays.asList("dev", "admin"));

// Encode to TOON
String toon = JToon.encode(data);

// Decode back to objects
Object decoded = JToon.decode(toon);

// Values are preserved (note: integers decode as Long)
```

#### Custom Decode Options

```java
import com.felipestanzani.jtoon.*;

String toon = "tags[3|]: a|b|c";

// Decode with pipe delimiter
DecodeOptions options = new DecodeOptions(2, Delimiter.PIPE, true);
Object result = JToon.decode(toon, options);

// Lenient mode (returns null on errors instead of throwing)
DecodeOptions lenient = DecodeOptions.withStrict(false);
Object result2 = JToon.decode(invalidToon, lenient);
```

## See Also

- **[TOON Format Specification](TOON-SPECIFICATION.md)** – Complete format rules, benchmarks, and examples
- **[Changelog](CHANGELOG.md)** – Version history and notable changes

## Implementations in Other Languages

- **TypeScript/JavaScript**: [@johannschopplich/toon](https://github.com/johannschopplich/toon) (original)
- **Elixir:** [toon_ex](https://github.com/kentaro/toon_ex)
- **PHP:** [toon-php](https://github.com/HelgeSverre/toon-php)
- **Python:** [python-toon](https://github.com/xaviviro/python-toon) or [pytoon](https://github.com/bpradana/pytoon)
- **Ruby:** [toon-ruby](https://github.com/andrepcg/toon-ruby)
- **Java:** [JToon](https://github.com/felipestanzani/JToon)
- **.NET:** [toon.NET](https://github.com/ghost1face/toon.NET)
- **Swift:** [TOONEncoder](https://github.com/mattt/TOONEncoder)
- **Go:** [gotoon](https://github.com/alpkeskin/gotoon)
- **Rust:** [toon-rs](https://github.com/JadJabbour/toon-rs)

## License

[MIT](./LICENSE) License © 2025-PRESENT [Felipe Stanzani](https://felipestanzani.com)
