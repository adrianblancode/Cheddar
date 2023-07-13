plugins {
    id("cheddar.android.library")
}

android {
    namespace = "co.adrianblan.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}