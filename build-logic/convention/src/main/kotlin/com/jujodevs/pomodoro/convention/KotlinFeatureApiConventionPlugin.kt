package com.jujodevs.pomodoro.convention

import com.jujodevs.pomodoro.convention.extensions.libs
import com.jujodevs.pomodoro.convention.extensions.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KotlinFeatureApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // API modules are pure Kotlin - no Android dependencies
                apply("pomodoro.kotlin.library")
            }

            dependencies {
                // Only essential dependencies for contracts
                add("implementation", project(":core:domain"))
                add("implementation", libs.library("kotlinx-coroutines-core"))
            }
        }
    }
}
