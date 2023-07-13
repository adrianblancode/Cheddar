plugins {
    id("cheddar.android.library")
    id("cheddar.android.dagger")
    id("kotlinx-serialization")
}

android {
    namespace = "co.adrianblan.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))

    implementation(libs.kotlinx.coroutines.android)
    // Implemented here because module compiler plugin is needed
    implementation(libs.kotlinx.serialization)
    implementation(libs.retrofit.serialization)
}
