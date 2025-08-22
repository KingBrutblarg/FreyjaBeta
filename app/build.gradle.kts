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
        versionCode = 1
        versionName = "1.0"
    }
dependencies {
    // ...lo que ya tienes...

    // Serialización JSON (para parsear respuestas si lo necesitas)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Para compat util (WorkManager ya lo tienes si lo usas luego)
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
}
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // --- RED / HTTP ---
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // opcional

    // --- DataStore (preferencias) ---
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Coroutines (para DataStore/asincronía) ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // --- AndroidX / UI ---
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
}