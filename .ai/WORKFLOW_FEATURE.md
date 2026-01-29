# WORKFLOW_FEATURE.md - Feature Development Workflow for Pomodoro

## Overview

This document defines the step-by-step workflow for developing new features in the Pomodoro Android application. It is specifically tailored to the project's architecture: **Clean Architecture**, **MVI**, **Jetpack Compose**, and strict **api/impl modularization**.

> **Important**: This workflow must be followed in conjunction with [AGENTS.md](../AGENTS.md), which is the binding architectural specification for the project.

---

## Input Interpretation

Before starting any feature implementation, analyze all provided inputs:

### Screenshots / UI Designs
- Analyze visual design: colors, spacing, typography, components
- Identify interactive elements: buttons, text fields, checkboxes, sliders
- Determine visual states: normal, error, loading, disabled, empty
- Extract exact texts and labels (remember: all strings go to `strings.xml`)
- **Important**: Map UI components to design-system equivalents (never use Material3 directly)

### API Endpoints (if applicable)
- Full endpoint URL
- HTTP method (GET, POST, PUT, DELETE, PATCH)
- Required headers (Authorization, Content-Type, etc.)
- Request body structure
- Response structure (success and error cases)
- Expected HTTP status codes

### Persistence Requirements
- Type of persistence: DataStore (preferences), Room (structured data)
- Entities to store
- Data structure and relationships
- Required queries

### Task Description
- Main functionality to implement
- Required validations
- Post-action navigation
- Edge cases to consider
- Background execution requirements (AlarmManager, Foreground Service)

---

## Workflow Steps

### Step 1: Analysis and Planning

1. Review all provided inputs
2. Identify required modules (new feature module? libs module?)
3. Determine if existing modules need modification
4. List all files to create/modify
5. Identify dependencies on other modules

**Output**: Clear list of modules and files to create.

---

### Step 2: Create Module Structure

Each feature follows the `api/impl` separation:

```
features/
  [feature-name]/
    api/
      build.gradle.kts
      src/main/kotlin/com/jujodevs/pomodoro/features/[feature]/
        domain/
          model/           # Shared domain models
          usecase/         # Shared Use cases interfaces
        navigation/        # Navigation contracts
    impl/
      build.gradle.kts
      src/main/kotlin/com/jujodevs/pomodoro/features/[feature]/
        data/
          datasource/      # Local/Remote data sources
          repository/      # Repository implementations
          mapper/          # Data mappers
        domain/
          repository/      # Repository interfaces
          usecase/         # Use cases
        presentation/
          [screen]/
            [Screen]Route.kt
            [Screen]Screen.kt
            [Screen]ViewModel.kt
            [Screen]State.kt
            [Screen]Action.kt
            [Screen]Effect.kt
            components/    # Screen-specific components
        di/
          [Feature]Module.kt  # Koin module
      src/test/kotlin/...    # Unit tests
```

**Module Naming Convention**:
- Feature modules: `features:[feature-name]:api`, `features:[feature-name]:impl`
- Use kebab-case for feature names: `pomodoro`, `settings`, `statistics`

---

### Step 3: Implement Domain Layer

The domain layer is **pure Kotlin** with no Android dependencies.

#### 3.1 Domain Models (in `api` module)

```kotlin
// features/[feature]/api/.../domain/model/[Entity].kt
data class PomodoroSession(
    val id: String,
    val durationMinutes: Int,
    val startedAt: Long,
    val completedAt: Long?,
    val type: SessionType
)

enum class SessionType {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}
```

#### 3.2 Repository Interface (in `impl` module)

```kotlin
// features/[feature]/impl/.../domain/repository/[Feature]Repository.kt
interface PomodoroRepository {
    fun getActiveSessions(): Flow<List<PomodoroSession>>
    suspend fun startSession(type: SessionType): Result<PomodoroSession>
    suspend fun completeSession(sessionId: String): Result<Unit>
}
```

#### 3.3 Use Cases (in `impl` module)

```kotlin
// features/[feature]/impl/.../domain/usecase/[UseCase].kt
class StartPomodoroUseCase(
    private val repository: PomodoroRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(type: SessionType): Result<PomodoroSession> {
        return repository.startSession(type)
            .onSuccess { session ->
                notificationScheduler.scheduleEndNotification(session)
            }
    }
}
```

