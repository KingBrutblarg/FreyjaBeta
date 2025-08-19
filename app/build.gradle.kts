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

  // Firma OPCIONAL: solo si ANDROID_KEYSTORE_PATH viene del entorno
  val ksPath = System.getenv("ANDROID_KEYSTORE_PATH")
  if (!ksPath.isNullOrBlank()) {
    signingConfigs {
      create("release") {
        storeFile = file(ksPath)
        storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
        keyAlias = System.getenv("ANDROID_KEY_ALIAS")
        keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
        storeType = "pkcs12"
      }
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      // Solo aplicamos la firma si existe keystore
      if (!ksPath.isNullOrBlank()) {
        signingConfig = signingConfigs.getByName("release")
      }
    }
    getByName("debug") {
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