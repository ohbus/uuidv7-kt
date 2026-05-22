---
name: GitHub Actions Release Plan
status: Planned
progress:
  - "[x] Add CI workflow"
  - "[x] Add snapshot publish workflow"
  - "[x] Add release workflow"
  - "[x] Validate package publishing permissions"
  - "[x] Validate release asset generation"
  - "[ ] Validate Maven Central environment approval"
---

# GitHub Actions Release Plan

## Goal

Provide minimal CI/CD through GitHub Actions without release bots or unnecessary automation.

## Workflows

`ci.yml`:

- Runs on pull requests and pushes to `main`.
- Executes Gradle on JDK 17.
- Tests runtime compatibility on Java 8, 11, 17, and 21.
- Installs the matrix JDK before JDK 17 so Gradle runs on 17 while toolchain discovery can still find the test JDK.
- Publishes nothing.

`publish.yml`:

- Runs on pushes to `main`.
- Publishes snapshots only.
- Fails if project version does not end with `-SNAPSHOT`.

`release.yml`:

- Runs on tags matching `vX.Y.Z`.
- Derives Gradle project version from the tag.
- Fails if tag does not match the derived Gradle project version.
- Fails if project version is a snapshot.
- Allows the first release only as `v0.0.1`.
- Allows later releases only when the tag is exactly one major, minor, or patch step after the previous release tag.
- Publishes to GitHub Packages through `publishAllPublicationsToGitHubPackagesRepository`.
- Publishes to Maven Central only after approval from the `maven-central` GitHub Environment.
- Creates a GitHub Release.
- Attaches jar, sources jar, javadocs jar, generated POM, and SHA-256 checksums.

## Permissions

- CI: `contents: read`.
- Publish: `contents: read`, `packages: write`.
- Release: `contents: write`, `packages: write`.
- Maven Central credentials are scoped to the `publish-maven-central` job.

## Release Policy

- Do not publish stable versions from ordinary `main` merges.
- Do not overwrite published versions.
- If a release is bad, publish a patch release.
- Do not create the GitHub Release until GitHub Packages and Maven Central publication have both completed.

## Verification Status

- [x] Local jar, sources jar, javadocs jar, and generated POM were produced.
- [ ] GitHub Release asset upload still needs validation on the first tag.
- [ ] Maven Central environment approval still needs validation on the first tag.
