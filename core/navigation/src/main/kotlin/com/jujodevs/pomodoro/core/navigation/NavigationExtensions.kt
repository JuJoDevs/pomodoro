package com.jujodevs.pomodoro.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Navigate to a destination by adding it to the back stack.
 *
 * @param key The navigation key to navigate to
 */
fun <T : NavKey> NavBackStack<T>.navigateTo(key: T) {
    add(key)
}

/**
 * Navigate back by removing the last entry from the back stack.
 * Does nothing if there's only one entry (prevents empty back stack).
 */
fun <T : NavKey> NavBackStack<T>.goBack() {
    if (size > 1) {
        removeAt(size - 1)
    }
}

/**
 * Pop back stack to a specific key.
 *
 * @param key The key to pop to
 * @param inclusive If true, also removes the target key
 */
fun <T : NavKey> NavBackStack<T>.popUpTo(
    key: T,
    inclusive: Boolean = false,
) {
    val index = indexOfLast { it == key }
    if (index != -1) {
        val removeCount = if (inclusive) size - index else size - index - 1
        repeat(removeCount) {
            if (size > 1) removeAt(size - 1)
        }
    }
}

/**
 * Replace the current destination with a new one.
 */
fun <T : NavKey> NavBackStack<T>.replaceWith(key: T) {
    if (isNotEmpty()) {
        removeAt(size - 1)
    }
    add(key)
}

/**
 * Clear the entire back stack and navigate to a new root.
 */
fun <T : NavKey> NavBackStack<T>.navigateToRoot(key: T) {
    clear()
    add(key)
}
