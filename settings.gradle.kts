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
include(":core:design-system")
include(":core:navigation")
include(":core:resources")
include(":core:testing")
include(":core:ui")

// Logger modules
include(":libs:logger:api")
include(":libs:logger:impl")

// Analytics modules
include(":libs:analytics:api")
include(":libs:analytics:impl")

// Crashlytics modules
include(":libs:crashlytics:api")
include(":libs:crashlytics:impl")

// DataStore modules
include(":libs:datastore:api")
include(":libs:datastore:impl")

// Notifications modules
include(":libs:notifications:api")
include(":libs:notifications:impl")

// Permissions modules
include(":libs:permissions:api")
include(":libs:permissions:impl")

// Features modules
include(":features:pomodoro-timer:api")
include(":features:pomodoro-timer:impl")
