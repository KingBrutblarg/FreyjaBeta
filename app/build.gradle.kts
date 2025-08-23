plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// 👉 fuera de `plugins`
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
            isUniversalApk = false
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

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

    buildFeatures {
        dataBinding = false
        compose = true
        viewBinding = true
    }

    composeOptions { kotlinCompilerExtensionVersion = "1.5.15" }

    packaging {
        resources {
            excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1", "META-INF/*kotlin_module")
        }
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xjvm-default=all","-Xcontext-receivers")
    }

    testOptions {
        animationsDisabled = true
        unitTests.isIncludeAndroidResources = true
    }
}