plugins {
    id("cheddar.android.library")
    id("cheddar.android.compose")
    id("kotlin-parcelize")
}

android {
    namespace = "co.adrianblan.model"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
}