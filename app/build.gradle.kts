plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.kingbrutblarg.freyjabeta"        // 👈 que coincida con tu app
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kingbrutblarg.freyjabeta" // 👈 igual que arriba
        minSdk = 24
        targetSdk = 34
        // versionCode y versionName como los tengas
    }

    // 🔐 lee credenciales desde gradle.properties
    signingConfigs {
        create("release") {
            val ksFile  = project.findProperty("RELEASE_STORE_FILE") as String?
            val ksPass  = project.findProperty("RELEASE_STORE_PASSWORD") as String?
            val keyAl   = project.findProperty("RELEASE_KEY_ALIAS") as String?
            val keyPass = project.findProperty("RELEASE_KEY_PASSWORD") as String?
            if (ksFile != null && ksPass != null && keyAl != null && keyPass != null) {
                storeFile = file(ksFile)   // relativo al módulo "app"
                storePassword = ksPass
                keyAlias = keyAl
                keyPassword = keyPass
                storeType = "pkcs12"
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") { isDebuggable = true }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { viewBinding = true }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    val ktor = "2.3.12"
    implementation("io.ktor:ktor-client-core:$ktor")
    implementation("io.ktor:ktor-client-cio:$ktor")
    implementation("io.ktor:ktor-client-websockets:$ktor")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okio:okio:3.6.0")
}