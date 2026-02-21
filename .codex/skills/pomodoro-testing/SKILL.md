---
name: pomodoro-testing
description: Testing standards for the Pomodoro Android app. Use when writing or reviewing unit tests, ViewModel tests, and Roborazzi snapshot tests that must follow the project's libraries and conventions.
---

# Pomodoro Testing

## Overview

Write tests that match the project's tooling, structure, and naming conventions across domain, presentation, and UI snapshots.

## Tooling

- Unit tests: JUnit5, MockK, Kluent, Turbine.
- Snapshot tests: Roborazzi with Robolectric.

## Core rules

- Use GIVEN/WHEN/THEN naming.
- Prefer fakes over mocks; use mocks only when a fake is not practical.
- Prefer `verifyOnce()` and `verifyNever()` with MockK.
- Use `relaxedMockk` where appropriate.
- Keep domain tests free of Android dependencies.

## ViewModel tests

- Use `runTest` with `turbineScope` and `testIn(this)` to collect `StateFlow` and effects.
- Assert state transitions and effects deterministically.
- Test error paths and loading states.

## Use case tests

- Mock repositories and collaborators.
- Verify side effects such as notification scheduling.
- Return `Result<T>` and verify success and failure branches.

## Snapshot tests (Roborazzi)

- Use pure Screen composables, never Routes.
- Use Robolectric and JVM-based tests only.
- Prefer parameterized tests for multiple states.
- Use `@EnumSource` or `@MethodSource` for variants.
- Capture with `captureRoboImage("<name>.png")`.

## Common placements

- Use case tests: `features/<feature>/impl/src/test/kotlin/.../domain/usecase`.
- ViewModel tests: `features/<feature>/impl/src/test/kotlin/.../presentation/<screen>`.
- Snapshot tests: `features/<feature>/impl/src/test/kotlin/.../presentation/<screen>`.
- Design-system snapshots: `core/design-system/src/test/kotlin/...`.

## Checklist

- State and effect flows are tested.
- Error states are covered.
- Snapshot tests cover representative UI states.
- Tests are deterministic and do not hit real navigation.
