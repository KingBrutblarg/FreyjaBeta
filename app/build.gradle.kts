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

        // Solo ARM64 para reducir tamaño y asegurar compatibilidad
        ndk { abiFilters += listOf("arm64-v8a") }

        // Flags para CMake/llama.cpp
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17 -O3"
            }
        }
    }

    // Java/Kotlin a 17 para AGP 8 + JDK 17 en CI
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        // Necesario para BuildConfig.*
        buildConfig = true
        // Compose activado
        compose = true
    }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.15" }

    // Empaquetado JNI clásico (si tus .so se ubican en jniLibs/)
    packaging {
        jniLibs.useLegacyPackaging = true
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    // Ruta del CMakeLists.txt del módulo app
    externalNativeBuild { cmake { path = file("src/main/cpp/CMakeLists.txt") } }

    signingConfigs {
        create("release") {
            // Del workflow: ANDROID_KEYSTORE_PATH apunta al keystore restaurado
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

            // Inyectar secrets como BuildConfig.* (tolerante si faltan)
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
    // Utilidades de almacenamiento (SAF - carpetas)
    implementation("androidx.documentfile:documentfile:1.0.1")

    // Descarga por URL (si usas downloader)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Compose BOM + componentes
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // KTX base para Activity y Core (necesarios)
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.core:core-ktx:1.13.1")

    // Material (views/clásico) por si alguna parte lo usa; no es AppCompat
    implementation("com.google.android.material:material:1.12.0")

    // ⚠️ AppCompat solo si usas AppCompatActivity. Si ya migramos a ComponentActivity, puedes omitirlo:
    // implementation("androidx.appcompat:appcompat:1.7.0")
}