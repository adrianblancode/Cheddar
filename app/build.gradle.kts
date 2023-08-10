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
        versionCode = 1
        versionName = "0.1"
    }

    signingConfigs {
        named("debug") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "debugkey"
            keyAlias = "debugkey"
            keyPassword = "debugkey"
        }
        create("release") {
            val propsFile = rootProject.file("keystore.properties")
            if (propsFile.exists()) {
                val props = Properties()
                props.load(FileInputStream(propsFile))
                storeFile = file(props.getProperty("storeFile"))
                storePassword = props.getProperty("storePassword")
                keyAlias = props.getProperty("keyAlias")
                keyPassword = props.getProperty("keyPassword")
            } else {
                println("File keystore.properties not found.")
            }
        }
    }

    buildTypes {
        named("debug") {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }
        named("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../proguard-rules.pro"
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
    implementation(libs.timber)
    implementation(libs.coil)
    debugImplementation(libs.leakcanary)
}