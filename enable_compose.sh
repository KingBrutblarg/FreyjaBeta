#!/data/data/com.termux/files/usr/bin/bash
set -e

# 1) Detecta el build.gradle* de módulo "application"
TARGET=$(grep -RIl --include=build.gradle --include=build.gradle.kts "com.android.application" . | head -n1 || true)
if [ -z "$TARGET" ]; then
  echo "❌ No encontré un módulo con 'com.android.application'. Revisa tu estructura."
  echo "Archivos gradle encontrados:"
  find . -maxdepth 3 -name "build.gradle*" -print
  exit 1
fi

echo "➤ Módulo detectado: $TARGET"

# 2) Groovy o KTS
EXT="${TARGET##*.}"  # gradle o kts

if [ "$EXT" = "gradle" ]; then
  echo "➤ Aplicando cambios (Groovy)..."

  # Habilitar compose en android{}
  if ! grep -q 'buildFeatures' "$TARGET"; then
    sed -i 's/android {/android {\n    buildFeatures {\n        compose true\n    }\n    composeOptions {\n        kotlinCompilerExtensionVersion "1.5.14"\n    }\n/' "$TARGET"
  else
    # añade compose true si falta
    sed -i '/buildFeatures\s*{[^}]*}/!b;/buildFeatures\s*{/a\        compose true' "$TARGET" || true
    # añade composeOptions si falta
    grep -q 'composeOptions' "$TARGET" || sed -i 's/android {/android {\n    composeOptions {\n        kotlinCompilerExtensionVersion "1.5.14"\n    }\n/' "$TARGET"
  fi

  # Añadir dependencias Compose (usando BOM)
  awk '/dependencies *{/{print;print "    implementation platform(\"androidx.compose:compose-bom:2024.08.00\")\n    implementation \"androidx.activity:activity-compose:1.9.2\"\n    implementation \"androidx.compose.material3:material3\"\n    implementation \"androidx.compose.ui:ui\"\n    implementation \"androidx.compose.ui:ui-tooling-preview\"\n    debugImplementation \"androidx.compose.ui:ui-tooling\"\n    implementation \"androidx.lifecycle:lifecycle-runtime-compose:2.8.4\"";next}1' "$TARGET" > "$TARGET.tmp" && mv "$TARGET.tmp" "$TARGET"

elif [ "$EXT" = "kts" ]; then
  echo "➤ Aplicando cambios (Kotlin DSL)..."

  # Habilitar compose en android{}
  if ! grep -q 'buildFeatures' "$TARGET"; then
    sed -i 's/android {/android {\n    buildFeatures {\n        compose = true\n    }\n    composeOptions {\n        kotlinCompilerExtensionVersion = "1.5.14"\n    }\n/' "$TARGET"
  else
    # compose = true si falta
    sed -i '/buildFeatures\s*{[^}]*}/!b;/buildFeatures\s*{/a\        compose = true' "$TARGET" || true
    # composeOptions si falta
    grep -q 'composeOptions' "$TARGET" || sed -i 's/android {/android {\n    composeOptions {\n        kotlinCompilerExtensionVersion = "1.5.14"\n    }\n/' "$TARGET"
  fi

  # Añadir dependencias Compose (BOM)
  awk '/dependencies *{/{print;print "    implementation(platform(\"androidx.compose:compose-bom:2024.08.00\"))\n    implementation(\"androidx.activity:activity-compose:1.9.2\")\n    implementation(\"androidx.compose.material3:material3\")\n    implementation(\"androidx.compose.ui:ui\")\n    implementation(\"androidx.compose.ui:ui-tooling-preview\")\n    debugImplementation(\"androidx.compose.ui:ui-tooling\")\n    implementation(\"androidx.lifecycle:lifecycle-runtime-compose:2.8.4\")";next}1' "$TARGET" > "$TARGET.tmp" && mv "$TARGET.tmp" "$TARGET"

else
  echo "❌ Extensión desconocida: $EXT"
  exit 1
fi

echo "✅ Compose + Material3 agregados en: $TARGET"
