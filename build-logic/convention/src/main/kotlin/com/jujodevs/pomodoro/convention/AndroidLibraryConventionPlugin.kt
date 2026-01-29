package com.jujodevs.pomodoro.convention

import com.android.build.api.dsl.LibraryExtension
import com.jujodevs.pomodoro.convention.extensions.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                // AGP 9.0 has built-in Kotlin support, no need to apply kotlin-android
            }

            val extension = extensions.getByType<LibraryExtension>()
            configureKotlinAndroid(extension)

            extension.apply {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }
        }
    }
}
