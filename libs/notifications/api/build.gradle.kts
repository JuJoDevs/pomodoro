plugins {
    alias(libs.plugins.pomodoro.kotlin.library)
}

dependencies {
    api(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.core)
}