**Use Case Rules**:
- Single responsibility
- `operator fun invoke()` for execution
- Return `Result<T>` for operations that can fail
- Return `Flow<T>` for observable data
- No Android dependencies
- If need to share with other features create a shared interface in `api`

---

### Step 4: Implement Data Layer

#### 4.1 Data Models

```kotlin
// features/[feature]/impl/.../data/model/[Entity]Dto.kt (for network)
@Serializable
data class PomodoroSessionDto(
    @SerialName("id") val id: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("started_at") val startedAt: Long,
    @SerialName("completed_at") val completedAt: Long?,
    @SerialName("type") val type: String
)

// features/[feature]/impl/.../data/model/[Entity]Entity.kt (for Room)
@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey val id: String,
    val durationMinutes: Int,
    val startedAt: Long,
    val completedAt: Long?,
    val type: String
)
```

#### 4.2 Mappers

```kotlin
// features/[feature]/impl/.../data/mapper/[Entity]Mapper.kt
fun PomodoroSessionEntity.toDomain(): PomodoroSession = PomodoroSession(
    id = id,
    durationMinutes = durationMinutes,
    startedAt = startedAt,
    completedAt = completedAt,
    type = SessionType.valueOf(type)
)

fun PomodoroSession.toEntity(): PomodoroSessionEntity = PomodoroSessionEntity(
    id = id,
    durationMinutes = durationMinutes,
    startedAt = startedAt,
    completedAt = completedAt,
    type = type.name
)
```

#### 4.3 Data Sources

```kotlin
// features/[feature]/impl/.../data/datasource/[Feature]LocalDataSource.kt
interface PomodoroLocalDataSource {
    fun getSessions(): Flow<List<PomodoroSessionEntity>>
    suspend fun insertSession(session: PomodoroSessionEntity)
    suspend fun updateSession(session: PomodoroSessionEntity)
}

class PomodoroLocalDataSourceImpl(
    private val dao: PomodoroDao
) : PomodoroLocalDataSource {
    override fun getSessions(): Flow<List<PomodoroSessionEntity>> = dao.getAllSessions()
    override suspend fun insertSession(session: PomodoroSessionEntity) = dao.insert(session)
    override suspend fun updateSession(session: PomodoroSessionEntity) = dao.update(session)
}
```

#### 4.4 Repository Implementation

```kotlin
// features/[feature]/impl/.../data/repository/[Feature]RepositoryImpl.kt
class PomodoroRepositoryImpl(
    private val localDataSource: PomodoroLocalDataSource
) : PomodoroRepository {

    override fun getActiveSessions(): Flow<List<PomodoroSession>> =
        localDataSource.getSessions()
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun startSession(type: SessionType): Result<PomodoroSession> =
        runCatching {
            val session = PomodoroSession(
                id = UUID.randomUUID().toString(),
                durationMinutes = type.defaultDuration,
                startedAt = System.currentTimeMillis(),
                completedAt = null,
                type = type
            )
            localDataSource.insertSession(session.toEntity())
            session
        }

    override suspend fun completeSession(sessionId: String): Result<Unit> =
        runCatching {
            // Implementation
        }
}
```

---

### Step 5: Implement Presentation Layer (MVI)

#### 5.1 State Definition

```kotlin
// features/[feature]/impl/.../presentation/[screen]/[Screen]State.kt
data class PomodoroState(
    val isLoading: Boolean = false,
    val currentSession: PomodoroSession? = null,
    val remainingTimeMillis: Long = 0L,
    val sessionType: SessionType = SessionType.WORK,
    val isRunning: Boolean = false,
    val error: String? = null
)
```

#### 5.2 Actions Definition

```kotlin
// features/[feature]/impl/.../presentation/[screen]/[Screen]Action.kt
sealed interface PomodoroAction {
    data object StartSession : PomodoroAction
    data object PauseSession : PomodoroAction
    data object ResumeSession : PomodoroAction
    data object StopSession : PomodoroAction
    data object SkipToNext : PomodoroAction
    data class SelectSessionType(val type: SessionType) : PomodoroAction
    data object DismissError : PomodoroAction
}
```

#### 5.3 Effects Definition

