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
            tasks.withType<Test> {
                useJUnitPlatform()
            }

            dependencies {
                // JUnit 5
                add("testImplementation", libs.library("junit5-api"))
                add("testRuntimeOnly", libs.library("junit5-engine"))
                add("testImplementation", libs.library("junit5-params"))
                
                // MockK
                add("testImplementation", libs.library("mockk"))
                
                // Kluent assertions
                add("testImplementation", libs.library("kluent"))
                
                // Turbine for Flow testing
                add("testImplementation", libs.library("turbine"))
                
                // Coroutines test
                add("testImplementation", libs.library("kotlinx-coroutines-test"))
            }
        }
    }
}
