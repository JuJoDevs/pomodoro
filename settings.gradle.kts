@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Pomodoro"
include(":app")

// Core modules
include(":core:appconfig:api")
include(":core:appconfig:impl")

// Logger modules
include(":libs:logger:api")
include(":libs:logger:impl")

// Analytics modules
include(":libs:analytics:api")
include(":libs:analytics:impl")

// Crashlytics modules
include(":libs:crashlytics:api")
include(":libs:crashlytics:impl")
