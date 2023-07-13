plugins {
    id("cheddar.android.feature")
    id("cheddar.android.compose")
}

android {
    namespace = "co.adrianblan.storynavigation"
}

dependencies {
    implementation(project(":feature:storyfeed"))
    implementation(project(":feature:storydetail"))
}