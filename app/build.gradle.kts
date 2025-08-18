signingConfigs {
  create("release") {
    storeFile = file(System.getenv("ANDROID_KEYSTORE_PATH") ?: "release.keystore")
    storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
    keyAlias = System.getenv("ANDROID_KEY_ALIAS")
    keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
    storeType = "pkcs12"
  }
}
buildTypes {
  release {
    signingConfig = signingConfigs.getByName("release")
    isMinifyEnabled = false
    proguardFiles(
      getDefaultProguardFile("proguard-android-optimize.txt"),
      "proguard-rules.pro"
    )
  }
}