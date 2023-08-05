plugins {
    id("cheddar.android.library")
    id("cheddar.android.dagger")
}

android {
    namespace = "co.adrianblan.network"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.interceptor)
}