```kotlin
// features/[feature]/impl/.../presentation/[screen]/[Screen]Effect.kt
sealed interface PomodoroEffect {
    data class NavigateTo(val destination: NavigationDestination) : PomodoroEffect
    data class ShowSnackbar(val message: String) : PomodoroEffect
    data object SessionCompleted : PomodoroEffect
}
```

#### 5.4 ViewModel

```kotlin
// features/[feature]/impl/.../presentation/[screen]/[Screen]ViewModel.kt
class PomodoroViewModel(
    private val startPomodoroUseCase: StartPomodoroUseCase,
    private val completePomodoroUseCase: CompletePomodoroUseCase,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private val _effects = Channel<PomodoroEffect>(Channel.BUFFERED)
    val effects: Flow<PomodoroEffect> = _effects.receiveAsFlow()

    fun onAction(action: PomodoroAction) {
        when (action) {
            is PomodoroAction.StartSession -> startSession()
            is PomodoroAction.PauseSession -> pauseSession()
            is PomodoroAction.ResumeSession -> resumeSession()
            is PomodoroAction.StopSession -> stopSession()
            is PomodoroAction.SkipToNext -> skipToNext()
            is PomodoroAction.SelectSessionType -> selectSessionType(action.type)
            is PomodoroAction.DismissError -> dismissError()
        }
    }

    private fun startSession() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            startPomodoroUseCase(_state.value.sessionType)
                .onSuccess { session ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            currentSession = session,
                            isRunning = true
                        )
                    }
                    analyticsTracker.track(PomodoroStartedEvent(session.type))
                }
                .onFailure { error ->
                    _state.update { 
                        it.copy(isLoading = false, error = error.message)
                    }
                }
        }
    }

    // ... other action handlers
}
```

#### 5.5 Route (ViewModel Connection)

```kotlin
// features/[feature]/impl/.../presentation/[screen]/[Screen]Route.kt
@Composable
fun PomodoroRoute(
    viewModel: PomodoroViewModel = koinViewModel(),
    onNavigate: (NavigationDestination) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PomodoroEffect.NavigateTo -> onNavigate(effect.destination)
                is PomodoroEffect.ShowSnackbar -> { /* Handle snackbar */ }
                is PomodoroEffect.SessionCompleted -> { /* Handle completion */ }
            }
        }
    }

    PomodoroScreen(
        state = state,
        onAction = viewModel::onAction
    )
}
```

#### 5.6 Screen (Pure UI)

```kotlin
// features/[feature]/impl/.../presentation/[screen]/[Screen]Screen.kt
@Composable
fun PomodoroScreen(
    state: PomodoroState,
    onAction: (PomodoroAction) -> Unit
) {
    // Use ONLY design-system components, NOT Material3 directly
    PomodoroScaffold { // From design-system
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Timer display
            PomodoroTimerDisplay(
                remainingTimeMillis = state.remainingTimeMillis,
                isRunning = state.isRunning
            )

            Spacer(modifier = Modifier.height(PomodoroSpacing.large))

            // Control buttons
            PomodoroControlButtons(
                isRunning = state.isRunning,
                onStart = { onAction(PomodoroAction.StartSession) },
                onPause = { onAction(PomodoroAction.PauseSession) },
                onStop = { onAction(PomodoroAction.StopSession) }
            )

            // Loading indicator
            if (state.isLoading) {
                PomodoroLoadingIndicator()
            }

            // Error display
            state.error?.let { error ->
                PomodoroErrorBanner(
                    message = error,
                    onDismiss = { onAction(PomodoroAction.DismissError) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PomodoroScreenPreview() {
    PomodoroTheme {
        PomodoroScreen(
            state = PomodoroState(
                remainingTimeMillis = 25 * 60 * 1000L,
                sessionType = SessionType.WORK,
                isRunning = false
            ),
            onAction = {}
        )
    }
}
```

---

### Step 6: Navigation Contracts

```kotlin
// features/[feature]/api/.../navigation/[Feature]NavigationContract.kt
sealed interface PomodoroDestination : NavigationDestination {
    data object Timer : PomodoroDestination
    data object Settings : PomodoroDestination
    data class Statistics(val sessionId: String) : PomodoroDestination
}
```

Navigation wiring lives in the `app` module, not in features.

---

### Step 7: Dependency Injection (Koin)

