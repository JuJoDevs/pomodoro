plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.testing)
    alias(libs.plugins.pomodoro.koin)
}

android {
    namespace = "com.jujodevs.pomodoro.libs.notifications.impl"
}

dependencies {
    implementation(project(":libs:notifications:api"))
    implementation(project(":libs:logger:api"))
    implementation(project(":core:resources"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
}
