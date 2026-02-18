# Task Workflow: <short-title>

## Objective

- Describe the business/technical outcome in 1-2 sentences.

## Inputs

- Task context:
- Affected modules:
- Relevant constraints:

## Constraints

- Keep Clean Architecture boundaries (`api` contracts, `impl` implementations).
- Keep domain pure Kotlin (no Android/SDK dependencies).
- Use design-system components only in features.
- Keep navigation contracts in `core/navigation` and wiring in `app`.

## Steps

1. Analyze touched modules and list files to create/modify.
2. Update domain contracts/use cases (`api` when contract is public, `impl` for implementation).
3. Update data implementations and mappings.
4. Update presentation (`UiState`, `UiAction`, `UiEffect`, `ViewModel`, `Route`, `Screen`).
5. Wire dependency injection in feature `impl/di` and app Koin setup.
6. Wire navigation intents/contracts if needed.
7. Add or update tests and snapshots.
8. Run validation commands and fix failures.

## Acceptance Criteria

- `./gradlew :app:assembleDebug` succeeds.
- `lint`, `ktlintCheck`, and `detekt` pass for impacted modules.
- `./gradlew test` passes.
- Roborazzi verification passes for impacted modules (for example, `:core:design-system:verifyRoborazziDebug`).
- Emulator/device integration tests are out of scope unless explicitly requested.

## Project Snippets

```kotlin
data class FeatureState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface FeatureAction {
    data object OnRetryClick : FeatureAction
}

sealed interface FeatureEffect {
    data object NavigateBack : FeatureEffect
}
```

```kotlin
@Composable
fun FeatureRoute(
    viewModel: FeatureViewModel = koinViewModel(),
    onNavigate: (NavigationDestination) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FeatureScreen(state = state, onAction = viewModel::onAction)
}
```

```kotlin
@Test
fun GIVEN_validInput_WHEN_execute_THEN_returnSuccess() = runTest {
    // Arrange
    // Act
    // Assert
}
```

## Expected Tests

- Use case tests for success and failure branches.
- ViewModel tests for state transitions and effects.
- Roborazzi screen snapshot test for main flow states.
- Roborazzi component snapshot tests for extracted `presentation/components`.

## Execution Commands

```bash
./gradlew :app:assembleDebug
./gradlew lint ktlintCheck detekt
./gradlew test
./gradlew :core:design-system:verifyRoborazziDebug
./gradlew :features:pomodoro-timer:impl:verifyRoborazziDebug
```
