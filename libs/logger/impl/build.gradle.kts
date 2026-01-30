plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.koin)
}

android {
    namespace = "com.jujodevs.pomodoro.libs.logger.impl"
}

dependencies {
    implementation(project(":libs:logger:api"))
    implementation(libs.timber)
}
