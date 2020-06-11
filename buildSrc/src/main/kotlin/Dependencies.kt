object Versions {
    const val minSdk = 23
    const val targetSdk = 29

    const val kotlin = "1.3.72"
    const val coroutines = "1.3.6"

    const val androidxAppcompat = "1.1.0"
    const val androidxCore = "1.2.0-alpha01"
    const val androidxActivity = "1.1.0-rc02"
    const val compose = "0.1.0-dev13"
    const val composeCompiler = "1.3.70-dev-withExperimentalGoogleExtensions-20200424"

    const val dagger = "2.27"
    const val assistedInject = "0.5.2"
    const val okhttp = "4.6.0"
    const val retrofit = "2.8.1"

    const val junit = "4.13"
    const val androidTestJunit = "1.1.1"
    const val mockito = "3.3.3"
}

object Dependencies {

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val kotlinSerializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
    const val kotlinSerializationRuntime = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0"

    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid =  "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesTest =  "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"

    const val androidxAppcompat = "androidx.appcompat:appcompat:${Versions.androidxAppcompat}"
    const val androidxCore = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val androidxActivity = "androidx.activity:activity-ktx:${Versions.androidxActivity}"
    const val androidxBrowser = "androidx.browser:browser:1.2.0"

    const val composeRuntime = "androidx.compose:compose-runtime:${Versions.compose}"
    const val composeLayout = "androidx.ui:ui-layout:${Versions.compose}"
    const val composeMaterial = "androidx.ui:ui-material:${Versions.compose}"
    const val composeFoundation = "androidx.ui:ui-foundation:${Versions.compose}"
    const val composeAnimation = "androidx.ui:ui-animation:${Versions.compose}"
    const val composeTooling = "androidx.ui:ui-tooling:${Versions.compose}"
    const val composeIcons = "androidx.ui:ui-material-icons-extended:${Versions.compose}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val assistedInject = "com.squareup.inject:assisted-inject-annotations-dagger2:${Versions.assistedInject}"
    const val assistedInjectProcessor = "com.squareup.inject:assisted-inject-processor-dagger2:${Versions.assistedInject}"

    const val desugar = "com.android.tools:desugar_jdk_libs:1.0.4"

    const val okhttpCore = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLoggingInterceptor =  "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val retrofitCore = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitSerializationConverter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.5.0"

    const val picasso = "com.squareup.picasso:picasso:2.71828"
    const val jsoup = "org.jsoup:jsoup:1.13.1"

    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.2"
    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val junit = "junit:junit:${Versions.junit}"
    const val androidTestJunit = "androidx.test.ext:junit:${Versions.androidTestJunit}"
    const val androidxCoreTesting = "androidx.arch.core:core-testing:2.1.0"

    const val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockito}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
}