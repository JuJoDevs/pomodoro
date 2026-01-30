package com.jujodevs.pomodoro.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test

class NavigationExtensionsTest {
    @Test
    fun `GIVEN back stack with one item WHEN goBack THEN back stack unchanged`() {
        // GIVEN
        val backStack = NavBackStack(TestNavKey.Home)

        // WHEN
        backStack.goBack()

        // THEN
        backStack.size shouldBe 1
        backStack.last() shouldBe TestNavKey.Home
    }

    @Test
    fun `GIVEN back stack with multiple items WHEN goBack THEN removes last item`() {
        // GIVEN
        val backStack = NavBackStack<NavKey>(TestNavKey.Home)
        backStack.add(TestNavKey.Settings)

        // WHEN
        backStack.goBack()

        // THEN
        backStack.size shouldBe 1
        backStack.last() shouldBe TestNavKey.Home
    }

    @Test
    fun `GIVEN back stack WHEN navigateTo THEN adds new key to stack`() {
        // GIVEN
        val backStack = NavBackStack<NavKey>(TestNavKey.Home)

        // WHEN
        backStack.navigateTo(TestNavKey.Settings)

        // THEN
        backStack.size shouldBe 2
        backStack.last() shouldBe TestNavKey.Settings
    }

    @Test
    fun `GIVEN back stack WHEN popUpTo with inclusive false THEN removes up to but not including target`() {
        // GIVEN
        val backStack = NavBackStack<NavKey>(TestNavKey.Home)
        backStack.add(TestNavKey.Settings)
        backStack.add(TestNavKey.Profile)
        backStack.add(TestNavKey.Details)

        // WHEN
        backStack.popUpTo(TestNavKey.Settings, inclusive = false)

        // THEN
        backStack.size shouldBe 2
        backStack.last() shouldBe TestNavKey.Settings
    }

    @Test
    fun `GIVEN back stack WHEN popUpTo with inclusive true THEN removes up to and including target`() {
        // GIVEN
        val backStack = NavBackStack<NavKey>(TestNavKey.Home)
        backStack.add(TestNavKey.Settings)
        backStack.add(TestNavKey.Profile)
        backStack.add(TestNavKey.Details)

        // WHEN
        backStack.popUpTo(TestNavKey.Settings, inclusive = true)

        // THEN
        backStack.size shouldBe 1
        backStack.last() shouldBe TestNavKey.Home
    }

    @Test
    fun `GIVEN back stack WHEN popUpTo with key not in stack THEN stack unchanged`() {
        // GIVEN
        val backStack = NavBackStack<NavKey>(TestNavKey.Home)
        backStack.add(TestNavKey.Settings)

        // WHEN
        backStack.popUpTo(TestNavKey.Profile, inclusive = false)

        // THEN
        backStack.size shouldBe 2
        backStack.last() shouldBe TestNavKey.Settings
    }

    @Test
    fun `GIVEN back stack with items WHEN replaceWith THEN replaces last item`() {
        // GIVEN
        val backStack = NavBackStack<NavKey>(TestNavKey.Home)
        backStack.add(TestNavKey.Settings)

        // WHEN
        backStack.replaceWith(TestNavKey.Profile)

        // THEN
        backStack.size shouldBe 2
        backStack.last() shouldBe TestNavKey.Profile
        backStack[0] shouldBe TestNavKey.Home
    }

    @Test
    fun `GIVEN empty back stack WHEN replaceWith THEN adds new key`() {
        // GIVEN
        val backStack = NavBackStack<NavKey>(TestNavKey.Home)
        backStack.clear()

        // WHEN
        backStack.replaceWith(TestNavKey.Settings)

        // THEN
        backStack.size shouldBe 1
        backStack.last() shouldBe TestNavKey.Settings
    }

    @Test
    fun `GIVEN back stack WHEN navigateToRoot THEN clears stack and adds new root`() {
        // GIVEN
        val backStack = NavBackStack<NavKey>(TestNavKey.Home)
        backStack.add(TestNavKey.Settings)
        backStack.add(TestNavKey.Profile)
        backStack.add(TestNavKey.Details)

        // WHEN
        backStack.navigateToRoot(TestNavKey.Settings)

        // THEN
        backStack.size shouldBe 1
        backStack.last() shouldBe TestNavKey.Settings
    }

    @Test
    fun `GIVEN back stack WHEN popUpTo first item with inclusive false THEN removes all except first`() {
        // GIVEN
        val backStack = NavBackStack<NavKey>(TestNavKey.Home)
        backStack.add(TestNavKey.Settings)
        backStack.add(TestNavKey.Profile)

        // WHEN
        backStack.popUpTo(backStack.first(), inclusive = false)

        // THEN
        backStack.size shouldBe 1
        backStack.last() shouldBe TestNavKey.Home
    }
}

/**
 * Test navigation keys for testing purposes.
 */
@Serializable
sealed interface TestNavKey : AppNavKey {
    @Serializable
    data object Home : TestNavKey

    @Serializable
    data object Settings : TestNavKey

    @Serializable
    data object Profile : TestNavKey

    @Serializable
    data object Details : TestNavKey
}
