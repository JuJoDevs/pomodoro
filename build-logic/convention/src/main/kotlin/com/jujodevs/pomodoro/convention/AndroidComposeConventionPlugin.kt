package com.jujodevs.pomodoro.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.jujodevs.pomodoro.convention.extensions.libs
import com.jujodevs.pomodoro.convention.extensions.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            // Configure Compose for either Application or Library modules
            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    buildFeatures {
                        compose = true
                    }
                }
            }
            
            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    buildFeatures {
                        compose = true
                    }
                }
            }

            dependencies {
                val bom = libs.library("compose-bom")
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))
                
                add("implementation", libs.library("compose-ui"))
                add("implementation", libs.library("compose-ui-graphics"))
                add("implementation", libs.library("compose-ui-tooling-preview"))
                add("implementation", libs.library("compose-material3"))
                add("debugImplementation", libs.library("compose-ui-tooling"))
                add("debugImplementation", libs.library("compose-ui-test-manifest"))
            }
        }
    }
}
