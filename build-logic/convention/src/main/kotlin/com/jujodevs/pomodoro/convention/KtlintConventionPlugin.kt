package com.jujodevs.pomodoro.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jlleitschuh.gradle.ktlint")
            }

            // Configure ktlint extension
            extensions.configure<KtlintExtension>("ktlint") {
                // Set ktlint version
                version.set("1.5.0")
                
                // Enable Android mode
                android.set(true)
                
                // Enable verbose output
                verbose.set(true)
                
                // Output to console
                outputToConsole.set(true)
                
                // Output color
                outputColorName.set("AUTO")
                
                // Reporters
                reporters {
                    reporter(ReporterType.HTML)
                    reporter(ReporterType.CHECKSTYLE)
                }
                
                // Filter configuration
                filter {
                    exclude("**/generated/**")
                    exclude("**/build/**")
                }
            }
        }
    }
}
