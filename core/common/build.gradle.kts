plugins {
    id("cheddar.android.library")
}

android {
    namespace = "co.adrianblan.common"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.android)
}