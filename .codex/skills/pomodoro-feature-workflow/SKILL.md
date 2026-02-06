---
name: pomodoro-feature-workflow
description: Feature development workflow for the Pomodoro Android app using Clean Architecture, MVI, Compose, and api/impl modules. Use when creating a new feature module, adding screens, or implementing a new flow with DI, navigation, persistence, or notifications.
---

# Pomodoro Feature Workflow

## Overview

Implement features consistently with the project's architecture, module boundaries, and testing standards.

## Inputs to analyze

- Screenshots or UI designs: analyze colors, spacing, typography, and components.
- UI states: normal, loading, error, empty, disabled.
- Text content: extract labels and place them in resources.
- Interactions: buttons, fields, toggles, sliders, and navigation.
- API endpoints: URL, method, headers, request body, responses, and errors.
- Persistence: DataStore vs Room, entities, relationships, and queries.
- Background execution: AlarmManager, BroadcastReceiver, Foreground Service if required.
- Task goals: validations, edge cases, and post-action navigation.

## Step 1: Analysis and planning

- Identify modules to create or modify.
- List files to add or change.
- Determine dependencies and Koin wiring.
- Define navigation contract changes.

Output: a concrete list of modules and files.

## Step 2: Module structure

Use kebab-case feature names and `src/main/kotlin`.

```
features/
  <feature>/
    api/
      build.gradle.kts
      src/main/kotlin/com/jujodevs/pomodoro/features/<feature>/
        domain/
          model/
          repository/
        navigation/
    impl/
      build.gradle.kts
      src/main/kotlin/com/jujodevs/pomodoro/features/<feature>/
        data/
          datasource/
          repository/
          mapper/
          model/
        domain/
          repository/
          usecase/
        presentation/
          <screen>/
            <Screen>Route.kt
            <Screen>Screen.kt
            <Screen>ViewModel.kt
            <Screen>State.kt
            <Screen>Action.kt
            <Screen>Effect.kt
            components/
        di/
          <Feature>Module.kt
      src/test/kotlin/...
```

## Step 3: Domain layer

- Define domain models in `api`.
- Place repository interfaces in `api` when they are part of the public contract.
- Implement use cases in `impl/domain/usecase`.
- Keep domain pure Kotlin with no Android or SDK dependencies.

Use case rules:
- Single responsibility.
- `operator fun invoke()`.
- Return `Result<T>` for fallible operations.
- Return `Flow<T>` for observable data.

## Step 4: Data layer

- Define DTOs and Room entities in `impl/data/model` as needed.
- Write mappers between data and domain.
- Implement local or remote data sources.
- Implement repositories in `impl/data/repository`.

## Step 5: Presentation layer (MVI)

- State is an immutable data class.
- Action is a sealed interface.
- Effect is a sealed interface for one-time events.
- ViewModel exposes `StateFlow` for state and `Flow` for effects.
- Route connects ViewModel and Screen.
- Screen is pure UI and uses design-system components only.

Route and Screen template:

```kotlin
@Composable
fun FeatureRoute(
    viewModel: FeatureViewModel = koinViewModel(),
    onNavigate: (NavigationDestination) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FeatureEffect.NavigateTo -> onNavigate(effect.destination)
                is FeatureEffect.ShowSnackbar -> { /* handle */ }
            }
        }
    }

    FeatureScreen(
        state = state,
        onAction = viewModel::onAction
    )
}
```

```kotlin
@Composable
fun FeatureScreen(
    state: FeatureState,
    onAction: (FeatureAction) -> Unit
) {
    // Pure UI using design-system components only
}
```

## Step 6: Navigation contracts

- Define navigation contracts in `core/navigation`.
- If a feature needs its own contract, expose it in its `api` module and wire it in `app`.
- Only `app` wires Navigation3 destinations.

## Step 7: Dependency injection

- Define Koin modules in `impl/di`.
- Register modules in the `app` Koin setup.

## Step 8: Validations

- UI layer: immediate formatting feedback.
- ViewModel: business rule validation.
- Domain use cases: critical rules.

## Step 9: Error handling

- Map errors to UI-friendly messages.
- Handle network, API, validation, database, and background execution failures.
- Allow dismissing errors in the UI.

## Step 10: Testing

- Use JUnit5, MockK, Kluent, and Turbine.
- Write ViewModel tests and use case tests.
- Use Roborazzi for snapshot tests with Robolectric.
- Prefer parameterized snapshot tests.

Use `pomodoro-testing` for detailed patterns.

## Naming conventions

- Feature module: `kebab-case`.
- Screen: `PascalCaseScreen`.
- Route: `PascalCaseRoute`.
- ViewModel: `PascalCaseViewModel`.
- State: `PascalCaseState`.
- Action: `PascalCaseAction`.
- Effect: `PascalCaseEffect`.
- Repository: `PascalCaseRepository`.
- Repository impl: `PascalCaseRepositoryImpl`.
- Use case: `VerbNounUseCase`.
- Room entity: `PascalCaseEntity`.
- DTO: `PascalCaseDto`.
- Mapper: `PascalCaseMapper`.
- DI module: `PascalCaseModule`.
- Test: `ClassNameTest`.

## Completeness checklist

Architecture:
- `api` has only contracts and shared models.
- `impl` has all implementations.
- Domain has no Android or SDK dependencies.

MVI:
- State is immutable.
- Action and Effect are sealed interfaces.
- ViewModel uses `StateFlow` and `Flow`.

UI:
- Route and Screen separation is enforced.
- Screen uses design-system components only.
- Previews exist for Screen.
- Strings are in resources.

Navigation:
- Contracts in `core/navigation` or feature `api`.
- Wiring only in `app`.
- Features emit intents only.

Dependency Injection:
- Koin module in `impl`.
- Module registered in `app`.

Error handling:
- Known errors are mapped.
- Error state is visible and dismissible.

Testing:
- Use case tests and ViewModel tests exist.
- Snapshot tests exist when UI is non-trivial.
- GIVEN/WHEN/THEN naming is used.

Background execution:
- Implementation lives in `libs/notifications`.
- Works with screen locked and is battery efficient.

## Anti-patterns to avoid

- Android dependencies in domain.
- ViewModels in `api`.
- Direct Material3 usage in features.
- Manual singletons.
- Direct navigation calls from UI.
- LiveData.
- Two-way binding.
- kapt.
- Hardcoded strings.
- Logging in domain.
- Mixing Route and Screen responsibilities.

## Priority order

1. Domain contracts.
2. Domain logic.
3. Data layer.
4. MVI contracts.
5. ViewModel.
6. UI.
7. DI module.
8. Error handling.
9. Testing.
10. Polish.

## Output expected

- Source code for all created or modified files.
- Summary of changes.
- Integration instructions for DI and navigation.
- New dependencies added to version catalogs and build files.
- Suggested next steps.

## Security considerations

- Never store sensitive data in plain text.
- Use HTTPS for all network calls.
- Validate on client and server.
- Sanitize user inputs.
- Use secure token storage.
