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
    implementation(libs.androidx.activity.compose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Core modules
    implementation(project(":core:design-system"))
    implementation(project(":libs:permissions:api"))
}
