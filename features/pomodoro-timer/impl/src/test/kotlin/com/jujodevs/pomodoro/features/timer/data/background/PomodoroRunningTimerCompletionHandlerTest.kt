package com.jujodevs.pomodoro.features.timer.data.background

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.usecase.ReconcileExpiredPomodoroPhasesUseCase
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

class PomodoroRunningTimerCompletionHandlerTest {
    private lateinit var repository: FakePomodoroRepository
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var notificationScheduler: BackgroundNotificationSchedulerFake
    private lateinit var usageStatsRepository: BackgroundUsageStatsRepositoryFake
    private lateinit var handler: PomodoroRunningTimerCompletionHandler

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        timeProvider = FakeTimeProvider()
        notificationScheduler = BackgroundNotificationSchedulerFake()
        usageStatsRepository = BackgroundUsageStatsRepositoryFake()

        handler =
            PomodoroRunningTimerCompletionHandler(
                repository = repository,
                reconcileExpiredPhases = ReconcileExpiredPomodoroPhasesUseCase(repository, timeProvider),
                notificationScheduler = notificationScheduler,
                recordUsageStatsEvent = RecordUsageStatsEventUseCase(usageStatsRepository),
                timeProvider = timeProvider,
            )
    }

    @Test
    fun `GIVEN expired phase and token WHEN handler runs THEN should advance and schedule next`() =
        runTest {
            // GIVEN
            repository.updateSessionState(
                PomodoroSessionState(
                    selectedWorkMinutes = 1,
                    selectedShortBreakMinutes = 1,
                    autoStartBreaks = true,
                    autoStartWork = true,
                    currentPhase = PomodoroPhase.WORK,
                    status = PomodoroStatus.RUNNING,
                    remainingMillis = 60_000L,
                    completedWorkSessions = 0,
                    phaseToken = "token-1",
                    lastKnownEndTimestamp = timeProvider.currentTime - 1_000L,
                ),
            )

            // WHEN
            handler.onRunningTimerCompleted("token-1")

            // THEN
            val updatedState = repository.getSessionState().first()
            updatedState.currentPhase shouldBeEqualTo PomodoroPhase.SHORT_BREAK
            updatedState.status shouldBeEqualTo PomodoroStatus.RUNNING
            updatedState.completedWorkSessions shouldBeEqualTo 1

            notificationScheduler.scheduledNotifications.size shouldBeEqualTo 1
            notificationScheduler.startedForegroundNotifications.size shouldBeEqualTo 1
            notificationScheduler
                .scheduledNotifications
                .first()
                .token
                .isBlank() shouldBeEqualTo false

            usageStatsRepository.recordedEvents.any {
                it.type == UsageStatsEventType.PHASE_COMPLETED && it.phase?.name == "WORK"
            } shouldBeEqualTo true
            usageStatsRepository.recordedEvents.any {
                it.type == UsageStatsEventType.PHASE_TIME_RECORDED &&
                    it.phase?.name == "WORK" &&
                    it.durationMillis == 60_000L
            } shouldBeEqualTo true
        }

    @Test
    fun `GIVEN expired running phase with different token WHEN completion handler runs THEN should do nothing`() =
        runTest {
            // GIVEN
            val initialState =
                PomodoroSessionState(
                    selectedWorkMinutes = 1,
                    selectedShortBreakMinutes = 1,
                    autoStartBreaks = true,
                    autoStartWork = true,
                    currentPhase = PomodoroPhase.WORK,
                    status = PomodoroStatus.RUNNING,
                    remainingMillis = 60_000L,
                    completedWorkSessions = 0,
                    phaseToken = "token-active",
                    lastKnownEndTimestamp = timeProvider.currentTime - 1_000L,
                )
            repository.updateSessionState(initialState)

            // WHEN
            handler.onRunningTimerCompleted("token-other")

            // THEN
            repository.getSessionState().first() shouldBeEqualTo initialState
            notificationScheduler.scheduledNotifications.size shouldBeEqualTo 0
            notificationScheduler.startedForegroundNotifications.size shouldBeEqualTo 0
            usageStatsRepository.recordedEvents.size shouldBeEqualTo 0
        }
}

private class BackgroundNotificationSchedulerFake : NotificationScheduler {
    val scheduledNotifications = mutableListOf<NotificationData>()
    val startedForegroundNotifications = mutableListOf<RunningTimerNotificationData>()

    override suspend fun scheduleNotification(notification: NotificationData): EmptyResult<DataError.Local> {
        scheduledNotifications += notification
        return Result.Success(Unit)
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
        startedForegroundNotifications += notification
        return Result.Success(Unit)
    }

    override suspend fun stopRunningForegroundTimer(): EmptyResult<DataError.Local> = Result.Success(Unit)
}

private class BackgroundUsageStatsRepositoryFake : UsageStatsRepository {
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
