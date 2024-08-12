import io.morfly.airin.MissingComponentResolution
import io.morfly.airin.feature.AndroidBinaryFeature
import io.morfly.airin.feature.AndroidToolchainFeature
import io.morfly.airin.feature.ForcedMavenArtifactsFeature
import io.morfly.airin.feature.HiltFeature
import io.morfly.airin.feature.JetpackComposeFeature
import io.morfly.airin.module.AndroidLibraryModule
import io.morfly.airin.module.RootModule

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dependency.updates.plugin) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false

    id("io.morfly.airin.android") version libs.versions.airinGradle
}

airin {
    targets += setOf(":app")
    onMissingComponent = MissingComponentResolution.Fail
    register<AndroidLibraryModule> {
        include<AndroidBinaryFeature>()
        include<JetpackComposeFeature>()
        include<HiltFeature>()
    }
    register<RootModule> {
        include<AndroidToolchainFeature>()

        // When a conflict with transitive dependency versions occurs, Gradle and Bazel resolve
        // them differently. While Gradle resolves them automatically for the most part, Bazel
        // requires to explicitly force artifact versions.
        include<ForcedMavenArtifactsFeature> {
            artifacts = listOf(
                "androidx.lifecycle:lifecycle-runtime:2.6.1",
                "androidx.activity:activity-ktx:1.7.0",
                "androidx.compose.animation:animation-core:1.2.1",
                "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1",
            )
        }
    }
}