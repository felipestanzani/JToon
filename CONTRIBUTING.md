# Contributing to JToon

Thank you for your interest in contributing to the official Java implementation of TOON!

## Project Setup

This project uses [Gradle](https://gradle.org/) for dependency management and build automation.

```bash
# Clone the repository
git clone https://github.com/toon-format/JToon.git
cd JToon

# Build the project
./gradlew build

# Run tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport
```

## Development Workflow

1. **Fork the repository** and create a feature branch
2. **Make your changes** following the coding standards below
3. **Add tests** for any new functionality
4. **Ensure all tests pass** and coverage remains high
5. **Submit a pull request** with a clear description

### New features

1. **Open a discussion topic** about the new features explaining the advantages or the motivation for this new feature.
2. **After approval** create an Issue linked to the discussion topic.
3. **Follow the development workflow** to implement the new feature.

### Bugs

1. **Open an issue** reporting the bug with detailed reproduction steps.

## Coding Standards

### Java Version Support

We support Java 17 and above.

### Code Style

- Follow Java naming conventions (camelCase for methods/variables, PascalCase for classes)
- Keep methods focused and concise
- Use meaningful variable and method names
- Add Javadoc comments for public APIs
- Format code consistently (use IDE auto-formatting)

### Testing

- All new features must include tests
- Maintain test coverage at **75%+ (enforced in CI)** for files and **85%+ for overall project**
- Tests should cover edge cases and spec compliance
- Use JUnit 5 for unit tests
- Run the full test suite:

```bash
./gradlew test
```

- Run with coverage report:

```bash
./gradlew test jacocoTestReport
```

## SPEC Compliance

All implementations must comply with the [TOON specification](https://github.com/toon-format/spec/blob/main/SPEC.md).

Before submitting changes that affect encoding/decoding behavior:

1. Verify against the official SPEC.md
2. Add tests for the specific spec sections you're implementing
3. Document any spec version requirements

## Pull Request Guidelines

- **Title:** Use a clear, descriptive title (e.g., "Add support for nested arrays", "Fix: Handle edge case in decoder")
- **Description:** Explain what changes you made and why
- **Tests:** Include tests for your changes
- **Documentation:** Update README or Javadoc if needed
- **Commits:** Use clear commit messages ([Conventional Commits](https://www.conventionalcommits.org/) preferred)

## Communication

- **GitHub Issues:** For bug reports and feature requests
- **GitHub Discussions:** For questions and general discussion
- **Pull Requests:** For code reviews and implementation discussion
- **[Discord channel](https://discord.gg/ywXDMFdx):** For direct interaction with project members

## Co-Maintainers

This is a collaborative project. Current maintainers:

- [@felipestanzani](https://github.com/felipestanzani)
- [@jenspapenhagen](https://github.com/jenspapenhagen)

All maintainers have equal and consensual decision-making power. For major architectural decisions, please open a discussion issue first.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
