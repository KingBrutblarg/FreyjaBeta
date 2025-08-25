plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Kotlin toolchain (Java 17)
kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.angeluz.freyja"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.angeluz.freyja"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        // 👇 Base URL configurable (DEBE terminar en "/")
        buildConfigField("String", "API_BASE_URL", "\"https://tu-backend.tld/\"")

        // Si la usas en ImageApi / backend
        buildConfigField("String", "IMG_API_KEY", "\"1226\"")

        // Opcional si usas vectores en <21
        vectorDrawables.useSupportLibrary = true
    }

    // Tipos de build
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Habilita Compose / ViewBinding / BuildConfig
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
        // dataBinding = false // (por claridad: no lo usamos)
    }

    // Versión del compilador de Compose (alineada con el BOM 2024.10.x)
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    // Kotlin/JVM
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-Xcontext-receivers"
        )
    }

    // Java (Javac) a 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // ABI splits: genera APK por arquitectura (ligero)
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86_64")
            isUniversalApk = false
        }
    }

    // Empaquetado (evita conflictos de metadatos)
    packaging {
        resources {
            excludes += setOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*kotlin_module"
            )
        }
    }

    // Opcional: tests
    testOptions {
        animationsDisabled = true
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    // ----- Compose BOM (mantiene versiones alineadas) -----
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose núcleo
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-graphics") // <- para asImageBitmap()

    // Core & Lifecycle
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

    // DataStore (prefs) ligero
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Imágenes
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Networking (Retrofit + Moshi + OkHttp)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // (Opcional) Si NO usas vistas clásicas, puedes evitar appcompat/material:
    // implementation("androidx.appcompat:appcompat:1.7.0")
    // implementation("com.google.android.material:material:1.12.0")
}