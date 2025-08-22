#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

# Usage:
#   ./scripts/release_tag.sh                # por defecto: bump "patch"
#   ./scripts/release_tag.sh minor          # bump minor
#   ./scripts/release_tag.sh major          # bump major
#   ./scripts/release_tag.sh v1.4.0         # fija versión exacta
#   ./scripts/release_tag.sh --dry-run ...  # muestra sin ejecutar

DRY_RUN=0
if [[ "${1:-}" == "--dry-run" ]]; then
  DRY_RUN=1
  shift || true
fi

BUMP="${1:-patch}"   # patch | minor | major | vX.Y.Z

# --- helpers ---
err() { echo "❌ $*" >&2; exit 1; }
run() { if [[ $DRY_RUN -eq 1 ]]; then echo "DRY ▸ $*"; else eval "$@"; fi }

# --- prechecks ---
git rev-parse --is-inside-work-tree >/dev/null 2>&1 || err "No es un repo git aquí."
CURRENT_BRANCH="$(git rev-parse --abbrev-ref HEAD)"
[[ "$CURRENT_BRANCH" == "main" ]] || echo "⚠️ Estás en rama '$CURRENT_BRANCH' (no main). Continuo igual…"

# debe estar limpio
if [[ -n "$(git status --porcelain)" ]]; then
  err "Tu árbol tiene cambios sin commit. Haz commit/stash antes de publicar un tag."
fi

echo "➤ trayendo tags remotos…"
git fetch --tags --quiet || true

# último tag tipo vX.Y.Z
LAST_TAG="$(git tag --list 'v[0-9]*.[0-9]*.[0-9]*' --sort=-v:refname | head -n1)"
if [[ -z "$LAST_TAG" ]]; then
  LAST_TAG="v1.0.0"   # punto de arranque si no hay tags
  BASE="0.0.0"
else
  BASE="${LAST_TAG#v}"
fi

major="${BASE%%.*}"; rest="${BASE#*.}"
minor="${rest%%.*}"; patch="${rest#*.}"

next_tag=""
if [[ "$BUMP" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  next_tag="$BUMP"
elif [[ "$BUMP" == "major" ]]; then
  next_tag="v$((major+1)).0.0"
elif [[ "$BUMP" == "minor" ]]; then
  next_tag="v$major.$((minor+1)).0"
else
  # patch (default)
  next_tag="v$major.$minor.$((patch+1))"
fi

# evitar duplicado
if git tag --list | grep -qx "$next_tag"; then
  err "El tag $next_tag ya existe."
fi

echo "🧭 Último tag: $LAST_TAG"
echo "🏷️  Nuevo tag: $next_tag"
echo

# changelog corto desde último tag si existe en el repo
if git rev-parse "$LAST_TAG" >/dev/null 2>&1; then
  CHANGELOG="$(git log --pretty=format:'- %s' ${LAST_TAG}..HEAD)"
else
  CHANGELOG="$(git log --pretty=format:'- %s' --max-count=20)"
fi
[[ -z "$CHANGELOG" ]] && CHANGELOG="- Primera publicación"

echo "📝 Cambios:"
echo "$CHANGELOG"
echo

# confirmar (si no es dry-run)
if [[ $DRY_RUN -eq 0 ]]; then
  read -p "¿Crear y empujar tag ${next_tag}? [S/n] " ans
  ans="${ans:-S}"
  [[ "$ans" =~ ^[sS]$ ]] || err "Cancelado por usuario."
fi

# crear tag anotado y push
run "git tag -a \"$next_tag\" -m \"Release $next_tag\""
run "git push origin \"$next_tag\""

echo
echo "✨ Listo. Se disparará el workflow 'Android Release Build' para $next_tag."
echo "👉 Revisa GitHub → Actions → Artifact 'freyja-release.apk' cuando termine."
