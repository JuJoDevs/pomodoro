plugins {
    alias(libs.plugins.pomodoro.android.feature)
    alias(libs.plugins.pomodoro.android.testing)
    alias(libs.plugins.pomodoro.roborazzi)
}

android {
    namespace = "com.jujodevs.pomodoro.features.settings.impl"
}

dependencies {
    implementation(project(":features:settings:api"))

    implementation(project(":core:design-system"))
    implementation(project(":core:resources"))
    implementation(project(":core:ui"))
    implementation(project(":libs:notifications:api"))
    implementation(project(":libs:permissions:api"))
    implementation(project(":libs:logger:api"))
}
