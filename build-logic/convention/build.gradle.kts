plugins {
    `kotlin-dsl`
}

group = "com.jujodevs.pomodoro.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.roborazzi.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
    implementation(libs.ktlint.gradlePlugin)
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
        register("androidTesting") {
            id = "pomodoro.android.testing"
            implementationClass = "com.jujodevs.pomodoro.convention.AndroidTestingConventionPlugin"
        }
        register("roborazzi") {
            id = "pomodoro.roborazzi"
            implementationClass = "com.jujodevs.pomodoro.convention.RoborazziConventionPlugin"
        }
        register("detekt") {
            id = "pomodoro.detekt"
            implementationClass = "com.jujodevs.pomodoro.convention.DetektConventionPlugin"
        }
        register("ktlint") {
            id = "pomodoro.ktlint"
            implementationClass = "com.jujodevs.pomodoro.convention.KtlintConventionPlugin"
        }
    }
}
