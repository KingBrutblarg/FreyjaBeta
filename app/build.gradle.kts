plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.angeluz.freyja"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.angeluz.freyja"
        minSdk = 26
        targetSdk = 34
        versionCode = 100
        versionName = "0.9-Standalone"

        ndk { abiFilters += listOf("arm64-v8a") } // solo ARM64
        externalNativeBuild { cmake { cppFlags += "-std=c++17 -O3" } }
    }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }

    packaging { jniLibs.useLegacyPackaging = true }

    externalNativeBuild { cmake { path = file("src/main/cpp/CMakeLists.txt") } }
}

dependencies {
    // (mantén las que ya tienes)
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}