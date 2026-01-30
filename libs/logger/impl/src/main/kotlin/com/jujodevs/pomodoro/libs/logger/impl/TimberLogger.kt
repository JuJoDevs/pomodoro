package com.jujodevs.pomodoro.libs.logger.impl

import com.jujodevs.pomodoro.libs.logger.Logger
import timber.log.Timber

/**
 * Timber-based implementation of [Logger].
 *
 * Only logs in debug builds. In release builds, all log calls are no-ops.
 */
internal class TimberLogger : Logger {

    override fun d(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Timber.tag(tag).d(throwable, message)
        } else {
            Timber.tag(tag).d(message)
        }
    }

    override fun i(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Timber.tag(tag).i(throwable, message)
        } else {
            Timber.tag(tag).i(message)
        }
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Timber.tag(tag).w(throwable, message)
        } else {
            Timber.tag(tag).w(message)
        }
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }
}
