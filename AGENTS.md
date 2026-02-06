# AGENTS.md

## Project Goal

- Build an Android Pomodoro app with Clean Architecture, MVI, and Jetpack Compose.
- Ensure reliability with the screen locked and notify users on completion.
- Prioritize background reliability, battery efficiency, and modern Android compliance.

## Architecture Principles

- Strict Clean Architecture.
- Domain is pure Kotlin with no Android or third-party SDK dependencies.
- Small, explicit, highly decoupled modules.
- api/impl separation for visibility control.
- MVI for presentation.
- Compose-first UI.
- Test-first mindset.

## Module Structure

```
app/
core/
  appconfig/
  ui/
  design-system/
  resources/
  navigation/
features/
  <feature>/
    api/
    impl/
libs/
  datastore/
  notifications/
  analytics/
  crashlytics/
  logger/
  permissions/
build-logic/
```

## Feature Modules

- Feature names use kebab-case.
- Each feature has `api` for contracts and `impl` for implementations.
- `api` contains interfaces, shared models, and navigation contracts only.
- `impl` contains ViewModels, use cases, data sources, and repositories.

## Clean Architecture by Packages

- Use `data`, `domain`, and `presentation` packages in both `api` and `impl`.
- Domain contains entities, value objects, use cases, and repository interfaces.
- Data contains repository implementations, data sources, and mappers.
- Presentation uses MVI with UiState, UiAction, UiEffect, and ViewModels.

## UI and Compose

- Split each screen into Route and Screen.
- Screen is the only composable used for previews and Roborazzi tests.
- Use design-system components only; do not use Material3 directly in features.

## Navigation

- Use androidx Navigation3.
- Navigation contracts live in `core/navigation`.
- Navigation wiring lives in `app`.
- Features emit navigation intents only.

## Centralized Scaffold

- A single Scaffold exists in `app`.
- Navigation renders inside `Scaffold.content`.
- Scaffold state lives in `core/ui` via `ScaffoldConfig`.

## Design System

- Located in `core/design-system`.
- Provides theme, colors, typography, spacing, and components.
- Do not use Material3 directly in features.

## Dependency Injection

- Use Koin.
- Define modules per feature in `impl`.
- Keep `api` exposure minimal.

## Testing Strategy

- Unit tests are mandatory where possible.
- Use JUnit5, MockK, Kluent, and Turbine.
- Use GIVEN/WHEN/THEN naming.
- Use `verifyOnce()` and `verifyNever()`.
- Use `relaxedMockk` where appropriate.
- Domain tests must have no Android dependencies.
- Snapshot tests use Roborazzi with Robolectric and Screen composables only.

## Background Execution

- Implement background logic in `libs/notifications` with api/impl separation.
- Prefer AlarmManager plus BroadcastReceiver.
- Use Foreground Service only if required.
- Use WorkManager only if justified.

## Persistence

- Use DataStore for app configuration.
- Use Room only when persistence is required.
- Access storage via repositories only.

## Analytics and Crash Reporting

- Use Firebase Analytics and Crashlytics only through `libs/analytics` and `libs/crashlytics`.
- `api` exposes provider-agnostic interfaces.
- `impl` contains Firebase integration.
- Features, presentation, and domain must not depend on Firebase SDKs.
- Emit analytics from presentation and respect consent and privacy.

## Gradle and Build Configuration

- Use version catalogs.
- Use build-logic convention plugins.
- Kotlin DSL only.

## Code Generation

- Use KSP only.
- No kapt.

## Logging

- Use Timber.
- No logging in domain.

## CI and Quality Gates

- GitHub Actions must run build, unit tests, and lint.
- No merge is allowed if checks fail.

## Static Analysis

- detekt and ktlint with shared configuration.
- Strict enforcement.

## Forbidden Anti-Patterns

- Android dependencies in domain.
- ViewModels in `api`.
- Direct Material3 usage outside design-system.
- Manual singletons.
- Direct navigation calls from UI.

## Language Policy

- All source code, documentation, configuration files, tests, build scripts, and repo artifacts must be written in English.

## Objective

- Deliver a scalable, highly testable, maintainable, and decoupled codebase ready for long-term evolution.
