plugins {
    alias(libs.plugins.pomodoro.kotlin.library)
}

dependencies {
    implementation(platform(libs.junit5.bom))
    implementation(libs.junit)
    implementation(libs.junit5.api)
    implementation(libs.mockk)
    implementation(libs.kotlinx.coroutines.test)
}
