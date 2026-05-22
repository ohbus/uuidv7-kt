---
name: Security And Governance Tracker
status: Planned
progress:
  - "[x] Add license"
  - "[x] Add security policy"
  - "[x] Configure least-privilege workflows"
  - "[ ] Configure branch protection"
  - "[ ] Configure tag protection"
---

# Security And Governance Tracker

## Goal

Keep the project safe enough for production use without adding process overhead before it is useful.

## License

- [x] Use MIT license.
- [x] Add `LICENSE`.
- [x] Add license metadata to publication.

## Security

- [x] Add `SECURITY.md`.
- [ ] Do not log tokens in workflows.
- [ ] Use `GITHUB_TOKEN` for GitHub Packages publishing.
- [ ] Avoid personal access tokens unless absolutely required.
- [ ] Keep workflow permissions minimal.

## Repository Governance

- [ ] Protect `main`.
- [ ] Require CI before merge.
- [ ] Restrict release tag creation.
- [ ] Decide whether signed commits are required.
- [ ] Add Dependabot later for Gradle and GitHub Actions.

## OSS Hygiene

- [ ] Add README before first public release.
- [ ] Add CHANGELOG before first stable release.
- [ ] Add issue templates later if external users appear.
- [ ] Add code of conduct later if community contribution becomes expected.
