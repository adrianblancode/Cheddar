plugins {
    id("cheddar.android.feature")
    id("cheddar.android.compose")
}

android {
    namespace = "co.adrianblan.storyfeed"
}

dependencies {
    implementation(project(":core:hackernews"))
    implementation(project(":core:webpreview"))
}