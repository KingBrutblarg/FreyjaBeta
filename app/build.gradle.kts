plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Kotlin toolchain a Java 17 (fuera de plugins)
kotlin {
    jvmToolchain(17)
}

android {

    // 🔥 Splits por ABI: genera un APK por arquitectura (más pequeños)
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86_64")
            isUniversalApk = false // no generes APK universal (más pesado)
        }
    }

    namespace = "com.angeluz.freyja"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.angeluz.freyja"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables.useSupportLibrary = true
    }

    // Alinea Javac a 17 (evita mismatch con Kotlin)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("debug") {
            // Mantén debug rápido
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

    buildFeatures {
        dataBinding = false
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15" // Alineado con Compose BOM 2024.10+
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*kotlin_module"
            )
        }
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-Xcontext-receivers"
        )
    }

    // Acelera tareas de test/instrumentación (si las agregamos luego)
    testOptions {
        animationsDisabled = true
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    // DataStore (prefs)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ----- Compose BOM (mantiene versiones alineadas) -----
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1