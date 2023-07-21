plugins {
    id("cheddar.android.library")
    id("cheddar.android.compose")
}

android {
    namespace = "co.adrianblan.ui"
}

dependencies {
    api(libs.androidx.appcompat)
    api(libs.androidx.core)
    api(libs.androidx.activity)

    api(libs.androidx.compose.animation)
    api(libs.androidx.compose.material.core)
    implementation(libs.androidx.compose.material.icons)
    api(libs.androidx.compose.foundation.core)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.compose)
    api(libs.androidx.compose.ui.tooling.core)
    debugApi(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.coil)
}