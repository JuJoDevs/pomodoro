plugins {
    alias(libs.plugins.pomodoro.kotlin.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.pomodoro.testing)
}

dependencies {
    api(libs.navigation3.runtime)
    api(libs.kotlinx.serialization.json)
}
