plugins {
    alias(libs.plugins.pomodoro.android.feature)
    alias(libs.plugins.pomodoro.android.testing)
    alias(libs.plugins.pomodoro.roborazzi)
}

android {
    namespace = "com.jujodevs.pomodoro.features.timer.impl"
}

dependencies {
    implementation(project(":features:pomodoro-timer:api"))

    implementation(project(":libs:datastore:api"))
    implementation(project(":libs:notifications:api"))
    implementation(project(":libs:logger:api"))
    implementation(project(":libs:usage-stats:api"))
}
