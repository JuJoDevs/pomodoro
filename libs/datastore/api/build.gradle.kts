plugins {
    alias(libs.plugins.pomodoro.kotlin.library)
    alias(libs.plugins.pomodoro.testing)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
