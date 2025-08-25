############################
# Coroutines
############################
# R8 ya entiende coroutines; con esto basta:
-dontwarn kotlinx.coroutines.**

# (Opcional) si ves ofuscación agresiva rompiendo stacktraces de corrutinas:
# -keep class kotlinx.coroutines.internal.MainDispatcherLoader { *; }

############################
# OkHttp / Retrofit
############################
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn javax.annotation.**                    # firmas anotadas
-dontwarn org.codehaus.mojo.animal_sniffer.**    # firmas Java 8

# Conserva metadatos que Retrofit/Moshi usan en runtime
-keepattributes Signature, Exceptions, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations
-keepattributes RuntimeVisibleTypeAnnotations, RuntimeInvisibleTypeAnnotations

############################
# Moshi (REFLEXIÓN, sin codegen)
############################
# Mantén anotaciones/fields que Moshi inspecciona
-keepclassmembers class ** {
    @com.squareup.moshi.Json <fields>;
    @com.squareup.moshi.JsonQualifier *;
}

# Métodos From/ToJson si usas adapters a mano (seguro incluirlos)
-keepclasseswithmembers class * {
    @com.squareup.moshi.FromJson <methods>;
}
-keepclasseswithmembers class * {
    @com.squareup.moshi.ToJson <methods>;
}

# Si usas campos con @JsonAdapter en tus modelos:
-keepclassmembers class ** {
    @com.squareup.moshi.JsonAdapter <fields>;
}

# Mantener tus modelos (amplio pero práctico)
-keep class com.angeluz.freyja.data.** { *; }

############################
# (Opcional) Compose / Kotlin
############################
# Compose no suele requerir reglas manuales; R8 ya trae defaults.
# Si ves warnings de kotlin.Metadata, puedes silenciarlos:
# -dontwarn kotlin.Metadata