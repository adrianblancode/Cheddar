plugins {
    id("cheddar.android.library")
}

android {
    namespace = "co.adrianblan.testing"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.androidx.core.test)
    implementation(libs.kotlinx.coroutines.test)
}