plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.android.testing)
    alias(libs.plugins.pomodoro.koin)
}

android {
    namespace = "com.jujodevs.pomodoro.libs.notifications.impl"
}

dependencies {
    implementation(project(":libs:notifications:api"))
    implementation(project(":libs:datastore:api"))
    implementation(project(":libs:logger:api"))
    implementation(project(":core:resources"))

    testImplementation(project(":libs:datastore:impl"))
    testImplementation(libs.androidx.datastore.preferences)
    testImplementation(libs.androidx.datastore.core)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
}
