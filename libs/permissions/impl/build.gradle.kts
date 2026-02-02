plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.koin)
}

android {
    namespace = "com.jujodevs.pomodoro.libs.permissions.impl"
}

dependencies {
    implementation(project(":libs:permissions:api"))
    implementation(libs.androidx.core.ktx)
}
