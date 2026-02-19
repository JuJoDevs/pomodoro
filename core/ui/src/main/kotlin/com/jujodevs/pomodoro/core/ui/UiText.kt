package com.jujodevs.pomodoro.core.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class DynamicString(
        val value: String,
    ) : UiText()

    data class StringResource(
        @param:StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : UiText()

    fun asString(context: Context): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, *args.toTypedArray())
        }

    @Composable
    fun asString(): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id = id, *args.toTypedArray())
        }
}
