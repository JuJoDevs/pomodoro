plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.testing)
    alias(libs.plugins.pomodoro.koin)
}

android {
    namespace = "com.jujodevs.pomodoro.libs.datastore.impl"
}

dependencies {
    implementation(project(":libs:datastore:api"))

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}
