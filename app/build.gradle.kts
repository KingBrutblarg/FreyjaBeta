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
    versionCode = 9
    versionName = "0.9"
  }

  // Firma (lee credenciales del entorno que pone el workflow)
  signingConfigs {
    create("release") {
      storeFile = file(System.getenv("ANDROID_KEYSTORE_PATH") ?: "keystore.jks")
      storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
      keyAlias = System.getenv("ANDROID_KEY_ALIAS")
      keyPassword = System.getenv("ANDROID_KEY_ALIAS_PASSWORD")
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      isDebuggable = true
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }

  buildFeatures { viewBinding = true }
}

dependencies {
  // AndroidX
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.activity:activity-ktx:1.9.2")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

  // Kotlin / Coroutines / DataStore
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
  implementation("androidx.datastore:datastore-preferences:1.1.1")

  // Ktor (si lo usas)
  val ktor = "2.3.12"
  implementation("io.ktor:ktor-client-core:$ktor")
  implementation("io.ktor:ktor-client-cio:$ktor")
  implementation("io.ktor:ktor-client-websockets:$ktor")

  // OkHttp (para HybridInvoker)
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.squareup.okio:okio:3.6.0")
  // implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // opcional
}