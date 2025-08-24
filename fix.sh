#!/usr/bin/env bash
set -uxo pipefail   # quitamos -e para no cerrar la sesión; dejamos -u -x y pipefail

# --- Helpers ---
require() { command -v "$1" >/dev/null || { echo "Falta $1"; exit 1; }; }
require git
require awk
require sed
require perl

root="$(pwd)"

# Función: obtiene paquete de un símbolo (class/object/enum) y su archivo
find_symbol() {
  local sym="$1"
  local f
  f="$(git grep -nE "^(class|object|enum)\s+$sym\b" -- 'app/src/main/java/**/*.kt' | head -n1 | cut -d: -f1 || true)"
  if [ -n "${f:-}" ] && [ -f "$f" ]; then
    local pkg
    pkg="$(awk '/^package /{print $2; exit}' "$f")"
    printf "%s|%s\n" "$pkg" "$f"
  fi
}

# --- 1) Detecta paquetes reales ---
PREFS_INFO="$(find_symbol Prefs || true)"
SPEAK_INFO="$(git grep -nE '^enum\s+class\s+SpeakMode\b' -- 'app/src/main/java/**/*.kt' | head -n1 | cut -d: -f1 || true)"
if [ -z "$PREFS_INFO" ]; then echo "No se encontró Prefs"; fi
if [ -n "$SPEAK_INFO" ]; then SPEAK_PKG="$(awk '/^package /{print $2; exit}' "$SPEAK_INFO")"; else echo "No se encontró SpeakMode"; fi

PREFS_PKG="${PREFS_INFO%%|*}"
CHATVM_INFO="$(find_symbol ChatViewModel || true)"
RETRO_INFO="$(find_symbol RetrofitProvider || true)"
REQ_INFO="$(find_symbol ChatRequest || true)"

CHATVM_PKG="${CHATVM_INFO%%|*}"
RETRO_PKG="${RETRO_INFO%%|*}"
REQ_PKG="${REQ_INFO%%|*}"

echo "PKG Prefs       : ${PREFS_PKG:-<no encontrado>}"
echo "PKG SpeakMode   : ${SPEAK_PKG:-<no encontrado>}"
echo "PKG ChatViewModel: ${CHATVM_PKG:-<no encontrado>}"
echo "PKG RetrofitProv : ${RETRO_PKG:-<no encontrado>}"
echo "PKG ChatRequest  : ${REQ_PKG:-<no encontrado>}"

# --- 2) Awareness.kt: importa Prefs ---
AWARE="app/src/main/java/com/angeluz/freyja/Awareness.kt"
if [ -f "$AWARE" ] && [ -n "${PREFS_PKG:-}" ]; then
  awk -v imp="import ${PREFS_PKG}.Prefs" '
    BEGIN{have=0}
    /^import /{ if($0==imp) have=1 }
    { print }
    END{ if(!have) print imp }
  ' "$AWARE" > "$AWARE.tmp" && mv "$AWARE.tmp" "$AWARE"
fi

# --- 3) HybridInvoker.kt: imports + collectLatest + when ---
HYB="app/src/main/java/com/angeluz/freyja/HybridInvoker.kt"
if [ -f "$HYB" ]; then
  if [ -n "${PREFS_PKG:-}" ]; then
    grep -q "import ${PREFS_PKG}\.Prefs$" "$HYB" || sed -i "1,/^package /!b;//a import ${PREFS_PKG}.Prefs" "$HYB"
    if [ -n "${SPEAK_PKG:-}" ]; then
      grep -q "import ${SPEAK_PKG}\.SpeakMode$" "$HYB" || sed -i "1,/^package /!b;//a import ${SPEAK_PKG}.SpeakMode" "$HYB"
    else
      grep -q "import ${PREFS_PKG}\.Prefs\.SpeakMode$" "$HYB" || sed -i "1,/^package /!b;//a import ${PREFS_PKG}.Prefs.SpeakMode" "$HYB"
    fi
  fi

  sed -i 's/\bspeakModeFlow\b/Prefs.speakModeFlow/g' "$HYB"

  sed -i 's/collectLatest *{ *}/collectLatest { _: SpeakMode -> }/g' "$HYB"
  sed -i 's/collectLatest *{ *it *->/collectLatest { mode: SpeakMode ->/g' "$HYB"
  sed -i 's/collectLatest *{ *mode *->/collectLatest { mode: SpeakMode ->/g' "$HYB"
  perl -0777 -pe 's/collectLatest *\{\s*(?![^}]*->)/collectLatest { mode: SpeakMode -> /g' -i "$HYB"

  perl -0777 -pe 's/(when *\(\s*mode\s*\)\s*\{)([^}]*)(\})/$1$2\n        else -> Unit\n    $3/s' -i "$HYB"
fi

# --- 4) MainActivity.kt: FreyjaScreen requiere vm ---
MAIN="app/src/main/java/com/angeluz/freyja/MainActivity.kt"
SCREEN="app/src/main/java/com/angeluz/freyja/ui/screens/FreyjaScreen.kt"

if [ -f "$MAIN" ]; then
  if grep -qE 'FreyjaScreen\(' "$MAIN" && ! grep -qE 'FreyjaScreen\(.*vm\s*=' "$MAIN"; then
    if git grep -q 'com.google.dagger.hilt' -- 'app/src/main/java/**/*.kt' || grep -q 'hilt-android' app/build.gradle*; then
      sed -i 's/FreyjaScreen(/FreyjaScreen(vm = androidx.hilt.navigation.compose.hiltViewModel(), /' "$MAIN"
    else
      sed -i 's/FreyjaScreen(/FreyjaScreen(vm = androidx.lifecycle.viewmodel.compose.viewModel(), /' "$MAIN"
    fi
  fi
fi

# --- 5) FreyjaScreen.kt: imports ausentes + default vm ---
if [ -f "$SCREEN" ]; then
  [ -n "${CHATVM_PKG:-}" ] && { grep -q "import ${CHATVM_PKG}\.ChatViewModel$" "$SCREEN" || sed -i "1,/^package /!b;//a import ${CHATVM_PKG}.ChatViewModel" "$SCREEN"; }
  [ -n "${RETRO_PKG:-}" ]  && { grep -q "import ${RETRO_PKG}\.RetrofitProvider$" "$SCREEN" || sed -i "1,/^package /!b;//a import ${RETRO_PKG}.RetrofitProvider" "$SCREEN"; }
  [ -n "${REQ_PKG:-}" ]    && { grep -q "import ${REQ_PKG}\.ChatRequest$" "$SCREEN" || sed -i "1,/^package /!b;//a import ${REQ_PKG}.ChatRequest" "$SCREEN"; }

  perl -0777 -pe 's/@Composable\s+fun\s+FreyjaScreen\s*\(\s*vm\s*:\s*ChatViewModel\s*\)/@Composable fun FreyjaScreen(vm: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel())/s' -i "$SCREEN"

  # Fuerza named-arg en Text(...) cuando el primer arg es el texto
  perl -0777 -pe 's/\bText\(\s*([^) ,][^=,)]+)\s*([,)]) /Text(text = \1\2 /g' -i "$SCREEN"
fi

echo "=== Diff de cambios ==="
git -c color.ui=always --no-pager diff

echo "=== Fin. Ejecuta gradle aparte para ver errores restantes ==="
