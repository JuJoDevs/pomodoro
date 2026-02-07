package com.jujodevs.pomodoro.convention

import com.jujodevs.pomodoro.convention.extensions.libs
import com.jujodevs.pomodoro.convention.extensions.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                // Core Testing
                add("testImplementation", project(":core:testing"))

                // JUnit 5 BOM for version alignment
                add("testImplementation", platform(libs.library("junit5-bom")))
                add("testImplementation", libs.library("junit5-api"))
                add("testImplementation", libs.library("junit5-params"))
                add("testRuntimeOnly", libs.library("junit5-engine"))
                add("testRuntimeOnly", libs.library("junit5-launcher"))

                // MockK
                add("testImplementation", libs.library("mockk"))

                // Kluent assertions
                add("testImplementation", libs.library("kluent"))

                // Turbine for Flow testing
                add("testImplementation", libs.library("turbine"))

                // Coroutines test
                add("testImplementation", libs.library("kotlinx-coroutines-test"))
            }

            // Configure JUnit Platform for all test tasks
            // For Android modules, tasks are created after evaluation
            afterEvaluate {
                tasks.withType<Test> {
                    useJUnitPlatform()
                }
            }
        }
    }
}
