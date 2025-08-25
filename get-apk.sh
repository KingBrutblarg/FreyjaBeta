#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

REPO="KingBrutblarg/FreyjaBeta"
OUTDIR="$PWD/artifact_out"

echo "→ Comprobando autenticación de gh…"
gh auth status >/dev/null || { echo "Necesitas iniciar sesión: gh auth login"; exit 1; }

mkdir -p "$OUTDIR"
rm -rf "$OUTDIR"/* || true

echo "→ Buscando el último run del workflow…"
RUN_ID="$(gh run list --workflow="android-ci.yml" -R "$REPO" -L 1 --json databaseId,status,conclusion -q '.[0].databaseId')"
if [ -z "${RUN_ID:-}" ]; then
  echo "No encontré runs. ¿Se ejecutó el workflow?"
  exit 1
fi
echo "RUN_ID=$RUN_ID"

echo "→ Descargando artifacts…"
gh run download "$RUN_ID" -R "$REPO" -D "$OUTDIR"

echo "→ Descomprimiendo si hay ZIPs…"
shopt -s nullglob
for z in "$OUTDIR"/*.zip; do
  unzip -o "$z" -d "$OUTDIR" >/dev/null
done

echo "→ Buscando APK…"
APK="$(find "$OUTDIR" -type f -iname '*.apk' -printf '%T@ %p\n' | sort -n | awk 'END{print $2}')"
if [ -z "${APK:-}" ]; then
  echo "No se encontró ningún .apk en $OUTDIR"
  exit 1
fi
echo "APK encontrado: $APK"

# Intento 1: instalar silencioso (puede fallar en algunos dispositivos)
if command -v pm >/dev/null 2>&1; then
  echo "→ Intentando instalar con pm install -r…"
  if pm install -r "$APK"; then
    echo "✅ Instalado con pm."
    exit 0
  else
    echo "⚠️  'pm install' no funcionó; intentaré abrir el instalador del sistema."
  fi
fi

# Intento 2: abrir instalador del sistema (interactivo)
if command -v termux-open >/dev/null 2>&1; then
  echo "→ Abriendo el APK con el instalador (termux-open)…"
  termux-open "$APK"
  echo "✅ Si ves el instalador, confirma la instalación en la UI."
  exit 0
fi

# Intento 3: mover a Descargas y abrir con 'am'
echo "→ Preparando almacenamiento compartido…"
termux-setup-storage >/dev/null 2>&1 || true
DEST="${HOME}/storage/downloads/$(basename "$APK")"
cp -f "$APK" "$DEST"
echo "APK copiado a: $DEST"
am start -a android.intent.action.VIEW -d "file://$DEST" -t "application/vnd.android.package-archive" >/dev/null 2>&1 || true
echo "✅ Abierto con el instalador del sistema (si no se abre, toca el APK en Descargas)."
