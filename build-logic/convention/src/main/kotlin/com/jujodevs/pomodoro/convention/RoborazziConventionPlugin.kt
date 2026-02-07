package com.jujodevs.pomodoro.convention

import com.android.build.api.dsl.LibraryExtension
import com.jujodevs.pomodoro.convention.extensions.libs
import com.jujodevs.pomodoro.convention.extensions.library
import io.github.takahirom.roborazzi.RoborazziExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class RoborazziConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.github.takahirom.roborazzi")
            }

            plugins.withId("com.android.library") {
                extensions.configure<LibraryExtension> {
                    testOptions {
                        unitTests {
                            isIncludeAndroidResources = true
                        }
                    }
                }
            }

            afterEvaluate {
                extensions.configure<RoborazziExtension> {
                    outputDir.set(layout.projectDirectory.dir("src/test/snapshots"))
                }
            }

            dependencies {
                add("testImplementation", libs.library("roborazzi"))
                add("testImplementation", libs.library("roborazzi-compose"))
                add("testImplementation", libs.library("roborazzi-rule"))
                add("testImplementation", libs.library("robolectric"))
            }
        }
    }
}
