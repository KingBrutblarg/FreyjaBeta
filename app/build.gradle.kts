android {
  // Ajusta estos si no los tienes ya
  namespace = "com.angeluz.freyja"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.angeluz.freyja"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  // --- Firma condicional para RELEASE ---
  // Lee variables de entorno (no se guardan en el repo)
  val ksPath: String? = System.getenv("ANDROID_KEYSTORE_FILE")
  val ksPass: String? = System.getenv("ANDROID_KEYSTORE_PASSWORD")
  val keyAlias: String? = System.getenv("ANDROID_KEY_ALIAS")
  val keyPass: String? = System.getenv("ANDROID_KEY_PASSWORD")

  // Verifica que realmente exista el archivo
  val hasKeystore = !ksPath.isNullOrBlank() && file(ksPath!!).exists()

  signingConfigs {
    if (hasKeystore) {
      create("release") {
        storeFile = file(ksPath!!)
        storePassword = ksPass
        this.keyAlias = keyAlias
        keyPassword = keyPass

        // (Opcional) asegura V2/V3 signing activado en builds modernos
        enableV2Signing = true
        enableV3Signing = true
      }
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false // o true si usas R8 y tienes proguard-rules.pro
      // Asigna la firma SOLO si existe keystore
      if (hasKeystore) {
        signingConfig = signingConfigs.getByName("release")
      } else {
        // Sin keystore: se genera APK/AAB sin firmar (útil para CI que firma aparte)
        println("⚠️  No ANDROID_KEYSTORE_FILE presente; release se generará sin firmar.")
      }
    }
    getByName("debug") {
      // Debug queda igual; usa el debug keystore por defecto de Android
    }
  }

  // (Opcional) Si usas shrinker:
  // buildTypes.getByName("release").proguardFiles(
  //   getDefaultProguardFile("proguard-android-optimize.txt"),
  //   "proguard-rules.pro"
  // )
}