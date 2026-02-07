package com.jujodevs.pomodoro.convention

import com.android.build.api.dsl.LibraryExtension
import com.jujodevs.pomodoro.convention.extensions.libs
import com.jujodevs.pomodoro.convention.extensions.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Android Testing Convention Plugin
 *
 * Applies the base TestingConventionPlugin and adds Android-specific testing dependencies
 * including Compose UI testing, Roborazzi snapshot testing, and Robolectric.
 */
class AndroidTestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply base testing plugin first
            pluginManager.apply(TestingConventionPlugin::class.java)

            plugins.withId("com.android.library") {
                extensions.configure<LibraryExtension> {
                    testOptions {
                        unitTests {
                            isIncludeAndroidResources = true
                        }
                    }
                }
            }

            dependencies {
                // JUnit4 support (for Roborazzi snapshot tests)
                add("testImplementation", libs.library("junit"))
                add("testRuntimeOnly", libs.library("junit5-vintage-engine"))

                // Compose UI Testing
                add("testImplementation", libs.library("androidx-compose-ui-test-junit4"))

                // Roborazzi for snapshot testing
                add("testImplementation", libs.library("roborazzi"))
                add("testImplementation", libs.library("roborazzi-compose"))
                add("testImplementation", libs.library("roborazzi-rule"))

                // Robolectric for Android unit tests
                add("testImplementation", libs.library("robolectric"))
            }
        }
    }
}
