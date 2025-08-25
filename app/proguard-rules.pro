############################
# Coroutines
############################
-dontwarn kotlinx.coroutines.**

############################
# OkHttp / Retrofit
############################
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.**

# Mantén metadatos útiles en runtime
-keepattributes Signature, Exceptions, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations
-keepattributes RuntimeVisibleTypeAnnotations, RuntimeInvisibleTypeAnnotations

############################
# Moshi (reflexión)
############################
-keepclassmembers class ** {
    @com.squareup.moshi.Json <fields>;
    @com.squareup.moshi.JsonQualifier *;
}

-keepclasseswithmembers class * {
    @com.squareup.moshi.FromJson <methods>;
}
-keepclasseswithmembers class * {
    @com.squareup.moshi.ToJson <methods>;
}

# Si usas @JsonAdapter en campos
-keepclassmembers class ** {
    @com.squareup.moshi.JsonAdapter <fields>;
}

# Mantén tus modelos (práctico)
-keep class com.angeluz.freyja.data.** { *; }

############################
# (Opcional) Compose / Kotlin
############################
# -dontwarn kotlin.Metadata