```kotlin
// features/[feature]/impl/.../di/[Feature]Module.kt
val pomodoroModule = module {
    // Data sources
    single<PomodoroLocalDataSource> { PomodoroLocalDataSourceImpl(get()) }

    // Repositories
    single<PomodoroRepository> { PomodoroRepositoryImpl(get()) }

    // Use cases
    factory { StartPomodoroUseCase(get(), get()) }
    factory { CompletePomodoroUseCase(get()) }

    // ViewModels
    viewModel { PomodoroViewModel(get(), get(), get()) }
}
```

Register the module in the app module's Koin configuration.

---

### Step 8: Implement Validations

**Where to validate**:
- **UI Layer**: Immediate visual feedback (field formatting)
- **ViewModel**: Business logic validation (before API calls)
- **Domain Use Case**: Critical business rules

```kotlin
// Validation in ViewModel
private fun validateSessionDuration(minutes: Int): ValidationResult {
    return when {
        minutes < MIN_SESSION_DURATION -> ValidationResult.Error("Duration too short")
        minutes > MAX_SESSION_DURATION -> ValidationResult.Error("Duration too long")
        else -> ValidationResult.Success
    }
}

sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Error(val message: String) : ValidationResult
}
```

---

### Step 9: Error Handling

```kotlin
// Sealed class for Result type (use Kotlin's built-in Result or custom)
sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val exception: Throwable) : DataResult<Nothing>()
}

// Extension for common error mapping
fun <T> Result<T>.toUiError(): String? = exceptionOrNull()?.let { error ->
    when (error) {
        is IOException -> "Network error. Please check your connection."
        is HttpException -> "Server error. Please try again later."
        else -> error.message ?: "Unknown error occurred."
    }
}
```

**Error Types to Handle**:
- Network errors (timeout, no connection)
- API errors (4xx, 5xx)
- Validation errors
- Database errors
- Background execution failures

---

### Step 10: Testing

#### 10.1 ViewModel Unit Tests (JUnit5 + MockK + Turbine)

```kotlin
// features/[feature]/impl/src/test/.../presentation/[Screen]ViewModelTest.kt
class PomodoroViewModelTest {

    private val startPomodoroUseCase: StartPomodoroUseCase = mockk()
    private val completePomodoroUseCase: CompletePomodoroUseCase = mockk()
    private val analyticsTracker: AnalyticsTracker = mockk(relaxed = true)

    private lateinit var viewModel: PomodoroViewModel

    @BeforeEach
    fun setUp() {
        viewModel = PomodoroViewModel(
            startPomodoroUseCase,
            completePomodoroUseCase,
            analyticsTracker
        )
    }

    @Test
    fun `GIVEN idle state WHEN StartSession action THEN state should show loading`() = runTest {
        // GIVEN
        coEvery { startPomodoroUseCase(any()) } returns Result.success(mockSession)

        // WHEN
        viewModel.onAction(PomodoroAction.StartSession)

        // THEN
        viewModel.state.test {
            val state = awaitItem()
            state.isLoading shouldBe true
        }
    }

    @Test
    fun `GIVEN running session WHEN PauseSession action THEN isRunning should be false`() = runTest {
        // GIVEN
        // ... setup running state

        // WHEN
        viewModel.onAction(PomodoroAction.PauseSession)

        // THEN
        viewModel.state.test {
            val state = awaitItem()
            state.isRunning shouldBe false
        }
    }
}
```

#### 10.2 Use Case Unit Tests

```kotlin
class StartPomodoroUseCaseTest {

    private val repository: PomodoroRepository = mockk()
    private val notificationScheduler: NotificationScheduler = mockk(relaxed = true)

    private lateinit var useCase: StartPomodoroUseCase

    @BeforeEach
    fun setUp() {
        useCase = StartPomodoroUseCase(repository, notificationScheduler)
    }

    @Test
    fun `GIVEN valid session type WHEN invoke THEN should schedule notification`() = runTest {
        // GIVEN
        val sessionType = SessionType.WORK
        val expectedSession = mockSession
        coEvery { repository.startSession(sessionType) } returns Result.success(expectedSession)

        // WHEN
        val result = useCase(sessionType)

        // THEN
        result.isSuccess shouldBe true
        coVerify(exactly = 1) { notificationScheduler.scheduleEndNotification(expectedSession) }
    }
}
```

#### 10.3 Snapshot Tests (Roborazzi) - Parameterized

