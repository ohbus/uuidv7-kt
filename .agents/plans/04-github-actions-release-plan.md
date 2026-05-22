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

- Runs manually with required `release_name` input matching `vX.Y.Z`.
- Has `publish_github_packages` input for retrying partial releases when GitHub Packages already contains the immutable version.
- Derives Gradle project version from the release input name.
- Fails if release input name does not match the derived Gradle project version.
- Fails if project version is a snapshot.
- Allows the first release only as `v0.0.1`.
- Allows later releases only when the release input name is exactly one major, minor, or patch step after the previous release tag.
- Publishes to GitHub Packages through `publishAllPublicationsToGitHubPackagesRepository` only after approval from the `github-packages` GitHub Environment.
- Allows skipping GitHub Packages only for retrying a release after the same version was already published there.
- Publishes to Maven Central only after approval from the `maven-central` GitHub Environment.
- Creates the matching Git tag and GitHub Release after package publishing succeeds and the `github-packages` environment is approved.
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
- Do not create the release tag or GitHub Release until GitHub Packages and Maven Central publication have both completed.

## Verification Status

- [x] Local jar, sources jar, javadocs jar, and generated POM were produced.
- [ ] GitHub Release asset upload still needs validation on the first manual release run.
- [ ] Maven Central environment approval still needs validation on the first manual release run.
