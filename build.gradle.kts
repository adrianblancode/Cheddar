// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        jcenter()
        maven { url = uri(ProjectDependencies.mavenUrl) }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-alpha02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
    }
}

allprojects {

    repositories {
        google()
        jcenter()
        maven { url = uri(ProjectDependencies.mavenUrl) }
    }
}