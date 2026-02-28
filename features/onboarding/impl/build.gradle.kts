plugins {
    alias(libs.plugins.pomodoro.android.feature)
    alias(libs.plugins.pomodoro.android.testing)
    alias(libs.plugins.pomodoro.roborazzi)
}

android {
    namespace = "com.jujodevs.pomodoro.features.onboarding.impl"
}

dependencies {
    implementation(project(":features:onboarding:api"))

    implementation(project(":core:design-system"))
    implementation(project(":core:resources"))
    implementation(project(":core:ui"))
    implementation(project(":libs:analytics:api"))
    implementation(project(":libs:datastore:api"))
    implementation(project(":libs:logger:api"))
    implementation(project(":libs:permissions:api"))
}
