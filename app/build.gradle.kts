plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.angeluz.freyja"
    compileSdk = 34

    // Fija la NDK para reproducibilidad (opcional, pero recomendado si tu CI la define)
    ndkVersion = "26.3.11579264"

    defaultConfig {
        applicationId = "com.angeluz.freyja"
        minSdk = 26
        targetSdk = 34
        versionCode = 100
        versionName = "0.9-Standalone"

        // Solo ARM64 para reducir tamaño y asegurar compatibilidad
        ndk { abiFilters += listOf("arm64-v8a") }

        // Flags para CMake/llama.cpp de este variant
        externalNativeBuild {
            cmake {
                // Ajusta a tus necesidades (añade -mfpu/-march si compilas específicos)
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
        // freeCompilerArgs += listOf("-Xjvm-default=all") // si algún lib lo requiere
    }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }

    // Empaquetado JNI clásico (si tus .so se ubican en jniLibs/)
    packaging { jniLibs.useLegacyPackaging = true }

    // Ruta del CMakeLists.txt del módulo app
    externalNativeBuild { cmake { path = file("src/main/cpp/CMakeLists.txt") } }

    // (Opcional) Excluir licencias duplicadas en libs
    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    // Utilidades de almacenamiento (SAF - carpetas)
    implementation("androidx.documentfile:documentfile:1.0.1")

    // Descarga por URL (si usas el downloader)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.9.2")
}
