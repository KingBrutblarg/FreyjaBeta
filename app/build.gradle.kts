android {
    namespace = "com.angeluz.freyja"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.angeluz.freyja"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
android {
    // ... tu configuración actual

    applicationVariants.all {
        val variantName = name
        val capitalized = variantName.replaceFirstChar { it.uppercase() }
        tasks.register<Copy>("copy${capitalized}ApkToDownloads") {
            dependsOn("assemble$capitalized")
            from("$buildDir/outputs/apk/$variantName")
            include("*.apk")
            into("${System.getenv("HOME")}/storage/downloads")
        }
    }
}
    }

    // --- Firma condicional / estricta ---
    val ksPath = System.getenv("ANDROID_KEYSTORE_FILE")
    val ksPass = System.getenv("ANDROID_KEYSTORE_PASSWORD")
    val keyAlias = System.getenv("ANDROID_KEY_ALIAS")
    val keyPass = System.getenv("ANDROID_KEY_PASSWORD")

    val hasAll = !ksPath.isNullOrBlank()
            && !ksPass.isNullOrBlank()
            && !keyAlias.isNullOrBlank()
            && !keyPass.isNullOrBlank()
            && file(ksPath!!).exists()

    signingConfigs {
        if (hasAll) {
            create("release") {
                storeFile = file(ksPath!!)
                storePassword = ksPass
                this.keyAlias = keyAlias
                keyPassword = keyPass
                enableV2Signing = true
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            if (hasAll) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                throw GradleException(
                    "❌ Faltan variables o archivo keystore para firmar release. " +
                    "Exporta: ANDROID_KEYSTORE_FILE, ANDROID_KEYSTORE_PASSWORD, ANDROID_KEY_ALIAS, ANDROID_KEY_PASSWORD"
                )
            }
        }
        getByName("debug") {
            // Debug queda con el keystore por defecto de Android
        }
    }
}