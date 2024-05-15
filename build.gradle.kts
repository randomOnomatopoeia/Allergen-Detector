// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
}
buildscript{
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2") // Android Gradle Plugin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0") // Kotlin Gradle Plugin
        buildscript {

            repositories {
                google()
                mavenCentral()
                maven { url = uri("https://jitpack.io") }
            }
        }
    }
}


