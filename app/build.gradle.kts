plugins {
    alias(libs.plugins.pomodoro.android.application)
    alias(libs.plugins.pomodoro.android.compose)
}

android {
    namespace = "com.jujodevs.pomodoro"

    defaultConfig {
        applicationId = "com.jujodevs.pomodoro"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
}
