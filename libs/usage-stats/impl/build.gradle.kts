plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.testing)
    alias(libs.plugins.pomodoro.koin)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.jujodevs.pomodoro.libs.usagestats.impl"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.generateKotlin", "true")
    arg("room.incremental", "true")
}

dependencies {
    implementation(project(":libs:usage-stats:api"))
    implementation(project(":libs:analytics:api"))
    implementation(project(":libs:datastore:api"))
    implementation(project(":libs:logger:api"))

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
