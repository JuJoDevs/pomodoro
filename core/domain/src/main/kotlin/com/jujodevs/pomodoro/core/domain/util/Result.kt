package com.jujodevs.pomodoro.core.domain.util

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(
        val data: D,
    ) : Result<D, Nothing>

    data class Failure<out E : Error>(
        val error: E,
    ) : Result<Nothing, E>
}

typealias EmptyResult<E> = Result<Unit, E>

val <D, E : Error> Result<D, E>.isSuccess: Boolean
    get() = this is Result.Success

val <D, E : Error> Result<D, E>.isFailure: Boolean
    get() = this is Result.Failure

inline fun <D, E : Error> Result<D, E>.onSuccess(action: (D) -> Unit): Result<D, E> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <D, E : Error> Result<D, E>.onFailure(action: (E) -> Unit): Result<D, E> {
    if (this is Result.Failure) {
        action(error)
    }
    return this
}

inline fun <D, E : Error, R> Result<D, E>.map(mapper: (D) -> R): Result<R, E> =
    when (this) {
        is Result.Success -> Result.Success(mapper(data))
        is Result.Failure -> this
    }

inline fun <D, E : Error, R : Error> Result<D, E>.mapError(mapper: (E) -> R): Result<D, R> =
    when (this) {
        is Result.Success -> this
        is Result.Failure -> Result.Failure(mapper(error))
    }
