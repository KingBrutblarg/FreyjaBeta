plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.angeluz.freyja"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.angeluz.freyja"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // --- BuildConfig desde secretos (CI) con fallback local seguro ---
        val apiBaseUrl = System.getenv("API_BASE_URL") ?: "https://echo.hoppscotch.io/"
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")

        val imgKey = System.getenv("IMG_API_KEY") ?: ""
        buildConfigField("String", "IMG_API_KEY", "\"$imgKey\"")
    }

    // --- Firma de release restaurada por el workflow desde secreto ---
    signingConfigs {
        create("release") {
            // El workflow deja el keystore en la raíz del repo
            storeFile = rootProject.file("freyja-release.keystore")
            storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("ANDROID_KEY_ALIAS")
            keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }

    // Compose Compiler compatible con Kotlin 1.9.x
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    // Kotlin/JVM 17
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-Xcontext-receivers"
        )
    }

    // Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // --- Pulido de empaquetado: evita choques META-INF y ruidos ---
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,licenses/**}"
            excludes += "/META-INF/*.version"
        }
        // Si alguna AAR trae .so raros y molestan los "Unable to strip", podemos mantener símbolos:
        // jniLibs.keepDebugSymbols.add("**/*.so")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // Compose
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.ui:ui-graphics:1.7.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0")
    implementation("androidx.compose.material3:material3:1.3.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Imágenes
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
