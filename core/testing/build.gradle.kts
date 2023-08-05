plugins {
    id("cheddar.android.library")
    id("cheddar.android.dagger")
}

android {
    namespace = "co.adrianblan.testing"
}

dependencies {
    implementation(project(":core:common"))

    api(libs.androidx.core.test)
    api(libs.kotlinx.coroutines.test)
    api(libs.hilt.android.testing)
    api(libs.junit)
    api(libs.turbine)
}