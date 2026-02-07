// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

subprojects {
    afterEvaluate {
        // Only apply plugins to actual modules, not container directories
        // A container directory has subprojects, a real module doesn't
        val isContainer = project.subprojects.isNotEmpty()
        if (!isContainer) {
            plugins.apply("pomodoro.detekt")
            plugins.apply("pomodoro.ktlint")
        }
    }
}
