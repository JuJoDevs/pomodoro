plugins {
    alias(libs.plugins.pomodoro.android.application)
    alias(libs.plugins.pomodoro.android.compose)
    alias(libs.plugins.pomodoro.koin)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.jujodevs.pomodoro"

    defaultConfig {
        applicationId = "com.jujodevs.pomodoro"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)

    // Core
    implementation(project(":core:appconfig:api"))
    implementation(project(":core:appconfig:impl"))

    // Logger
    implementation(project(":libs:logger:api"))
    implementation(project(":libs:logger:impl"))

    // Analytics
    implementation(project(":libs:analytics:api"))
    implementation(project(":libs:analytics:impl"))

    // Crashlytics
    implementation(project(":libs:crashlytics:api"))
    implementation(project(":libs:crashlytics:impl"))
}
