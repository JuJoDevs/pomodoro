# Task Workflow: settings-screen-feature

## Objective

- Implement a production-ready Settings screen as a dedicated feature module, integrated with Navigation3 and reachable from the Pomodoro Timer screen.
- Prioritize settings that are technically reliable with current architecture and Android platform constraints (permissions, notification behavior, and background reliability).

## Inputs

- Reference design:
  - `.ai/screens/settings-screen.png`
  - `.ai/screens/pomodoro-screen.png`
- Existing navigation and placeholders:
  - `app/src/main/kotlin/com/jujodevs/pomodoro/MainActivity.kt`
  - `core/navigation/src/main/kotlin/com/jujodevs/pomodoro/core/navigation/MainNavKey.kt`
- Timer feature and current config logic:
  - `features/pomodoro-timer/impl/src/main/kotlin/com/jujodevs/pomodoro/features/timer/presentation/TimerRoute.kt`
  - `features/pomodoro-timer/impl/src/main/kotlin/com/jujodevs/pomodoro/features/timer/presentation/TimerViewModel.kt`
  - `features/pomodoro-timer/impl/src/main/kotlin/com/jujodevs/pomodoro/features/timer/domain/usecase/UpdatePomodoroConfigUseCase.kt`
- Permissions and notification capabilities:
  - `core/ui/src/main/kotlin/com/jujodevs/pomodoro/core/ui/permissions/ExactAlarmPermissionEffect.kt`
  - `core/ui/src/main/kotlin/com/jujodevs/pomodoro/core/ui/permissions/NotificationPermissionEffect.kt`
  - `libs/notifications/impl/src/main/kotlin/com/jujodevs/pomodoro/libs/notifications/impl/NotificationChannelManagerImpl.kt`
  - `libs/notifications/impl/src/main/kotlin/com/jujodevs/pomodoro/libs/notifications/impl/NotificationHelper.kt`
- Design-system components to reuse:
  - `core/design-system/src/main/kotlin/com/jujodevs/pomodoro/core/designsystem/components/input/PomodoroSwitch.kt`
  - `core/design-system/src/main/kotlin/com/jujodevs/pomodoro/core/designsystem/components/input/PomodoroDropdown.kt`
  - `core/design-system/src/main/kotlin/com/jujodevs/pomodoro/core/designsystem/components/input/PomodoroSlider.kt`

Feasibility decisions for requested options:

- Fully feasible now:
  - Exact alarm permission CTA (open Android exact alarm permission screen).
  - Notification permission CTA (request/open system settings).
  - Reset settings to defaults (DataStore-backed).
- Feasible with constraints:
  - Sound/Vibration toggles: reliable persistence is easy; real effect on Android 8+ notifications is channel-level.
  - Alarm sound management with fixed channel: open channel settings (`ACTION_CHANNEL_NOTIFICATION_SETTINGS`) and show current channel sound label with best-effort resolution (`NotificationChannel.sound` + `RingtoneManager` title).
- Not reliably app-controllable:
  - Notification volume slider: final volume is controlled by system stream/channel policy.
- Phase-2 candidate:
  - In-app alarm sound picker (from system ringtone list) remains deferred to avoid channel migration/versioning complexity.

Assumptions unless product clarifies otherwise:

- First release of Settings will expose only options with predictable behavior.
- "Notification volume" row from mock will be replaced by "Open system sound settings" informational action.
- Settings screen is full-screen route (no modal/bottom-sheet behavior in phase 1).
- Auto-start controls remain in Timer screen for phase 1 because Settings already has enough content.
- Keep one fixed notification channel for Pomodoro completion sound and delegate sound changes to Android channel settings.

## Constraints

- Keep Clean Architecture boundaries (`api` contracts, `impl` implementations).
- Keep domain pure Kotlin with no Android/SDK dependencies.
- Use design-system components only in features.
- Keep navigation contracts in `core/navigation` and wiring in `app`.
- Keep one centralized scaffold in `app`; do not create another scaffold inside feature.
- Keep all new source/docs/tests in English.
- Preserve current timer reliability behavior (exact alarm + foreground fallback).

