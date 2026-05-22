---
name: Service Migration Rollout Plan
status: Planned
progress:
  - "[ ] Baseline existing service UUIDv7 tests"
  - "[ ] Add library dependency to first service"
  - "[ ] Convert first local UUIDv7 copy to wrapper"
  - "[ ] Migrate first service imports"
  - "[ ] Repeat for remaining services"
  - "[ ] Remove duplicated wrappers"
---

# Service Migration Rollout Plan

## Goal

Move existing services from duplicated UUIDv7 source code to the shared library with zero disruption.

## Known Current Services

- `chitragupta`
- `narada`

Both currently keep a local UUIDv7 utility and use it for entity IDs.

## Stage 1: Baseline

- Run existing UUIDv7 tests in each service.
- Confirm no behavior differences between local implementations.
- Do not change production code.

## Stage 2: Add Dependency

- Add `com.subhrodip:uuidv7-kt` to one service.
- Keep local UUIDv7 implementation active.
- Add a small integration test that imports the library directly.

## Stage 3: Delegate Wrapper

- Replace the local implementation with a deprecated wrapper.
- Preserve the old local method name `randomUUID`.
- Delegate to `com.subhrodip.uuidv7.UUIDv7.generate`.

## Stage 4: Direct Imports

- Update application imports gradually.
- Start with base entity ID generation.
- Keep tests green after each service.

## Stage 5: Remove Duplicates

- Remove wrappers only after all services consume a stable non-snapshot release.
- Require at least one successful production deployment cycle before removal.

## Rollback

- Before wrapper: remove dependency.
- After wrapper: restore local implementation.
- After direct imports: revert imports or pin known-good library version.
- After removal: restore wrapper from git history if needed.
