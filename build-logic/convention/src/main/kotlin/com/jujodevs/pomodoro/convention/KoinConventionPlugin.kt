package com.jujodevs.pomodoro.convention

import com.jujodevs.pomodoro.convention.extensions.libs
import com.jujodevs.pomodoro.convention.extensions.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.library("koin-core"))
                add("implementation", libs.library("koin-android"))
                add("implementation", libs.library("koin-compose"))
                add("testImplementation", libs.library("koin-test"))
                add("testImplementation", libs.library("koin-test-junit5"))
            }
        }
    }
}
