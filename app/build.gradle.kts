import java.io.FileInputStream
import java.util.Properties

plugins {
    id("cheddar.android.application")
    id("cheddar.android.dagger")
}

android {

    namespace = "co.adrianblan.cheddar"

    defaultConfig {
        applicationId = "co.adrianblan.cheddar"
        versionCode = 7
        versionName = "2.0"
    }

    signingConfigs {
        named("debug") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "debugkey"
            keyAlias = "debugkey"
            keyPassword = "debugkey"
        }
    }

    buildTypes {
        named("debug") {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
        }
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":feature:storyfeed"))
    implementation(project(":feature:storydetail"))

    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:network"))
    testImplementation(project(":core:testing"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.splashscreen)
    implementation(libs.timber)
    implementation(libs.coil)
    debugImplementation(libs.leakcanary)
}