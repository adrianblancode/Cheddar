plugins {
    id("cheddar.android.library")
    id("cheddar.android.dagger")
}

android {
    namespace = "co.adrianblan.domain"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:hackernews"))
    implementation(project(":core:webpreview"))

    implementation(libs.androidx.browser)
    implementation(libs.androidx.core)
    implementation(libs.kotlinx.coroutines.android)
}