object Versions {
    const val kotlin = "1.3.60-eap-76"

    const val androidxAppcompat = "1.1.0"
    const val androidxCore = "1.1.0"
    const val androidxUiLayout = "0.1.0-dev02"
    const val androidxUiTooling = "0.1.0-dev02"
    const val androidxMaterial = "0.1.0-dev02"

    const val junit = "4.12"
    const val androidTestJunit = "1.1.1"
    const val androidTestEspressoCore = "3.2.0"
}

object ProjectDependencies {
    const val mavenUrl = "https://dl.bintray.com/kotlin/kotlin-eap"
}

object Dependencies {
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val androidxAppcompat = "androidx.appcompat:appcompat:${Versions.androidxAppcompat}"
    const val androidxCore = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val androidxUiLayout = "androidx.ui:ui-layout:${Versions.androidxUiLayout}"
    const val androidxUiTooling = "androidx.ui:ui-tooling:${Versions.androidxUiTooling}"
    const val androidxMaterial = "androidx.ui:ui-material:${Versions.androidxMaterial}"

    const val junit = "junit:junit:${Versions.junit}"
    const val androidTestJunit = "androidx.test.ext:junit:${Versions.androidTestJunit}"
    const val androidTestEspressoCore =
        "androidx.test.espresso:espresso-core:${Versions.androidTestEspressoCore}"
}