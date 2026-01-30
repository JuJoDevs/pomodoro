plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.android.compose)
    alias(libs.plugins.pomodoro.android.testing)
    alias(libs.plugins.pomodoro.roborazzi)
}

android {
    namespace = "com.jujodevs.pomodoro.core.designsystem"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)

    // Material Icons Extended
    implementation(libs.compose.material.icons.extended)

    // Debug tools
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
