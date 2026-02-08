package com.jujodevs.pomodoro.convention

import com.jujodevs.pomodoro.convention.extensions.libs
import com.jujodevs.pomodoro.convention.extensions.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("pomodoro.android.library")
                apply("pomodoro.android.compose")
                apply("pomodoro.koin")
                apply("pomodoro.testing")
            }

            dependencies {
                // Core dependencies for features
                // Note: These project dependencies will be added per module as needed
                 add("implementation", project(":core:domain"))
                 add("implementation", project(":core:ui"))
                 add("implementation", project(":core:design-system"))
                 add("implementation", project(":core:navigation"))
                 add("implementation", project(":core:resources"))

                // Lifecycle
                add("implementation", libs.library("lifecycle-runtime-ktx"))
                add("implementation", libs.library("lifecycle-viewmodel-compose"))
                add("implementation", libs.library("lifecycle-runtime-compose"))

                // Coroutines
                add("implementation", libs.library("kotlinx-coroutines-android"))

                // Timber for logging
                add("implementation", libs.library("timber"))
            }
        }
    }
}
