---
name: pomodoro-architecture-guardrails
description: Architecture guardrails and module-boundary checks for the Pomodoro Android app. Use when making or reviewing any change in this repo that affects module structure, dependencies, UI composition, DI, navigation, or testing compliance.
---

# Pomodoro Architecture Guardrails

## Overview

Enforce the non-negotiable architecture rules for this repo and keep changes aligned with Clean Architecture, MVI, and the design system.

## Source of truth

- Read `AGENTS.md`.
- Cross-check `settings.gradle.kts` for the current module list and naming.
- Read `.ai/WORKFLOW_FEATURE.md` when working on a feature.

## Non-negotiables

- Keep domain pure Kotlin with no Android or third-party SDK dependencies.
- Enforce `api/impl` separation with no implementation logic in `api`.
- Use MVI in presentation and keep ViewModels out of `api`.
- Use design-system components only; avoid direct Material3 in features.
- Keep navigation contracts in `core/navigation`; emit navigation intents only.
- Use a single scaffold in `app`; update scaffold state via events.
- Use Koin with module definitions in `impl` only.
- Use KSP only; avoid kapt.
- Use Timber outside domain only.
- Write all code, docs, and configs in English.

## Boundary placement

- Use `core/*` for shared UI, design system, navigation, resources, and app config.
- Use `libs/*` for infrastructure (datastore, notifications, analytics, crashlytics, logger, permissions).
- Use `features/*` for feature modules with `api` contracts and `impl` implementations.

## Quick compliance check

- Verify domain packages have no Android imports.
- Put UI strings in resources, not hardcoded in code.
- Wire new dependencies via version catalogs and build-logic.
- Cover logic with unit tests using JUnit5, MockK, Kluent, and Turbine.
