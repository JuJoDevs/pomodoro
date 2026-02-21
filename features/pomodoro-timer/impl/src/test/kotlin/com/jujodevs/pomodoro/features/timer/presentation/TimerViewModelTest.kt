package com.jujodevs.pomodoro.features.timer.presentation

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.turbineScope
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.core.testing.extenion.CoroutineTestExtension
import com.jujodevs.pomodoro.core.ui.UiText
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.usecase.AdvancePomodoroPhaseUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.ObservePomodoroSessionStateUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.PausePomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.ResetPomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.SkipPomodoroPhaseUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.StartOrResumePomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.StopPomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.UpdatePomodoroConfigUseCase
import com.jujodevs.pomodoro.features.timer.fakes.FakePomodoroRepository
import com.jujodevs.pomodoro.features.timer.fakes.FakeTimeProvider
import com.jujodevs.pomodoro.libs.notifications.NotificationData
import com.jujodevs.pomodoro.libs.notifications.NotificationScheduler
import com.jujodevs.pomodoro.libs.notifications.RunningTimerNotificationData
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEvent
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEventType
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsPeriod
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsSummary
import com.jujodevs.pomodoro.libs.usagestats.domain.repository.UsageStatsRepository
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.RecordUsageStatsEventUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class TimerViewModelTest {
    @RegisterExtension
    val coroutineTestExtension = CoroutineTestExtension()

    private lateinit var repository: FakePomodoroRepository
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var viewModel: TimerViewModel
    private lateinit var notificationScheduler: FakeNotificationScheduler
    private lateinit var usageStatsRepository: FakeUsageStatsRepository

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        timeProvider = FakeTimeProvider()
        notificationScheduler = FakeNotificationScheduler()
        usageStatsRepository = FakeUsageStatsRepository()
        viewModel =
            TimerViewModel(
                useCases =
                    TimerUseCases(
                        observeSessionState = ObservePomodoroSessionStateUseCase(repository),
                        startOrResume = StartOrResumePomodoroUseCase(repository, timeProvider),
                        pause = PausePomodoroUseCase(repository, timeProvider),
                        skip = SkipPomodoroPhaseUseCase(repository),
                        stop = StopPomodoroUseCase(repository),
                        reset = ResetPomodoroUseCase(repository),
                        advancePhase = AdvancePomodoroPhaseUseCase(repository, timeProvider),
                        updateConfig = UpdatePomodoroConfigUseCase(repository),
                    ),
                notificationScheduler = notificationScheduler,
                recordUsageStatsEvent = RecordUsageStatsEventUseCase(usageStatsRepository),
                timeProvider = timeProvider,
                stateSyncDebounceMs = 0L,
            )
    }

    @Test
    fun `GIVEN running state without end timestamp WHEN syncing flow THEN phase should not advance`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()

                repository.updateSessionState(
                    PomodoroSessionState(
                        currentPhase = PomodoroPhase.WORK,
                        status = PomodoroStatus.RUNNING,
                        remainingMillis = 25 * 60 * 1000L,
                        phaseToken = "token-1",
                        lastKnownEndTimestamp = null,
                    ),
                )

                // WHEN
                val uiState = state.awaitItem()

                // THEN
                uiState.status shouldBeEqualTo PomodoroStatus.RUNNING
                uiState.phase shouldBeEqualTo PomodoroPhase.WORK

                val repoState = repository.getSessionState().first()
                repoState.currentPhase shouldBeEqualTo PomodoroPhase.WORK
                repoState.status shouldBeEqualTo PomodoroStatus.RUNNING
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN running timer WHEN syncing flow THEN foreground should start once with expected payload`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()
                val initialStopCalls = notificationScheduler.foregroundStopCalls

                repository.updateSessionState(
                    PomodoroSessionState(
                        currentPhase = PomodoroPhase.WORK,
                        status = PomodoroStatus.RUNNING,
                        remainingMillis = 25 * 60 * 1000L,
                        completedWorkSessions = 2,
                        phaseToken = "token-1",
                        lastKnownEndTimestamp = timeProvider.currentTime + 10_000L,
                    ),
                )

                // WHEN
                val runningState = state.awaitItem()
                runningState.status shouldBeEqualTo PomodoroStatus.RUNNING

                notificationScheduler.foregroundStartCalls shouldBeEqualTo 1
                val payload = notificationScheduler.lastForegroundNotification
                payload?.titleResId shouldBeEqualTo R.string.status_focusing
                payload?.messageResId shouldBeEqualTo R.string.label_sessions_completed
                payload?.messageArgFirst shouldBeEqualTo 2
                payload?.messageArgSecond shouldBeEqualTo 4

                repository.updateSessionState(PomodoroSessionState(status = PomodoroStatus.IDLE))

                // THEN
                val idleState = state.awaitItem()
                idleState.status shouldBeEqualTo PomodoroStatus.IDLE
                notificationScheduler.foregroundStopCalls shouldBeEqualTo initialStopCalls + 1
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN running state WHEN stop action is triggered THEN stop confirmation should be shown`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()
                repository.updateSessionState(PomodoroSessionState(status = PomodoroStatus.RUNNING))
                state.awaitItem()

                // WHEN
                viewModel.onAction(TimerAction.Stop)

                // THEN
                val currentState = state.awaitItem()
                currentState.showStopConfirmation shouldBeEqualTo true
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN timer started WHEN start action THEN usage start event is recorded`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                state.awaitInitialSyncedState()

                // WHEN
                viewModel.onAction(TimerAction.Start)
                state.awaitItem()

                // THEN
                val hasStartEvent =
                    usageStatsRepository.recordedEvents
                        .any { it.type == UsageStatsEventType.PHASE_STARTED }
                hasStartEvent shouldBeEqualTo true

                repository.updateSessionState(PomodoroSessionState(status = PomodoroStatus.IDLE))
                state.awaitItem()
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN running phase WHEN pause action THEN elapsed usage time is recorded`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                state.awaitInitialSyncedState()

                val remainingMillis = 20 * 60 * 1000L
                repository.updateSessionState(
                    PomodoroSessionState(
                        currentPhase = PomodoroPhase.WORK,
                        status = PomodoroStatus.RUNNING,
                        remainingMillis = remainingMillis,
                        phaseToken = "token-1",
                        lastKnownEndTimestamp = timeProvider.currentTime + remainingMillis,
                    ),
                )
                state.awaitItem()

                viewModel.onAction(TimerAction.Pause)
                state.awaitItem()

                val recordedElapsedEvent =
                    usageStatsRepository.recordedEvents.firstOrNull {
                        it.type == UsageStatsEventType.PHASE_TIME_RECORDED
                    }
                recordedElapsedEvent?.durationMillis shouldBeEqualTo 5 * 60 * 1000L
                usageStatsRepository.recordedEvents.any {
                    it.type == UsageStatsEventType.PHASE_PAUSED
                } shouldBeEqualTo true
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN elapsed recorded on pause WHEN phase completes THEN only remaining delta is recorded`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                state.awaitInitialSyncedState()

                val remainingMillis = 20 * 60 * 1000L
                repository.updateSessionState(
                    PomodoroSessionState(
                        currentPhase = PomodoroPhase.WORK,
                        status = PomodoroStatus.RUNNING,
                        remainingMillis = remainingMillis,
                        phaseToken = "token-1",
                        lastKnownEndTimestamp = timeProvider.currentTime + remainingMillis,
                    ),
                )
                state.awaitItem()

                viewModel.onAction(TimerAction.Pause)
                state.awaitItem()

                viewModel.onAction(TimerAction.Resume)
                state.awaitItem()

                timeProvider.currentTime += remainingMillis + 2_000L
                state.awaitItem()

                val recordedWorkDurations =
                    usageStatsRepository.recordedEvents
                        .filter {
                            it.type == UsageStatsEventType.PHASE_TIME_RECORDED &&
                                it.phase?.name == "WORK"
                        }.mapNotNull { it.durationMillis }

                recordedWorkDurations.sum() shouldBeEqualTo 25 * 60 * 1000L
                usageStatsRepository.recordedEvents.any {
                    it.type == UsageStatsEventType.PHASE_COMPLETED &&
                        it.phase?.name == "WORK"
                } shouldBeEqualTo true
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN running work phase WHEN timer reaches zero THEN completion and cycle are recorded`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                state.awaitInitialSyncedState()

                repository.updateSessionState(
                    PomodoroSessionState(
                        currentPhase = PomodoroPhase.WORK,
                        status = PomodoroStatus.RUNNING,
                        completedWorkSessions = 3,
                        remainingMillis = 1000L,
                        phaseToken = "token-1",
                        lastKnownEndTimestamp = timeProvider.currentTime + 200L,
                    ),
                )
                state.awaitItem()

                // WHEN
                timeProvider.currentTime += 1200L
                state.awaitItem()

                // THEN
                usageStatsRepository.recordedEvents.any {
                    it.type == UsageStatsEventType.PHASE_COMPLETED && it.phase?.name == "WORK"
                } shouldBeEqualTo true
                val hasCycleEvent =
                    usageStatsRepository.recordedEvents
                        .any { it.type == UsageStatsEventType.CYCLE_COMPLETED }
                hasCycleEvent shouldBeEqualTo true
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN stop confirmation visible WHEN user confirms THEN timer should stop`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()
                repository.updateSessionState(PomodoroSessionState(status = PomodoroStatus.RUNNING))
                state.awaitItem()

                viewModel.onAction(TimerAction.Stop)
                state.awaitItem()

                // WHEN
                viewModel.onAction(TimerAction.ConfirmStop)
                state.skipItems(1)
                val stoppedState = state.awaitItem()

                // THEN
                stoppedState.status shouldBeEqualTo PomodoroStatus.IDLE
                stoppedState.showStopConfirmation shouldBeEqualTo false

                val repoState = repository.getSessionState().first()
                repoState.phaseToken shouldBeEqualTo ""
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN completed sessions WHEN reset action is triggered THEN reset confirmation should be shown`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()
                repository.updateSessionState(
                    PomodoroSessionState(
                        status = PomodoroStatus.IDLE,
                        currentPhase = PomodoroPhase.SHORT_BREAK,
                        completedWorkSessions = 2,
                    ),
                )
                state.awaitItem()

                // WHEN
                viewModel.onAction(TimerAction.Reset)

                // THEN
                val resetState = state.awaitItem()
                resetState.showResetConfirmation shouldBeEqualTo true
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN reset confirmation visible WHEN user confirms THEN progress should be reset`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()
                repository.updateSessionState(
                    PomodoroSessionState(
                        status = PomodoroStatus.IDLE,
                        currentPhase = PomodoroPhase.SHORT_BREAK,
                        selectedWorkMinutes = 30,
                        completedWorkSessions = 3,
                    ),
                )
                state.awaitItem()

                viewModel.onAction(TimerAction.Reset)
                state.awaitItem()

                // WHEN
                viewModel.onAction(TimerAction.ConfirmReset)
                state.skipItems(1)
                val updatedState = state.awaitItem()

                // THEN
                updatedState.phase shouldBeEqualTo PomodoroPhase.WORK
                updatedState.completedSessions shouldBeEqualTo 0
                updatedState.showResetConfirmation shouldBeEqualTo false
                updatedState.remainingTimeText shouldBeEqualTo "30:00"
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN exact alarm scheduling fails WHEN warning is dismissed THEN warning state should reset`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()
                notificationScheduler.scheduleError = DataError.Local.INSUFFICIENT_PERMISSIONS

                repository.updateSessionState(
                    PomodoroSessionState(
                        status = PomodoroStatus.RUNNING,
                        phaseToken = "token-1",
                        lastKnownEndTimestamp = timeProvider.currentTime + 5_000L,
                    ),
                )
                state.skipItems(1)
                val warningState = state.awaitItem()
                warningState.isExactAlarmPermissionMissing shouldBeEqualTo true

                // WHEN
                viewModel.onAction(TimerAction.DismissExactAlarmWarning)
                val updatedState = state.awaitItem()

                // THEN
                updatedState.isExactAlarmPermissionMissing shouldBeEqualTo false
                repository.updateSessionState(PomodoroSessionState(status = PomodoroStatus.IDLE))
                state.awaitItem()
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN dismissed warning WHEN scheduling fails again THEN warning stays hidden during snooze`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()
                notificationScheduler.scheduleError = DataError.Local.INSUFFICIENT_PERMISSIONS
                viewModel.onAction(TimerAction.UpdateExactAlarmPermission(isGranted = false))
                state.awaitItem()

                repository.updateSessionState(
                    PomodoroSessionState(
                        status = PomodoroStatus.RUNNING,
                        phaseToken = "token-1",
                        lastKnownEndTimestamp = timeProvider.currentTime + 5_000L,
                    ),
                )
                state.skipItems(1)
                state.awaitItem().isExactAlarmPermissionMissing shouldBeEqualTo true

                viewModel.onAction(TimerAction.DismissExactAlarmWarning)
                state.awaitItem().isExactAlarmPermissionMissing shouldBeEqualTo false

                // WHEN
                repository.updateSessionState(
                    PomodoroSessionState(
                        status = PomodoroStatus.RUNNING,
                        phaseToken = "token-2",
                        lastKnownEndTimestamp = timeProvider.currentTime + 10_000L,
                        exactAlarmWarningSnoozedUntilMillis = timeProvider.currentTime + 1_000L,
                    ),
                )
                state.skipItems(1)
                val updatedState = state.awaitItem()

                // THEN
                updatedState.isExactAlarmPermissionMissing shouldBeEqualTo false
                repository.updateSessionState(PomodoroSessionState(status = PomodoroStatus.IDLE))
                state.awaitItem()
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN schedule notification fails WHEN syncing running state THEN show generic message effect`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                val effects = viewModel.effects.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()
                notificationScheduler.scheduleError = DataError.Local.UNKNOWN

                // WHEN
                repository.updateSessionState(
                    PomodoroSessionState(
                        status = PomodoroStatus.RUNNING,
                        phaseToken = "token-1",
                        lastKnownEndTimestamp = timeProvider.currentTime + 5_000L,
                    ),
                )
                state.awaitItem()
                val effect = effects.awaitItem()

                // THEN
                effect shouldBeEqualTo
                    TimerEffect.ShowMessage(
                        UiText.StringResource(R.string.error_generic),
                    )
                repository.updateSessionState(PomodoroSessionState(status = PomodoroStatus.IDLE))
                state.awaitItem()
                effects.cancelAndIgnoreRemainingEvents()
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN request exact alarm permission action WHEN triggered THEN permission effect emitted`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                val effects = viewModel.effects.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()

                // WHEN
                viewModel.onAction(TimerAction.RequestExactAlarmPermission)
                val effect = effects.awaitItem()

                // THEN
                effect shouldBeEqualTo TimerEffect.RequestExactAlarmPermission
                effects.cancelAndIgnoreRemainingEvents()
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN exact alarm permission updates WHEN denied then granted THEN warning follows state`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()

                // WHEN
                viewModel.onAction(TimerAction.UpdateExactAlarmPermission(isGranted = false))
                val deniedState = state.awaitItem()

                // THEN
                deniedState.isExactAlarmPermissionMissing shouldBeEqualTo true

                // WHEN
                viewModel.onAction(TimerAction.UpdateExactAlarmPermission(isGranted = true))
                val grantedState = state.awaitItem()

                // THEN
                grantedState.isExactAlarmPermissionMissing shouldBeEqualTo false
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN config actions WHEN updating THEN ui state should reflect latest values`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                // GIVEN
                state.awaitInitialSyncedState()

                // WHEN
                viewModel.onAction(TimerAction.SelectWorkDuration(30))
                viewModel.onAction(TimerAction.SelectShortBreakDuration(10))
                viewModel.onAction(TimerAction.ToggleAutoStartBreaks(true))
                viewModel.onAction(TimerAction.ToggleAutoStartWork(true))
                state.skipItems(3)
                val updatedState = state.awaitItem()

                // THEN
                updatedState.selectedWorkMinutes shouldBeEqualTo 30
                updatedState.selectedShortBreakMinutes shouldBeEqualTo 10
                updatedState.autoStartBreaks shouldBeEqualTo true
                updatedState.autoStartWork shouldBeEqualTo true
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    private suspend fun ReceiveTurbine<TimerState>.awaitInitialSyncedState() {
        val initialState = awaitItem()
        if (initialState.isLoading) {
            awaitItem()
        }
    }
}

private class FakeNotificationScheduler : NotificationScheduler {
    var foregroundStartCalls: Int = 0
    var foregroundStopCalls: Int = 0
    var scheduleError: DataError.Local? = null
    var lastForegroundNotification: RunningTimerNotificationData? = null

    override suspend fun scheduleNotification(notification: NotificationData): EmptyResult<DataError.Local> {
        val error = scheduleError
        return if (error != null) {
            Result.Failure(error)
        } else {
            Result.Success(Unit)
        }
    }

    override suspend fun cancelNotification(notificationId: Int): EmptyResult<DataError.Local> = Result.Success(Unit)

    override suspend fun cancelAllNotifications(): EmptyResult<DataError.Local> = Result.Success(Unit)

    override fun isNotificationScheduled(notificationId: Int): Boolean = false

    override suspend fun showPersistentNotification(notification: NotificationData): EmptyResult<DataError.Local> =
        Result.Success(Unit)

    override suspend fun dismissPersistentNotification(notificationId: Int): EmptyResult<DataError.Local> =
        Result.Success(Unit)

    override suspend fun startRunningForegroundTimer(
        notification: RunningTimerNotificationData,
    ): EmptyResult<DataError.Local> {
        foregroundStartCalls += 1
        lastForegroundNotification = notification
        return Result.Success(Unit)
    }

    override suspend fun stopRunningForegroundTimer(): EmptyResult<DataError.Local> {
        foregroundStopCalls += 1
        return Result.Success(Unit)
    }
}

private class FakeUsageStatsRepository : UsageStatsRepository {
    val recordedEvents = mutableListOf<UsageStatsEvent>()
    private val eventsCountFlow = MutableStateFlow(0L)

    override suspend fun recordEvent(event: UsageStatsEvent): EmptyResult<DataError.Local> {
        recordedEvents += event
        eventsCountFlow.value = recordedEvents.size.toLong()
        return Result.Success(Unit)
    }

    override suspend fun getSummary(
        periodStartMillis: Long,
        periodEndMillis: Long,
    ): Result<UsageStatsSummary, DataError.Local> =
        Result.Success(
            UsageStatsSummary(
                period = UsageStatsPeriod.DAILY,
                periodStartMillis = periodStartMillis,
                periodEndMillis = periodEndMillis,
                totalWorkTimeMillis = 0L,
                totalShortBreakTimeMillis = 0L,
                totalLongBreakTimeMillis = 0L,
                workSessionsCompleted = 0,
                shortBreaksCompleted = 0,
                longBreaksCompleted = 0,
                completedCycles = 0,
                skippedPhases = 0,
                stoppedSessions = 0,
                resetCount = 0,
                pauseCount = 0,
            ),
        )

    override fun observeEventsCount(): Flow<Long> = eventsCountFlow.asStateFlow()
}
