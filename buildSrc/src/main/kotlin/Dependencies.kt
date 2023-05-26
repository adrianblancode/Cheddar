object Versions {
    const val minSdk = 23
    const val targetSdk = 30

    const val kotlin = "1.7.20"
    const val coroutines = "1.6.4"

    const val androidxAppcompat = "1.7.0-alpha01"
    const val androidxCore = "1.9.0"
    const val androidxActivity = "1.7.0-alpha02"
    const val composeBom = "2022.10.00"
    const val composeCompilerExtension = "1.3.2"

    const val dagger = "2.44"
    const val assistedInject = "0.8.1"
    const val okhttp = "5.0.0-alpha.10"
    const val retrofit = "2.9.0"

    const val junit = "4.13.2"
    const val androidTestJunit = "1.1.1"
    const val mockito = "4.8.1"
}

object Dependencies {

    const val androidGradlePlugin = "com.android.tools.build:gradle:8.1.0-alpha11"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"

    const val kotlinSerializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
    const val kotlinSerializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC"

    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid =  "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesTest =  "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"

    const val androidxAppcompat = "androidx.appcompat:appcompat:${Versions.androidxAppcompat}"
    const val androidxCore = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val androidxActivity = "androidx.activity:activity-ktx:${Versions.androidxActivity}"
    const val androidxBrowser = "androidx.browser:browser:1.2.0"

    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val composeMaterial = "androidx.compose.material:material"
    const val composeFoundation = "androidx.compose.foundation:foundation"
    const val composeAnimation = "androidx.compose.animation:animation"
    const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose"
    const val composeTooling = "androidx.ui:ui-tooling"
    const val composeToolingPreview = "androidx.ui:ui-tooling-preview"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    const val desugar = "com.android.tools:desugar_jdk_libs:1.0.9"

    const val okhttpCore = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLoggingInterceptor =  "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val retrofitCore = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitSerializationConverter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"

    const val picasso = "com.squareup.picasso:picasso:2.71828"
    const val jsoup = "org.jsoup:jsoup:1.13.1"

    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.9.1"
    const val timber = "com.jakewharton.timber:timber:5.0.1"
    const val junit = "junit:junit:${Versions.junit}"
    const val androidTestJunit = "androidx.test.ext:junit:${Versions.androidTestJunit}"
    const val androidxCoreTesting = "androidx.arch.core:core-testing:2.1.0"

    const val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockito}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
}