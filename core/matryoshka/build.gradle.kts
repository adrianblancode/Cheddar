plugins {
    id("cheddar.android.library")
    id("cheddar.android.compose")
}

android {
    namespace = "co.adrianblan.matryoshka"
}

dependencies {
    implementation(project(":core:common"))
    api(libs.androidx.activity)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.lifecycle.compose)
    api(libs.kotlinx.coroutines.android)
}