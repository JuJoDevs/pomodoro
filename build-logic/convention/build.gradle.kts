plugins {
    `kotlin-dsl`
}

group = "com.jujodevs.pomodoro.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.roborazzi.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "pomodoro.android.application"
            implementationClass = "com.jujodevs.pomodoro.convention.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "pomodoro.android.library"
            implementationClass = "com.jujodevs.pomodoro.convention.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "pomodoro.android.compose"
            implementationClass = "com.jujodevs.pomodoro.convention.AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "pomodoro.android.feature"
            implementationClass = "com.jujodevs.pomodoro.convention.AndroidFeatureConventionPlugin"
        }
        register("androidFeatureApi") {
            id = "pomodoro.android.feature.api"
            implementationClass = "com.jujodevs.pomodoro.convention.AndroidFeatureApiConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "pomodoro.kotlin.library"
            implementationClass = "com.jujodevs.pomodoro.convention.KotlinLibraryConventionPlugin"
        }
        register("koin") {
            id = "pomodoro.koin"
            implementationClass = "com.jujodevs.pomodoro.convention.KoinConventionPlugin"
        }
        register("testing") {
            id = "pomodoro.testing"
            implementationClass = "com.jujodevs.pomodoro.convention.TestingConventionPlugin"
        }
        register("roborazzi") {
            id = "pomodoro.roborazzi"
            implementationClass = "com.jujodevs.pomodoro.convention.RoborazziConventionPlugin"
        }
    }
}