Always prefer parameterized tests for snapshot testing when possible. This approach:
- Reduces code duplication
- Makes it easy to add new states
- Ensures consistent test structure
- Improves maintainability
- JVM-based testing with Robolectric (no emulator needed)

```kotlin
// features/[feature]/impl/src/test/.../presentation/[Screen]ScreenSnapshotTest.kt
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class PomodoroScreenSnapshotTest {

    @get:Rule
    val roborazziRule = RoborazziRule(
        options = RoborazziRule.Options(
            captureType = RoborazziRule.CaptureType.LastImage()
        )
    )

    // Define all screen states to test
    enum class ScreenState(
        val state: PomodoroState,
        val description: String
    ) {
        IDLE(
            state = PomodoroState(),
            description = "idle"
        ),
        LOADING(
            state = PomodoroState(isLoading = true),
            description = "loading"
        ),
        RUNNING_WORK(
            state = PomodoroState(
                isRunning = true,
                remainingTimeMillis = 15 * 60 * 1000L,
                sessionType = SessionType.WORK
            ),
            description = "running_work_session"
        ),
        RUNNING_BREAK(
            state = PomodoroState(
                isRunning = true,
                remainingTimeMillis = 5 * 60 * 1000L,
                sessionType = SessionType.SHORT_BREAK
            ),
            description = "running_short_break"
        ),
        PAUSED(
            state = PomodoroState(
                isRunning = false,
                remainingTimeMillis = 10 * 60 * 1000L,
                currentSession = mockSession
            ),
            description = "paused"
        ),
        ERROR(
            state = PomodoroState(error = "Something went wrong"),
            description = "error"
        ),
        COMPLETED(
            state = PomodoroState(
                remainingTimeMillis = 0L,
                isRunning = false
            ),
            description = "completed"
        )
    }

    @ParameterizedTest(name = "pomodoro_screen_{0}")
    @EnumSource(ScreenState::class)
    fun `snapshot pomodoro screen`(screenState: ScreenState) {
        composeTestRule.setContent {
            PomodoroTheme {
                PomodoroScreen(
                    state = screenState.state,
                    onAction = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("${screenState.description}.png")
    }
}
```

**Alternative: Using `@MethodSource` for complex states**

For more complex scenarios where enum is not sufficient:

```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class PomodoroScreenSnapshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    companion object {
        @JvmStatic
        fun screenStates(): Stream<Arguments> = Stream.of(
            Arguments.of("idle", PomodoroState()),
            Arguments.of("loading", PomodoroState(isLoading = true)),
            Arguments.of("running_work", PomodoroState(
                isRunning = true,
                remainingTimeMillis = 15 * 60 * 1000L,
                sessionType = SessionType.WORK
            )),
            Arguments.of("running_short_break", PomodoroState(
                isRunning = true,
                remainingTimeMillis = 5 * 60 * 1000L,
                sessionType = SessionType.SHORT_BREAK
            )),
            Arguments.of("running_long_break", PomodoroState(
                isRunning = true,
                remainingTimeMillis = 15 * 60 * 1000L,
                sessionType = SessionType.LONG_BREAK
            )),
            Arguments.of("paused", PomodoroState(
                isRunning = false,
                remainingTimeMillis = 10 * 60 * 1000L
            )),
            Arguments.of("error", PomodoroState(error = "Connection failed")),
            Arguments.of("error_long_message", PomodoroState(
                error = "Unable to save session. Please check your connection and try again."
            )),
            Arguments.of("completed", PomodoroState(remainingTimeMillis = 0L))
        )
    }

    @ParameterizedTest(name = "pomodoro_screen_{0}")
    @MethodSource("screenStates")
    fun `snapshot pomodoro screen`(name: String, state: PomodoroState) {
        composeTestRule.setContent {
            PomodoroTheme {
                PomodoroScreen(
                    state = state,
                    onAction = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("$name.png")
    }
}
```

**Multi-device Parameterized Tests**

For testing across multiple device configurations with Roborazzi:

