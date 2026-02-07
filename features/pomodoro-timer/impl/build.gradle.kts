plugins {
    alias(libs.plugins.pomodoro.android.feature)
}

android {
    namespace = "com.jujodevs.pomodoro.features.timer.impl"
}

dependencies {
    implementation(project(":features:pomodoro-timer:api"))
    implementation(project(":core:ui"))
    implementation(project(":core:design-system"))
    implementation(project(":core:navigation"))
}
