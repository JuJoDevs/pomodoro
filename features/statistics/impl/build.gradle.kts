plugins {
    alias(libs.plugins.pomodoro.android.feature)
    alias(libs.plugins.pomodoro.android.testing)
    alias(libs.plugins.pomodoro.roborazzi)
}

android {
    namespace = "com.jujodevs.pomodoro.features.statistics.impl"
}

dependencies {
    implementation(project(":features:statistics:api"))
    implementation(project(":libs:usage-stats:api"))
    implementation(project(":libs:usage-stats:impl"))
}
