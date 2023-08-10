plugins {
    id("cheddar.android.library")
    id("cheddar.android.compose")
}

android {
    namespace = "co.adrianblan.ui"
}

dependencies {
    implementation(project(":core:model"))
    api(libs.androidx.appcompat)
    api(libs.androidx.core)
    api(libs.androidx.activity)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.compose)

    api(libs.androidx.compose.animation)
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    api(libs.androidx.compose.foundation.core)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.ui.tooling.core)
    debugApi(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.constraintlayout)
    implementation(libs.androidx.splashscreen)

    implementation(libs.coil)
    implementation(libs.coil.svg)
}