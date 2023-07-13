plugins {
    id("cheddar.android.library")
    id("cheddar.android.dagger")
}

android {
    namespace = "co.adrianblan.domain"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:model"))
    // TODO fix with hilt
    api(project(":core:hackernews"))
    api(project(":core:webpreview"))

    implementation(project(":core:network"))
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core)
    implementation(libs.kotlinx.coroutines.android)
}