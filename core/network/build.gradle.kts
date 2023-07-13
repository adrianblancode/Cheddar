plugins {
    id("cheddar.android.library")
    id("cheddar.android.dagger")
}

android {
    namespace = "co.adrianblan.network"
}

dependencies {
    api(libs.retrofit.core)
    api(libs.okhttp.core)
    implementation(libs.okhttp.interceptor)
}