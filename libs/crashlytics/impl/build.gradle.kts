plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.koin)
    alias(libs.plugins.pomodoro.testing)
}

android {
    namespace = "com.jujodevs.pomodoro.libs.crashlytics.impl"
}

dependencies {
    implementation(project(":libs:crashlytics:api"))
    implementation(project(":libs:logger:api"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
}
