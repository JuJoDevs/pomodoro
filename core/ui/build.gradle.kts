plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.android.compose)
}

android {
    namespace = "com.jujodevs.pomodoro.core.ui"
}

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    // Core modules
    implementation(project(":core:design-system"))
}
