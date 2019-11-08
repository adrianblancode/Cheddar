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

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
