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
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
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
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

    configureKotlinCompilerOptions()
}

private fun Project.configureKotlinCompilerOptions() {
    extensions.configure<KotlinAndroidProjectExtension>("kotlin") {
        compilerOptions {
            // Use JVM 21 to match Java compileOptions target
            // Android supports Java 17+ bytecode, so JVM 21 is compatible
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}
