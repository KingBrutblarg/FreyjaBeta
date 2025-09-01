plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.angeluz.freyja"
    compileSdk = 34
    ndkVersion = "26.3.11579264"

    defaultConfig {
        applicationId = "com.angeluz.freyja"
        minSdk = 26
        targetSdk = 34
        versionCode = 100
        versionName = "0.9-Standalone"

        ndk { abiFilters += listOf("arm64-v8a") }

        externalNativeBuild {
            cmake { cppFlags += "-std=c++17 -O3" }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.15" }

    packaging {
        jniLibs.useLegacyPackaging = true
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    externalNativeBuild {
        cmake { path = file("src/main/cpp/CMakeLists.txt") }
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("ANDROID_KEYSTORE_PATH") ?: "freyja-release.keystore")
            storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("ANDROID_KEY_ALIAS")
            keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            val apiBase = System.getenv("API_BASE_URL") ?: ""
            val pass = System.getenv("BACKUP_PASSPHRASE") ?: ""
            buildConfigField("String", "API_BASE_URL", "\"$apiBase\"")
            buildConfigField("String", "BACKUP_PASSPHRASE", "\"$pass\"")
        }
        getByName("debug") {
            val apiBase = System.getenv("API_BASE_URL") ?: "http://10.0.2.2:8080"
            val pass = System.getenv("BACKUP_PASSPHRASE") ?: "debug-pass"
            buildConfigField("String", "API_BASE_URL", "\"$apiBase\"")
            buildConfigField("String", "BACKUP_PASSPHRASE", "\"$pass\"")
        }
    }
}

dependencies {
    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // AndroidX / Compose
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.material:material:1.12.0")

    // Utilidades
    implementation("androidx.documentfile:documentfile:1.0.1")
    // AppCompat solo si la necesitas:
    // implementation("androidx.appcompat:appcompat:1.7.0")
}