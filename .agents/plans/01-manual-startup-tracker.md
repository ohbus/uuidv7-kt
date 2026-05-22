---
name: Manual Startup Tracker
status: Planned
progress:
  - "[x] Create GitHub repository"
  - "[ ] Decide repository visibility"
  - "[ ] Configure branch protection"
  - "[x] Confirm package publishing permissions"
  - "[x] Confirm Maven namespace strategy"
---

# Manual Startup Tracker

## Goal

Track tasks the user must do manually or explicitly approve before the project can be fully published and used.

## Required Before First Remote Publish

- [x] Create GitHub repository `ohbus/uuidv7-kt`.
- [ ] Decide whether the repository starts private or public.
- [x] Add remote: `git remote add origin git@github.com:ohbus/uuidv7-kt.git`.
- [x] Push initial branch: `git push -u origin main`.
- [ ] Enable GitHub Packages for the repository if required by account settings.
- [x] Confirm Actions can publish packages with `GITHUB_TOKEN`.

## Required Before First Stable Release

- [ ] Protect `main`.
- [ ] Require CI before merge.
- [ ] Restrict tag creation for `v*` tags.
- [ ] Decide whether releases require manual review before tagging.
- [ ] Confirm package visibility after first publish.
- [ ] Confirm at least one consumer can authenticate to GitHub Packages.

## Required Before Maven Central

- [x] Verify whether `com.subhrodip` is a valid owned namespace.
- [x] Create Sonatype Central Portal account.
- [x] Generate a GPG signing key.
- [x] Store signing credentials as GitHub secrets.
- [x] Create GitHub Environment `maven-central` for deployment approval.
- [ ] Validate first Maven Central publish from tag `v0.0.1`.

Signing is configured in-memory and activated only when signing secrets are supplied.

## Optional Later

- [ ] Add Dependabot.
- [ ] Add issue templates.
- [ ] Add code of conduct if the project becomes community-facing.
