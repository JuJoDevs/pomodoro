package com.jujodevs.pomodoro.core.ui

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.resources.R

fun DataError.asUiText(): UiText {
    return when (this) {
        is DataError.Network -> when (this) {
            DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(R.string.error_network)
            DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(R.string.error_network)
            DataError.Network.NO_INTERNET -> UiText.StringResource(R.string.error_network)
            DataError.Network.SERVER_ERROR -> UiText.StringResource(R.string.error_generic)
            DataError.Network.SERIALIZATION -> UiText.StringResource(R.string.error_generic)
            DataError.Network.UNKNOWN -> UiText.StringResource(R.string.error_generic)
        }

        is DataError.Local -> when (this) {
            DataError.Local.INSUFFICIENT_PERMISSIONS -> UiText.StringResource(R.string.error_permission_denied)
            DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_generic)
            DataError.Local.NOT_FOUND -> UiText.StringResource(R.string.error_generic)
            DataError.Local.UNKNOWN -> UiText.StringResource(R.string.error_generic)
        }
    }
}
