---
name: Manual Startup Tracker
status: Planned
progress:
  - "[ ] Create GitHub repository"
  - "[ ] Decide repository visibility"
  - "[ ] Configure branch protection"
  - "[ ] Confirm package publishing permissions"
  - "[ ] Confirm Maven namespace strategy"
---

# Manual Startup Tracker

## Goal

Track tasks the user must do manually or explicitly approve before the project can be fully published and used.

## Required Before First Remote Publish

- [ ] Create GitHub repository `ohbus/uuidv7-kt`.
- [ ] Decide whether the repository starts private or public.
- [ ] Add remote: `git remote add origin git@github.com:ohbus/uuidv7-kt.git`.
- [ ] Push initial branch: `git push -u origin main`.
- [ ] Enable GitHub Packages for the repository if required by account settings.
- [ ] Confirm Actions can publish packages with `GITHUB_TOKEN`.

## Required Before First Stable Release

- [ ] Protect `main`.
- [ ] Require CI before merge.
- [ ] Restrict tag creation for `v*` tags.
- [ ] Decide whether releases require manual review before tagging.
- [ ] Confirm package visibility after first publish.
- [ ] Confirm at least one consumer can authenticate to GitHub Packages.

## Required Before Maven Central

- [ ] Verify whether `com.subhrodip` is a valid owned namespace.
- [ ] If not, switch future Central coordinates to `io.github.subhrodip`.
- [ ] Create Sonatype Central Portal account.
- [ ] Generate a GPG signing key.
- [ ] Store signing credentials as GitHub secrets only when Central publishing is added.

Signing is explicitly deferred. Do not add Gradle signing configuration until Maven Central publishing is being implemented.

## Optional Later

- [ ] Add Dependabot.
- [ ] Add issue templates.
- [ ] Add code of conduct if the project becomes community-facing.
