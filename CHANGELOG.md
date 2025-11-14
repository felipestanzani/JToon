# Changelog

All notable changes to this project will be documented in this file.

This project adheres to Semantic Versioning and follows a Keep a Changelog-like format.

## [0.1.3] - 2025-11-14

### Added

- Decoding support via `JToon.decode()` and `JToon.decodeToJson()` methods.
- `DecodeOptions` record with `strict` validation mode.
- `decoder` package with full TOON parser supporting all formats (primitives, objects, arrays, delimiters).
- String unescaping in `StringEscaper.unescape()` method.
- Comprehensive test suite with round-trip encode/decode verification.

### Changed

- Updated README with decode API documentation and examples.

## [0.1.2] - 2025-11-05

### Changed

- Java version requirement from 21 to 17 for broader compatibility.
- Refactored `JsonNormalizer` to use if-else statements instead of switch expressions for better readability.
- Updated dependency: `com.fasterxml.jackson.core:jackson-databind` from 2.18.2 to 2.20.1.
- Updated dependency: `org.junit:junit-bom` from 5.10.0 to 6.0.1.
- Updated GitHub Actions: `actions/setup-java` from 4 to 5, `actions/upload-artifact` from 4 to 5, `actions/checkout` from 4 to 5, `softprops/action-gh-release` from 1 to 2.

## [0.1.1] - 2025-10-30

### Added

- `JToon.encodeJson(String)` and `JToon.encodeJson(String, EncodeOptions)` to encode plain JSON strings directly to TOON.
- Centralized JSON parsing via `JsonNormalizer.parse(String)` to preserve separation of concerns.
- Unit tests for JSON string entry point (objects, primitive arrays, tabular arrays, custom options, error cases).
- README examples for JSON-string encoding, including a Java text block example.
- This changelog.

### Changed

- README: Expanded API docs to include `encodeJson` overloads.

## [0.1.0] - 2025-10-30

### Added

- Initial release.
- Core encoding of Java objects to TOON with automatic normalization of Java types (numbers, temporals, collections, maps, arrays, POJOs).
- Tabular array encoding for uniform arrays of objects.
- Delimiter options (comma, tab, pipe) and optional length marker.
- Comprehensive README with specification overview and examples.

[0.1.2]: https://github.com/felipestanzani/jtoon/releases/tag/v0.1.2
[0.1.1]: https://github.com/felipestanzani/jtoon/releases/tag/v0.1.1
[0.1.0]: https://github.com/felipestanzani/jtoon/releases/tag/v0.1.0
