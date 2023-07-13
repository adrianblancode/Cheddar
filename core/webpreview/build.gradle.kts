plugins {
    id("cheddar.android.library")
    id("cheddar.android.dagger")
}

android {
    namespace = "co.adrianblan.webpreview"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.jsoup)
}