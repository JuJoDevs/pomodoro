plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.koin)
    alias(libs.plugins.pomodoro.testing)
}

android {
    namespace = "com.jujodevs.pomodoro.libs.analytics.impl"
}

dependencies {
    implementation(project(":libs:analytics:api"))
    implementation(project(":libs:logger:api"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
}
