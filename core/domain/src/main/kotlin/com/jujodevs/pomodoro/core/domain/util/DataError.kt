package com.jujodevs.pomodoro.core.domain.util

sealed interface DataError : Error {
    enum class Network : DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
    }

    enum class Local : DataError {
        DISK_FULL,
        INSUFFICIENT_PERMISSIONS,
        NOT_FOUND,
        UNKNOWN,
    }
}
