package com.jujodevs.pomodoro.convention

import com.jujodevs.pomodoro.convention.extensions.library
import com.jujodevs.pomodoro.convention.extensions.libs
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import java.io.File

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.gitlab.arturbosch.detekt")
            }

            // Configure detekt extension
            extensions.configure<DetektExtension>("detekt") {
                config.setFrom(File(rootProject.rootDir, "detekt.yml"))
                parallel = true
                autoCorrect = true
                buildUponDefaultConfig = true
                allRules = false
                // Exclude build directories and generated files
                baseline = File(rootProject.rootDir, "detekt-baseline.xml").takeIf { it.exists() }
            }

            // Configure detekt tasks
            tasks.withType<Detekt>().configureEach {
                jvmTarget = "1.8"
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    txt.required.set(false)
                    sarif.required.set(false)
                }
            }

            // Add detekt plugins
            dependencies {
                add("detektPlugins", libs.library("detekt-formatting"))
                add("detektPlugins", libs.library("detekt-compose-rules"))
            }
        }
    }
}
