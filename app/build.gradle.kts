

plugins {
    alias(libs.plugins.androidApplication)

}

android {
    namespace = "com.example.allergendetector"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.allergendetector"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    buildToolsVersion = "34.0.0"
    ndkVersion = "26.2.11394342"
}


dependencies {
    val nav_version = "2.7.7"
    // Remove the vision library as it's not needed directly when using ML Kit
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")

    // Required libraries for the app
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.runtime)

    // Testing libraries
    implementation(libs.junit)
    implementation(libs.ext.junit)
    implementation(libs.espresso.core)

    // Navigation dependencies
    implementation(libs.navigation.fragment)
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Other necessary dependencies
    implementation ("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.android.gms:play-services-base:18.5.0")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1") // Update to the latest version
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")



}


















