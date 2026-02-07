package com.jujodevs.pomodoro.core.testing

/**
 * Check a callback with x boolean parameter for both true and false.
 * Ensure that the mocks are cleared after each loop using [io.mockk.clearMocks] or equivalent.
 */
private fun forEachBooleanCombination(
    size: Int,
    block: (List<Boolean>) -> Unit,
) {
    val total = 1 shl size

    repeat(total) { mask ->
        val values =
            List(size) { index ->
                mask and (1 shl index) != 0
            }
        block(values)
    }
}

fun checkBoolean(block: (Boolean) -> Unit) =
    forEachBooleanCombination(ONE_BOOLEAN) { (a) ->
        block(a)
    }

fun check2Booleans(block: (Boolean, Boolean) -> Unit) =
    forEachBooleanCombination(TWO_BOOLEANS) { (a, b) ->
        block(a, b)
    }

fun check3Booleans(block: (Boolean, Boolean, Boolean) -> Unit) =
    forEachBooleanCombination(THREE_BOOLEANS) { (a, b, c) ->
        block(a, b, c)
    }

private const val ONE_BOOLEAN = 1
private const val TWO_BOOLEANS = 2
private const val THREE_BOOLEANS = 3
