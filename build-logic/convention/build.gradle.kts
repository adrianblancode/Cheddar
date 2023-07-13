import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "co.adrianblan.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradleplugin)
    compileOnly(libs.kotlin.gradleplugin)
    compileOnly(libs.ksp.gradleplugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "cheddar.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidFeature") {
            id = "cheddar.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidCompose") {
            id = "cheddar.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidDagger") {
            id = "cheddar.android.dagger"
            implementationClass = "AndroidDaggerConventionPlugin"
        }
        register("androidLibrary") {
            id = "cheddar.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("jvmLibrary") {
            id = "cheddar.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}