package com.jujodevs.pomodoro.convention.extensions

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(
    applicationExtension: ApplicationExtension
) {
    applicationExtension.apply {
        compileSdk = libs.version("compileSdk").toInt()

        defaultConfig {
            minSdk = libs.version("minSdk").toInt()
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    configureKotlinCompilerOptions()
}

internal fun Project.configureKotlinAndroid(
    libraryExtension: LibraryExtension
) {
    libraryExtension.apply {
        compileSdk = libs.version("compileSdk").toInt()

        defaultConfig {
            minSdk = libs.version("minSdk").toInt()
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    configureKotlinCompilerOptions()
}

private fun Project.configureKotlinCompilerOptions() {
    extensions.configure<KotlinAndroidProjectExtension>("kotlin") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}
