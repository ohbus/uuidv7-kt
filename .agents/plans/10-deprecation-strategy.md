---
name: Deprecation Strategy
status: Planned
progress:
  - "[ ] Define local wrapper shape"
  - "[ ] Apply wrapper in first service"
  - "[ ] Migrate direct imports"
  - "[ ] Remove wrapper after stable adoption"
---

# Deprecation Strategy

## Goal

Deprecate duplicated service UUIDv7 classes safely after the shared library is proven.

## Policy

- Do not remove duplicated service classes immediately.
- Do not change all call sites at once.
- Keep local API compatibility while migrating.
- Mark local wrappers deprecated only after the library package is resolvable in that service.

## Wrapper Intent

Each existing service-local UUIDv7 class should temporarily become a deprecated wrapper that delegates to:

```text
com.subhrodip.uuidv7.UUIDv7.generate()
```

The wrapper should keep the old local method:

```text
randomUUID()
```

This preserves existing service call sites while moving the actual generator to the shared package.

## Removal Criteria

- [ ] Library has a stable non-snapshot release.
- [ ] All services consume the stable release.
- [ ] All direct imports have been migrated.
- [ ] At least one production deployment cycle completed without UUID-related issues.
- [ ] Rollback path documented.

## Rollback Criteria

Restore local implementation if:

- Dependency resolution blocks deployment.
- New library behavior differs from expected UUIDv7 behavior.
- Runtime compatibility issue appears in a service.
- A release artifact is found to be corrupt or incomplete.
