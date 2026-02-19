plugins {
    alias(libs.plugins.pomodoro.android.application)
    alias(libs.plugins.pomodoro.android.compose)
    alias(libs.plugins.pomodoro.koin)
    alias(libs.plugins.kotlin.serialization)
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
    implementation(libs.androidx.core.splashscreen)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)

    // Core
    implementation(project(":core:appconfig:api"))
    implementation(project(":core:appconfig:impl"))
    implementation(project(":core:design-system"))
    implementation(project(":core:navigation"))
    implementation(project(":core:resources"))
    implementation(project(":core:ui"))

    // Navigation3
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.kotlinx.serialization.json)

    // Logger
    implementation(project(":libs:logger:api"))
    implementation(project(":libs:logger:impl"))

    // Analytics
    implementation(project(":libs:analytics:api"))
    implementation(project(":libs:analytics:impl"))

    // Crashlytics
    implementation(project(":libs:crashlytics:api"))
    implementation(project(":libs:crashlytics:impl"))

    // DataStore
    implementation(project(":libs:datastore:api"))
    implementation(project(":libs:datastore:impl"))

    // Notifications
    implementation(project(":libs:notifications:api"))
    implementation(project(":libs:notifications:impl"))

    // Permissions
    implementation(project(":libs:permissions:api"))
    implementation(project(":libs:permissions:impl"))

    // Usage stats
    implementation(project(":libs:usage-stats:api"))
    implementation(project(":libs:usage-stats:impl"))

    // Features
    implementation(project(":features:pomodoro-timer:impl"))
    implementation(project(":features:settings:impl"))
}
