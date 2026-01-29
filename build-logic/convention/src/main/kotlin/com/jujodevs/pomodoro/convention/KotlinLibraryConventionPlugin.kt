package com.jujodevs.pomodoro.convention

import com.jujodevs.pomodoro.convention.extensions.configureJava
import com.jujodevs.pomodoro.convention.extensions.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }

            configureJava()
            
            extensions.configure<KotlinProjectExtension> {
                configureKotlin(this)
            }
        }
    }
}
