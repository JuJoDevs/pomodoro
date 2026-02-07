package com.jujodevs.pomodoro.features.timer.presentation

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.turbineScope
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.core.testing.extenion.CoroutineTestExtension
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

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        timeProvider = FakeTimeProvider()
        notificationScheduler = FakeNotificationScheduler()
        viewModel = TimerViewModel(
            useCases = TimerUseCases(
                observeSessionState = ObservePomodoroSessionStateUseCase(repository),
                startOrResume = StartOrResumePomodoroUseCase(repository, timeProvider),
                pause = PausePomodoroUseCase(repository, timeProvider),
                skip = SkipPomodoroPhaseUseCase(repository),
                stop = StopPomodoroUseCase(repository),
                reset = ResetPomodoroUseCase(repository),
                advancePhase = AdvancePomodoroPhaseUseCase(repository, timeProvider),
                updateConfig = UpdatePomodoroConfigUseCase(repository)
            ),
            notificationScheduler = notificationScheduler,
            timeProvider = timeProvider,
            stateSyncDebounceMs = 0L
        )
    }

    @Test
    fun `GIVEN running state without end timestamp WHEN syncing flow THEN phase should not advance`() = runTest {
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
                    lastKnownEndTimestamp = null
                )
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
    fun `GIVEN running timer WHEN syncing flow THEN foreground should start once with expected payload`() = runTest {
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
                    lastKnownEndTimestamp = timeProvider.currentTime + 10_000L
                )
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
    fun `GIVEN running state WHEN stop action is triggered THEN stop confirmation should be shown`() = runTest {
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
    fun `GIVEN stop confirmation visible WHEN user confirms THEN timer should stop`() = runTest {
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
    fun `GIVEN completed sessions WHEN reset action is triggered THEN reset confirmation should be shown`() = runTest {
        turbineScope {
            val state = viewModel.state.testIn(this)

            // GIVEN
            state.awaitInitialSyncedState()
            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.IDLE,
                    currentPhase = PomodoroPhase.SHORT_BREAK,
                    completedWorkSessions = 2
                )
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
    fun `GIVEN reset confirmation visible WHEN user confirms THEN progress should be reset`() = runTest {
        turbineScope {
            val state = viewModel.state.testIn(this)

            // GIVEN
            state.awaitInitialSyncedState()
            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.IDLE,
                    currentPhase = PomodoroPhase.SHORT_BREAK,
                    selectedWorkMinutes = 30,
                    completedWorkSessions = 3
                )
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
    fun `GIVEN exact alarm scheduling fails WHEN warning is dismissed THEN warning state should reset`() = runTest {
        turbineScope {
            val state = viewModel.state.testIn(this)

            // GIVEN
            state.awaitInitialSyncedState()
            notificationScheduler.scheduleShouldFail = true

            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.RUNNING,
                    phaseToken = "token-1",
                    lastKnownEndTimestamp = timeProvider.currentTime + 5_000L
                )
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
    fun `GIVEN dismissed warning WHEN scheduling fails again THEN warning stays hidden during snooze`() = runTest {
        turbineScope {
            val state = viewModel.state.testIn(this)

            // GIVEN
            state.awaitInitialSyncedState()
            notificationScheduler.scheduleShouldFail = true
            viewModel.onAction(TimerAction.UpdateExactAlarmPermission(isGranted = false))
            state.awaitItem()

            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.RUNNING,
                    phaseToken = "token-1",
                    lastKnownEndTimestamp = timeProvider.currentTime + 5_000L
                )
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
                    exactAlarmWarningSnoozedUntilMillis = timeProvider.currentTime + 1_000L
                )
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
    fun `GIVEN request exact alarm permission action WHEN triggered THEN permission effect emitted`() = runTest {
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
    fun `GIVEN exact alarm permission updates WHEN denied then granted THEN warning follows state`() = runTest {
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
    fun `GIVEN config actions WHEN updating THEN ui state should reflect latest values`() = runTest {
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
    var scheduleShouldFail: Boolean = false
    var lastForegroundNotification: RunningTimerNotificationData? = null

    override suspend fun scheduleNotification(notification: NotificationData): Result<Unit> {
        return if (scheduleShouldFail) {
            Result.failure(IllegalStateException("Exact alarm permission not granted"))
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun cancelNotification(notificationId: Int): Result<Unit> = Result.success(Unit)

    override suspend fun cancelAllNotifications(): Result<Unit> = Result.success(Unit)

    override fun isNotificationScheduled(notificationId: Int): Boolean = false

    override suspend fun showPersistentNotification(notification: NotificationData): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun dismissPersistentNotification(notificationId: Int): Result<Unit> = Result.success(Unit)

    override suspend fun startRunningForegroundTimer(
        notification: RunningTimerNotificationData
    ): Result<Unit> {
        foregroundStartCalls += 1
        lastForegroundNotification = notification
        return Result.success(Unit)
    }

    override suspend fun stopRunningForegroundTimer(): Result<Unit> {
        foregroundStopCalls += 1
        return Result.success(Unit)
    }
}
