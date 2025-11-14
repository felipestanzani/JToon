# Release Process

This document describes how to create a new release of JToon.

## Automated Release via GitHub Actions

The project uses GitHub Actions to automatically build and release when a version tag is pushed.

### Steps to Create a Release

1. **Update the version in `build.gradle`**

   ```gradle
   version = '1.0'  // Update to your new version
   ```

2. **Commit the version change**

   ```bash
   git add build.gradle
   git commit -m "Bump version to 1.0"
   git push
   ```

3. **Create and push a version tag**

   ```bash
   git tag v1.0
   git push origin v1.0
   ```

4. **Wait for the workflow to complete**
    - The GitHub Actions workflow will automatically:
        - Build the project
        - Run all tests
        - Create a GitHub release
        - Attach the JAR file to the release
        - Generate release notes from commits

### Release Artifacts

The release will include:

- `JToon-{version}.jar` - Main library JAR
- Automatically generated release notes

### Pre-release Versions

If your version contains a dash (e.g., `1.0-SNAPSHOT`, `2.0-beta`), 
the release will be marked as a pre-release automatically.

### Version Tag Format

Use semantic versioning with a `v` prefix:

- `v1.0` - Major release
- `v1.0.0` - Patch release
- `v1.0-SNAPSHOT` - Snapshot/pre-release
- `v2.0-beta` - Beta release

## Continuous Integration

Every push to `main`, `master`, or `develop` branches, and every pull request, 
triggers the build workflow which:

- Compiles the code
- Runs all tests
- Generates test reports
- Creates build artifacts

Check the build status badges in the README for the current build state.