## Steps

1. Create feature modules and register them.
   - Add `:features:settings:api` and `:features:settings:impl` in `settings.gradle.kts`.
   - Add module dependencies in `app/build.gradle.kts`.
   - Create `features/settings/api/build.gradle.kts` and `features/settings/impl/build.gradle.kts` using existing feature conventions.

2. Define Settings domain contract in `features/settings/api`.
   - Add a settings model representing UI-relevant preferences.
   - Add repository interface for observing/updating settings.
   - Add optional navigation contract types if needed by app wiring.

3. Implement data layer in `features/settings/impl`.
   - Create DataStore-backed repository implementation.
   - Use existing `libs/datastore:api`.
   - Decide key strategy:
     - Reuse existing keys for values already used by timer.
     - Add new keys only when behavior is truly new.
   - Add default values aligned with current timer defaults.

4. Implement settings use cases in `features/settings/impl/domain/usecase`.
   - `ObserveSettingsUseCase`.
   - `UpdateSoundEnabledUseCase`.
   - `UpdateVibrationEnabledUseCase`.
   - `ResetSettingsToDefaultsUseCase`.

5. Implement MVI contracts and ViewModel in `features/settings/impl/presentation`.
   - Add `SettingsState`, `SettingsAction`, `SettingsEffect`, `SettingsViewModel`.
   - Model permission statuses and one-time effects for launching permission flows/system settings.
   - Include channel sound presentation state (for example `alarmSoundLabel`) and fallback values (`Silent`, `Custom sound`, `Unknown`).
   - Keep side effects in ViewModel effect channel, not directly in composables.

6. Implement Settings Route and Screen.
   - Add `SettingsRoute` and `SettingsScreen`.
   - Build reusable components in `presentation/components`:
     - feedback toggles section (sound/vibration),
     - permissions section (exact alarm + notifications),
     - system volume row (open system settings),
     - reset defaults action,
     - version row.
   - Add previews in each extracted component file.

7. Wire permission/system-setting effects.
   - Reuse `ExactAlarmPermissionEffect` and `NotificationPermissionEffect` patterns from `core/ui`.
   - Add explicit user-triggered CTA flow from Settings screen (not only app startup).
   - Add deep-link effect to Android notification channel settings for sound customization.
   - Ensure behavior for Android API-level differences is handled gracefully.

8. Integrate DI.
   - Create `features/settings/impl/src/main/kotlin/.../di/SettingsModule.kt`.
   - Register module in `app/src/main/kotlin/com/jujodevs/pomodoro/PomodoroApplication.kt`.

9. Integrate navigation from Pomodoro Timer to Settings feature.
   - Replace `SettingsScreen()` placeholder in `MainActivity.kt` with `SettingsRoute(...)`.
   - Keep timer-screen entry point (settings icon) and ensure it navigates to the feature destination.
   - Add back/close behavior from Settings to Home via `backStack.goBack()`.
   - Optional hardening: use `TimerEffect.NavigateToSettings` from timer feature if adding an in-screen settings trigger.

10. Align scaffold top bar behavior by destination.
    - Prevent stale timer title ("Focus") when entering Settings.
    - Show Settings title and back action while on Settings route.
    - Keep help/settings actions contextual to active route.

11. Add resources and localization-ready strings.
    - Add new string keys in `core/resources/src/main/res/values/strings.xml`.
    - Add matching entries in at least `core/resources/src/main/res/values-es/strings.xml`.
    - Avoid hardcoded text in feature/app code.

12. Add automated tests before merge.
    - Unit tests for settings use cases/repository.
    - ViewModel tests for actions, state transitions, and effects.
    - Roborazzi snapshots for Settings screen and key components.
    - Navigation regression tests for timer -> settings route and back handling (unit-level where feasible).

13. Address deferred scope explicitly.
    - Do not implement fake "notification volume control" logic that cannot affect system volume.
    - If mock keeps volume slider, make it clearly informational or defer to phase 2.
    - Defer in-app ringtone picker to phase 2; keep fixed channel + system settings deep-link in phase 1.

