package com.jujodevs.pomodoro.core.domain.coroutines

import kotlinx.coroutines.CoroutineDispatcher

/**
 * App-wide coroutine dispatchers abstraction for testability.
 */
interface AppDispatchers {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}
