---
name: GitHub Actions Release Plan
status: Planned
progress:
  - "[x] Add CI workflow"
  - "[x] Add snapshot publish workflow"
  - "[x] Add release workflow"
  - "[ ] Validate package publishing permissions"
  - "[x] Validate release asset generation"
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
- Fails if tag does not match Gradle project version.
- Fails if project version is a snapshot.
- Publishes to GitHub Packages.
- Creates a GitHub Release.
- Attaches jar, sources jar, javadocs jar, generated POM, and SHA-256 checksums.

## Permissions

- CI: `contents: read`.
- Publish: `contents: read`, `packages: write`.
- Release: `contents: write`, `packages: write`.

## Release Policy

- Do not publish stable versions from ordinary `main` merges.
- Do not overwrite published versions.
- If a release is bad, publish a patch release.

## Verification Status

- [x] Local jar, sources jar, javadocs jar, and generated POM were produced.
- [ ] GitHub Release asset upload still needs validation after the remote repository exists.