## Acceptance Criteria

- `./gradlew :app:assembleDebug` succeeds.
- Static analysis passes for impacted modules:
  - `lint`
  - `ktlintCheck`
  - `detekt`
- Unit tests pass:
  - `./gradlew test`
- Roborazzi verification passes for impacted modules:
  - `:features:settings:impl:verifyRoborazziDebug`
  - `:features:pomodoro-timer:impl:verifyRoborazziDebug` (regression if timer UI entrypoint changed)
- Settings destination is reachable from Pomodoro Timer screen and supports back navigation to timer.
- Timer auto-start controls remain in `pomodoro-timer` and keep current behavior.
- Settings shows the current completion-channel sound label (best-effort) and provides a button to open channel sound settings.
- Integration tests requiring emulator/device remain out of scope for now.

## Project Snippets

```kotlin
@Composable
fun AppNavigation(
    backStack: NavBackStack<NavKey>,
    snackbarHostState: SnackbarHostState,
    onPhaseChanged: (Int) -> Unit
) {
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<MainNavKey.Home> { /* TimerRoute(...) */ }
            entry<MainNavKey.Settings> {
                SettingsRoute(
                    onNavigateBack = { backStack.goBack() }
                )
            }
        }
    )
}
```

```kotlin
data class SettingsState(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val alarmSoundLabel: String = "Default",
    val canScheduleExactAlarms: Boolean = true,
    val hasNotificationPermission: Boolean = true
)

sealed interface SettingsAction {
    data class ToggleSound(val enabled: Boolean) : SettingsAction
    data class ToggleVibration(val enabled: Boolean) : SettingsAction
    data object OpenNotificationChannelSettings : SettingsAction
    data object GrantExactAlarmPermission : SettingsAction
    data object RequestNotificationPermission : SettingsAction
    data object OpenSystemSoundSettings : SettingsAction
    data object ResetDefaults : SettingsAction
}
```

```kotlin
@Test
fun GIVEN_soundEnabled_WHEN_toggleSoundOff_THEN_stateAndRepositoryAreUpdated() = runTest {
    // Arrange
    // Act
    // Assert
}
```

```kotlin
@RunWith(RobolectricTestRunner::class)
class SettingsScreenSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_default_snapshot() {
        composeTestRule.setContent { /* SettingsScreen(...) */ }
        composeTestRule.onRoot().captureRoboImage()
    }
}
```

## Expected Tests

- Repository / use case tests:
  - `GIVEN_defaultPreferences_WHEN_observeSettings_THEN_emitExpectedDefaults`
  - `GIVEN_soundToggle_WHEN_updateSoundEnabled_THEN_persistValue`
  - `GIVEN_resetRequest_WHEN_resetDefaults_THEN_restoreAllDefaultValues`
- ViewModel tests:
  - `GIVEN_toggleActions_WHEN_onAction_THEN_emitUpdatedState`
  - `GIVEN_channelSoundRead_WHEN_loaded_THEN_emitBestEffortAlarmSoundLabel`
  - `GIVEN_grantExactAlarmAction_WHEN_onAction_THEN_emitPermissionEffect`
  - `GIVEN_openNotificationChannelSettingsAction_WHEN_onAction_THEN_emitOpenChannelSettingsEffect`
- Navigation regression tests:
  - `GIVEN_timerScreen_WHEN_settingsActionClicked_THEN_navigateToSettingsDestination`
  - `GIVEN_settingsScreen_WHEN_backClicked_THEN_returnToTimer`
- Roborazzi screen snapshots:
  - default state,
  - permissions missing state,
  - reset confirmation state (if modal/dialog is included).
- Roborazzi component snapshots:
  - toggle row,
  - permissions row,
  - system volume informational row.

## Execution Commands

```bash
./gradlew :app:assembleDebug
./gradlew lint ktlintCheck detekt
./gradlew test
./gradlew :features:settings:impl:verifyRoborazziDebug
./gradlew :features:pomodoro-timer:impl:verifyRoborazziDebug
```
