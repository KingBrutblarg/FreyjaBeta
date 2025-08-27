android {
    // ... lo que ya tienes (namespace, compileSdk, defaultConfig, signing, buildTypes, etc.)

    defaultConfig {
        // ...
        // Toma la URL del entorno (secrets del workflow) o usa un fallback seguro para builds locales
        val apiBaseUrl = System.getenv("API_BASE_URL") ?: "https://example.invalid/"
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    // composeOptions, kotlinOptions, etc.
}