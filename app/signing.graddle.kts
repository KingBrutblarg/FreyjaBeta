// app/signing.gradle.kts
android {
    signingConfigs {
        create("release") {
            // El workflow define ANDROID_KEYSTORE_PATH al decodificar el secret
            storeFile = file(System.getenv("ANDROID_KEYSTORE_PATH") ?: "release.keystore")
            storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("ANDROID_KEY_ALIAS")
            keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
        }
    }
    buildTypes {
        getByName("release") {
            // Cambia a true si quieres R8/Proguard
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
}