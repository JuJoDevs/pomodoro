package com.jujodevs.pomodoro.convention

import com.android.build.api.dsl.ApplicationExtension
import com.jujodevs.pomodoro.convention.extensions.configureKotlinAndroid
import com.jujodevs.pomodoro.convention.extensions.libs
import com.jujodevs.pomodoro.convention.extensions.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            val extension = extensions.getByType<ApplicationExtension>()
            configureKotlinAndroid(extension)

            extension.apply {
                defaultConfig {
                    targetSdk = libs.version("targetSdk").toInt()
                    versionCode = libs.version("versionCode").toInt()
                    versionName = libs.version("versionName")

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                        ndk {
                            debugSymbolLevel = "SYMBOL_TABLE"
                        }
                    }
                    debug {
                        isMinifyEnabled = false
                        applicationIdSuffix = ".debug"
                        versionNameSuffix = "-debug"
                    }
                }
            }
        }
    }
}
