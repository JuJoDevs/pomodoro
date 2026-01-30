plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.koin)
}

android {
    namespace = "com.jujodevs.pomodoro.core.appconfig.impl"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:appconfig:api"))
}
