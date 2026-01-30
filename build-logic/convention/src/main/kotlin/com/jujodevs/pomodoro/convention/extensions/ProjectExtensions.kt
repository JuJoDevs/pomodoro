package com.jujodevs.pomodoro.convention.extensions

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal fun Project.configureKotlin(
    extension: KotlinProjectExtension
) {
    extension.apply {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}

internal fun Project.configureJava() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

internal fun Project.isFeatureModule(): Boolean =
    path.startsWith(":features:")

internal fun Project.isLibModule(): Boolean =
    path.startsWith(":libs:")

internal fun Project.isCoreModule(): Boolean =
    path.startsWith(":core:")
