# AGENTS.md

## 🎯 Project Goal

Android **Pomodoro** application built following **Clean Architecture**, **MVI**, and **Jetpack Compose**, with high test coverage, strong modularization, and long-term scalability in mind.

The app must **work reliably with the screen locked**, notifying the user when a Pomodoro finishes using:
- System alarms (`AlarmManager`) and/or
- Scheduled notifications (foreground services if strictly required)

The final technical approach must prioritize:
- Reliability in background execution
- Battery efficiency
- Compliance with modern Android restrictions

---

## 🧱 Architectural Principles

- Strict **Clean Architecture**
- **Domain is always pure Kotlin** (no Android dependencies)
- Small, explicit, highly decoupled modules
- `api / impl` separation for visibility control
- **MVI** for presentation
- **Compose-first UI**
- **Test-first mindset**

---

## 🗂️ Module Structure

```
app/
core/
  ui/
  design-system/
  resources/
  navigation/
features/
  pomodoro/
    api/
    impl/
libs/
  datastore/
    api/
    impl/
  notifications/
    api/
    impl/
  analytics/
    api/
    impl/
  crashlytics/
    api/
    impl/
build-logic/
```

---

## 📦 Feature Modules

### Naming

- Feature modules use **kebab-case**:
  - `pomodoro`
  - `settings`
  - `statistics`

### Structure

Each feature is split into:

```
feature-name/
  api/
  impl/
```

#### `api`
- Public contracts
- Interfaces
- Shared models
- Navigation contracts
- **No implementation logic**

#### `impl`
- Concrete implementations
- ViewModels
- UseCases
- Repository implementations

---

## 🧩 Clean Architecture by Packages

Inside both `api` and `impl`:

```
data/
domain/
presentation/
```

### Domain
- Pure Kotlin
- No Android
- No third-party SDKs
- Entities, value objects
- UseCases
- Repository interfaces

### Data
- Repository implementations
- DataSources
- Mappers
- Room / DataStore if required

### Presentation
- MVI
- ViewModels
- UiState / UiAction / UiEffect
- Compose UI

---

## 🎨 UI & Jetpack Compose

### Mandatory Screen Separation

Each screen must be split into **Route** and **Screen**:

```kotlin
@Composable
fun PomodoroRoute(
  viewModel: PomodoroViewModel = koinViewModel()
) {
  val state by viewModel.state.collectAsState()

  PomodoroScreen(
    state = state,
    onAction = viewModel::onAction
  )
}
```

```kotlin
@Composable
fun PomodoroScreen(
  state: PomodoroState,
  onAction: (PomodoroAction) -> Unit
) {
  // Pure UI
}
```

Only `PomodoroScreen` is allowed to be used for:
- Previews
- Paparazzi snapshot tests

---

## 🧭 Navigation

- **androidx Navigation3**
- Navigation contracts live in `core/navigation`
- Navigation wiring lives in `app`

Features:
- Do not know the NavHost
- Emit navigation intents only

---

## 🧱 Centralized Scaffold

- A **single Scaffold** exists in the `app` module
- Navigation is rendered inside `Scaffold.content`

### Dynamic TopBar / BottomBar Control

A global scaffold state is defined in `core/ui`:

```kotlin
data class ScaffoldConfig(
  val topBar: TopBarState?,
  val bottomBar: BottomBarState?
)
```

Screens may update this state via events, without:
- Knowing the Scaffold implementation
- Accessing Material directly

---

## 🎨 Design System

Located in:

```
core/design-system
```

Includes:
- Theme
- Colors
- Typography
- Spacing
- Custom UI components

### Golden Rule

❌ Do not use Material3 directly in features  
✅ Always use design-system components

---

## 🔌 Dependency Injection

- **Koin**
- Modules declared per feature
- Definitions live in `impl`
- Minimal exposure via `api`

---

## 🧪 Testing Strategy

### Unit Tests

Mandatory wherever possible.

Main libraries:
- JUnit5
- MockK
- Kluent
- Turbine

Rules:
- GIVEN / WHEN / THEN naming
- `verifyOnce()`, `verifyNever()`
- `relaxedMockk<>`
- Domain tests without Android

---

### UI Snapshot Tests

- **Paparazzi**
- Design-system components
- Full screens when simple enough
- No real navigation

---

## ⏱️ Pomodoro & Background Execution

Possible approaches:
- `AlarmManager` + `BroadcastReceiver`
- Foreground Service
- WorkManager (only if justified)

All implementations live under:
```
libs/notifications
```

Always following `api / impl` separation.

---

## 🗃️ Persistence

- **DataStore** → app configuration
- **Room** → only if persistence is required
- Access via repositories only

---

## 📊 Analytics & Crash Reporting

The app uses **Firebase Analytics** and **Firebase Crashlytics** in a **fully abstracted** manner. No feature, presentation layer, or domain layer may depend directly on Firebase SDKs.

Dedicated modules under `libs` (e.g. `libs/analytics`, `libs/crashlytics`) must follow strict `api / impl` separation:
- `api` exposes provider-agnostic interfaces (e.g. `AnalyticsTracker`, `AnalyticsEvent`, `CrashReporter`)
- `impl` contains the concrete Firebase integration

This approach enables:
- Provider replacement with no architectural impact
- No-op or disabled implementations per build type
- Easy testing using fakes
- Guaranteed purity of the domain layer

Event reporting is primarily done from the **presentation layer**, avoiding business logic coupled to analytics, and must respect consent and privacy requirements.

---

## ⚙️ Gradle & Build Configuration

- Version Catalogs
- `build-logic` module
- Convention Plugins
- **Kotlin DSL only**

---

## 🤖 Code Generation

- **KSP only**
- No kapt

---

## 📦 Logging

- **Timber**
- No logging in domain

---

## 🚀 CI / CD

- **GitHub Actions**
- Mandatory checks:
  - Build
  - Unit tests
  - Lint (detekt + ktlint)

No merge is allowed if checks fail.

---

## 🧹 Static Analysis

- detekt
- ktlint
- Shared configuration
- Strict enforcement

---

## ❌ Forbidden Anti‑Patterns

- Android dependencies in domain
- ViewModels in `api`
- Direct Material3 usage outside design-system
- Manual singletons
- Direct navigation calls from UI

---

## ✅ Final Objective

A codebase that is:
- Scalable
- Highly testable
- Maintainable
- Decoupled
- Ready for long-term evolution

---

**This document is binding for the project.**


## 🌍 Language Policy

All **source code**, **documentation**, **configuration files**, **tests**, **build scripts**, and **repository artifacts** must be written **exclusively in English**.

Even if discussions, reviews, or AI-assisted interactions happen in Spanish, the resulting outputs (including:
- Kotlin / Java code
- Compose UI
- Gradle scripts
- Markdown documentation
- Comments, naming, and identifiers
- CI / configuration files

) **must always be produced in English**.

This rule ensures:
- Consistency across the codebase
- Easier onboarding for international contributors
- Better long-term maintainability
- Compatibility with tooling, linting, and external documentation standards