```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PomodoroScreenMultiDeviceSnapshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    companion object {
        @JvmStatic
        fun deviceConfigs(): Stream<Arguments> = Stream.of(
            Arguments.of("phone_portrait", RobolectricDeviceQualifiers.Pixel5),
            Arguments.of("phone_landscape", RobolectricDeviceQualifiers.Pixel5 + "-land"),
            Arguments.of("tablet", RobolectricDeviceQualifiers.NexusOne)
        )
    }

    @ParameterizedTest(name = "pomodoro_screen_{0}")
    @MethodSource("deviceConfigs")
    @Config(qualifiers = "w411dp-h914dp-xxhdpi")
    fun `snapshot pomodoro screen on different devices`(name: String, qualifiers: String) {
        composeTestRule.setContent {
            PomodoroTheme {
                PomodoroScreen(
                    state = PomodoroState(
                        isRunning = true,
                        remainingTimeMillis = 25 * 60 * 1000L
                    ),
                    onAction = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("$name.png")
    }
}
```

**Design System Component Parameterized Tests**

```kotlin
// core/design-system/src/test/.../PomodoroButtonSnapshotTest.kt
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class PomodoroButtonSnapshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    enum class ButtonState(
        val enabled: Boolean,
        val loading: Boolean,
        val description: String
    ) {
        ENABLED(enabled = true, loading = false, description = "enabled"),
        DISABLED(enabled = false, loading = false, description = "disabled"),
        LOADING(enabled = true, loading = true, description = "loading")
    }

    enum class ButtonVariant(val description: String) {
        PRIMARY("primary"),
        SECONDARY("secondary"),
        OUTLINED("outlined")
    }

    @ParameterizedTest(name = "button_{0}_{1}")
    @CsvSource(
        "PRIMARY, ENABLED",
        "PRIMARY, DISABLED",
        "PRIMARY, LOADING",
        "SECONDARY, ENABLED",
        "SECONDARY, DISABLED",
        "OUTLINED, ENABLED",
        "OUTLINED, DISABLED"
    )
    fun `snapshot button variants`(variant: ButtonVariant, state: ButtonState) {
        composeTestRule.setContent {
            PomodoroTheme {
                when (variant) {
                    ButtonVariant.PRIMARY -> PomodoroButton(
                        text = "Start",
                        onClick = {},
                        enabled = state.enabled,
                        isLoading = state.loading
                    )
                    ButtonVariant.SECONDARY -> PomodoroSecondaryButton(
                        text = "Cancel",
                        onClick = {},
                        enabled = state.enabled
                    )
                    ButtonVariant.OUTLINED -> PomodoroOutlinedButton(
                        text = "Skip",
                        onClick = {},
                        enabled = state.enabled
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage("${variant.description}_${state.description}.png")
    }
}
```

---

## File Structure Summary

```
features/
  [feature-name]/
    api/
      build.gradle.kts
      src/main/java/com/jujodevs/pomodoro/features/[feature]/
        domain/
          model/
            [Entity].kt
          repository/
            [Feature]Repository.kt
        navigation/
          [Feature]NavigationContract.kt
    impl/
      build.gradle.kts
      src/main/java/com/jujodevs/pomodoro/features/[feature]/
        data/
          datasource/
            [Feature]LocalDataSource.kt
            [Feature]RemoteDataSource.kt (if needed)
          repository/
            [Feature]RepositoryImpl.kt
          mapper/
            [Entity]Mapper.kt
          model/
            [Entity]Entity.kt (Room)
            [Entity]Dto.kt (Network)
        domain/
          usecase/
            [Action][Feature]UseCase.kt
        presentation/
          [screen]/
            [Screen]Route.kt
            [Screen]Screen.kt
            [Screen]ViewModel.kt
            [Screen]State.kt
            [Screen]Action.kt
            [Screen]Effect.kt
            components/
              [Component].kt
        di/
          [Feature]Module.kt
      src/test/java/com/jujodevs/pomodoro/features/[feature]/
        domain/usecase/
          [UseCase]Test.kt
        presentation/[screen]/
          [Screen]ViewModelTest.kt
          [Screen]ScreenSnapshotTest.kt
```

---

## Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Feature module | kebab-case | `pomodoro`, `settings` |
| Screen | PascalCase + Screen | `PomodoroScreen.kt` |
| Route | PascalCase + Route | `PomodoroRoute.kt` |
| ViewModel | PascalCase + ViewModel | `PomodoroViewModel.kt` |
| State | PascalCase + State | `PomodoroState.kt` |
| Action | PascalCase + Action | `PomodoroAction.kt` |
| Effect | PascalCase + Effect | `PomodoroEffect.kt` |
| Repository | PascalCase + Repository | `PomodoroRepository.kt` |
| Repository Impl | PascalCase + RepositoryImpl | `PomodoroRepositoryImpl.kt` |
| Use Case | Verb + Noun + UseCase | `StartPomodoroUseCase.kt` |
| Entity (Room) | PascalCase + Entity | `PomodoroSessionEntity.kt` |
| DTO (Network) | PascalCase + Dto | `PomodoroSessionDto.kt` |
| Mapper | PascalCase + Mapper | `PomodoroSessionMapper.kt` |
| DI Module | PascalCase + Module | `PomodoroModule.kt` |
| Test | Class + Test | `PomodoroViewModelTest.kt` |

---

## Completeness Checklist

Before marking a feature as complete, verify:

### Architecture
- [ ] `api` module contains only contracts (interfaces, models, navigation)
- [ ] `impl` module contains all implementations
- [ ] Domain layer has no Android dependencies
- [ ] Repository interface in `api`, implementation in `impl`

### MVI Implementation
- [ ] State is immutable data class
- [ ] Actions are sealed interface
- [ ] Effects are sealed interface for one-time events
- [ ] ViewModel exposes StateFlow for state
- [ ] ViewModel exposes Flow for effects

### UI
- [ ] Route/Screen separation implemented
- [ ] Screen receives state and onAction lambda
- [ ] Only design-system components used (no direct Material3)
- [ ] Previews created for Screen
- [ ] All strings in `strings.xml`
- [ ] UI matches provided design/screenshots

### Navigation
- [ ] Navigation contract defined in `api` module
- [ ] Navigation wiring in `app` module
- [ ] Features emit navigation intents only

### Dependency Injection
- [ ] Koin module created in `impl`
- [ ] Module registered in app's Koin configuration

### Error Handling
- [ ] Network errors handled
- [ ] Validation errors handled
- [ ] Error state displayed in UI
- [ ] Errors can be dismissed

### Testing
- [ ] ViewModel unit tests with JUnit5 + MockK + Turbine
- [ ] Use case unit tests
- [ ] Snapshot tests with Roborazzi
- [ ] GIVEN/WHEN/THEN naming convention
- [ ] Domain tests have no Android dependencies

### Background Execution (if applicable)
- [ ] AlarmManager/Foreground Service implementation in `libs/notifications`
- [ ] Works with screen locked
- [ ] Battery efficient
- [ ] Complies with Android restrictions

---

## Anti-Patterns to Avoid

| Anti-Pattern | Correct Approach |
|--------------|------------------|
| Android dependencies in domain | Keep domain pure Kotlin |
| ViewModels in `api` module | ViewModels go in `impl` |
| Direct Material3 in features | Use design-system components |
| Manual singletons | Use Koin DI |
| Direct navigation calls from UI | Emit navigation intents via Effects |
| LiveData | Use StateFlow/Flow |
| MVVM with two-way binding | Use MVI with unidirectional flow |
| kapt | Use KSP |
| Hardcoded strings in code | Use strings.xml |
| Logging in domain | Use Timber only in data/presentation |
| Mixed Screen/Route responsibilities | Separate Route (VM connection) from Screen (pure UI) |

---

## Priority Order for Implementation

1. **Domain contracts** (models, repository interfaces in `api`)
2. **Domain logic** (use cases in `impl`)
3. **Data layer** (repositories, data sources, mappers)
4. **MVI contracts** (State, Action, Effect)
5. **ViewModel** (business logic)
6. **UI** (Screen + Route)
7. **DI module** (Koin)
8. **Error handling** (comprehensive)
9. **Testing** (unit + snapshot)
10. **Polish** (UI refinements)

---

## Output Expected After Feature Completion

1. **Source code** for all created/modified files
2. **Summary** of changes made
3. **Integration instructions** (if needed)
4. **New dependencies** to add in `libs.versions.toml` and `build.gradle.kts`
5. **Koin module registration** instructions
6. **Suggested next steps** (if applicable)

---

## Security Considerations

- Never store sensitive data in plain text
- Use HTTPS for all network calls
- Validate on client AND server
- Sanitize user inputs
- Use secure token storage for authentication
- Follow Android security best practices for background execution

---

**This workflow document complements AGENTS.md and must be followed for all feature development.**
