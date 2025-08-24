set -euxo pipefail

TMPDIR="${TMPDIR:-$PREFIX/tmp}"
mkdir -p "$TMPDIR"

# helper: inserta un import exacto justo después de la línea 'package ...' (sin duplicar)
ensure_import() {
  f="$1"; imp="$2"
  grep -qxF "$imp" "$f" && return 0
  awk -v imp="$imp" '
    BEGIN{added=0}
    # si la línea es package ... y aún no añadimos, imprimimos package, luego el import
    $0 ~ /^package[ \t]+/ && !added { print; print imp; added=1; next }
    { print }
    END{ if(!added) print imp }  # por si no había línea package (backup)
  ' "$f" > "$TMPDIR/tmp.$$" && mv "$TMPDIR/tmp.$$" "$f"
}

# ---- 0) MainActivity: quita coma suelta ----
sed -i 's/FreyjaScreen(vm = ChatViewModel(),[[:space:]]*)/FreyjaScreen(vm = ChatViewModel())/' \
  app/src/main/java/com/angeluz/freyja/MainActivity.kt || true

# ---- 1) Awareness.kt ----
ensure_import app/src/main/java/com/angeluz/freyja/Awareness.kt "import com.angeluz.freyja.Prefs"

# ---- 2) FreyjaScreen.kt ----
# remapea remote -> data (solo en imports)
sed -i 's/^import[[:space:]]\+com\.angeluz\.freyja\.remote\./import com.angeluz.freyja.data./' \
  app/src/main/java/com/angeluz/freyja/ui/screens/FreyjaScreen.kt || true

ensure_import app/src/main/java/com/angeluz/freyja/ui/screens/FreyjaScreen.kt "import com.angeluz.freyja.ChatViewModel"
ensure_import app/src/main/java/com/angeluz/freyja/ui/screens/FreyjaScreen.kt "import com.angeluz.freyja.data.RetrofitProvider"
ensure_import app/src/main/java/com/angeluz/freyja/ui/screens/FreyjaScreen.kt "import com.angeluz.freyja.data.ChatRequest"

# firma exacta: FreyjaScreen(vm: ChatViewModel)
perl -0777 -pe 's/@Composable\s+fun\s+FreyjaScreen\s*\([^)]*\)/@Composable fun FreyjaScreen(vm: ChatViewModel)/s' \
  -i app/src/main/java/com/angeluz/freyja/ui/screens/FreyjaScreen.kt

# Text(...) ambiguo -> Text(text = ...)
perl -0777 -pe 's/\bText\(\s*("([^"\\]|\\.)*")\s*\)/Text(text = $1)/g' \
  -i app/src/main/java/com/angeluz/freyja/ui/screens/FreyjaScreen.kt
perl -0777 -pe 's/\bText\(\s*if\s*\(([^)]*)\)\s*("([^"\\]|\\.)*")\s*else\s*("([^"\\]|\\.)*")\s*\)/Text(text = (if ($1) $2 else $4))/g' \
  -i app/src/main/java/com/angeluz/freyja/ui/screens/FreyjaScreen.kt

# ---- 3) HybridInvoker.kt ----
# quita import roto de SpeakMode (usaremos el enum del mismo paquete)
sed -i '/^import[[:space:]]\+com\.angeluz\.freyja\.SpeakMode$/d' app/src/main/java/com/angeluz/freyja/HybridInvoker.kt || true
ensure_import app/src/main/java/com/angeluz/freyja/HybridInvoker.kt "import com.angeluz.freyja.Prefs"
ensure_import app/src/main/java/com/angeluz/freyja/HybridInvoker.kt "import kotlinx.coroutines.flow.collectLatest"

# normaliza start()
perl -0777 -pe '
  s/fun\s+start\(\)\s*\{\s*scope\.launch\s*\{\s*.*?\}\s*\}/fun start() {
        scope.launch {
            Prefs.speakModeFlow.collectLatest { mode ->
                if (mode == currentMode) return@collectLatest
                stopInternal()
                currentMode = mode
                when (mode) {
                    SpeakMode.OFF -> Unit
                    SpeakMode.PUSH_TO_TALK -> { /* TODO: iniciar PTT */ }
                    SpeakMode.WAKE_WORD -> { /* TODO: iniciar hotword */ }
                }
            }
        }
    }/s
' -i app/src/main/java/com/angeluz/freyja/HybridInvoker.kt || true

# ---- 4) Commit/push + CI ----
git status --porcelain || true
git add -A
git commit -m "fix(termux-safe): imports y firmas (Awareness/Screen/Hybrid/MainActivity)" || true
git pull --rebase origin main
git push origin HEAD:main

gh workflow run android-ci.yml --repo KingBrutblarg/FreyjaBeta
RUN_ID=$(gh run list --workflow="android-ci.yml" -L 1 --json databaseId --jq '.[0].databaseId' --repo KingBrutblarg/FreyjaBeta); echo "RUN_ID=$RUN_ID"
gh run watch "$RUN_ID" --exit-status --repo KingBrutblarg/FreyjaBeta || true
JOB_ID=$(gh run view "$RUN_ID" --json jobs --jq '.jobs[] | select(.conclusion!="success") | .databaseId' --repo KingBrutblarg/FreyjaBeta | head -n1); echo "JOB_ID=$JOB_ID"
gh run view "$RUN_ID" --log --job "$JOB_ID" --repo KingBrutblarg/FreyjaBeta \
  | sed -n '/> Task :app:compileReleaseKotlin/,/FAILURE:/p' \
  | grep -E '^e: |: error:|Unresolved reference|Cannot infer|must be exhaustive|Overload resolution ambiguity' || true
