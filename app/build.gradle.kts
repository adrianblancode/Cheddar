import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {

    val targetSdkVersion = 29
    compileSdkVersion(targetSdkVersion)

    defaultConfig {
        applicationId = "co.adrianblan.boilerplate"
        minSdkVersion(23)
        targetSdkVersion(targetSdkVersion)
        versionCode = 1
        versionName = "0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
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
                storeFile = file(props["storeFile"]!!)
                storePassword = props["storePassword"] as String
                keyAlias = props["keyAlias"] as String
                keyPassword = props["keyPassword"] as String
            } else {
                println("File keystore.properties not found.")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig =  signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            signingConfig =  signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(Dependencies.kotlinStdlib)
    implementation(Dependencies.androidxAppcompat)
    implementation(Dependencies.androidxCore)
    implementation(Dependencies.androidxUiLayout)
    implementation(Dependencies.androidxUiTooling)
    implementation(Dependencies.androidxMaterial)

    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.androidTestJunit)
    androidTestImplementation(Dependencies.androidTestEspressoCore)
}
