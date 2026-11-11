#!/usr/bin/env bash
# Mengompilasi logika Kotlin (angka, materi, pinyin) menjadi web/js/logic.mjs.
# Butuh kotlinc 2.0.x (zip resmi dari github.com/JetBrains/kotlin/releases). KOTLINC = folder kotlinc.
set -euo pipefail
cd "$(dirname "$0")/.."
KC="${KOTLINC:?Isi KOTLINC dengan folder kotlinc}"
SRC=app/src/main/java/com/nesxha/readit
OUT=$(mktemp -d)
# Langkah 1: sumber Kotlin → klib. Langkah 2: klib → modul JavaScript (ES).
"$KC/bin/kotlinc-js" -Xir-produce-klib-dir -libraries "$KC/lib/kotlin-stdlib-js.klib" \
  -ir-output-dir "$OUT/klib" -ir-output-name logic \
  "$SRC/util/ChineseNumber.kt" "$SRC/util/Pinyin.kt" "$SRC/data/NumberQuiz.kt" "$SRC/data/NumberLessons.kt" web/kotlin/WebLogic.kt
"$KC/bin/kotlinc-js" -Xir-produce-js -Xinclude="$OUT/klib" -libraries "$KC/lib/kotlin-stdlib-js.klib" \
  -ir-output-dir "$OUT/js" -ir-output-name logic -module-kind es -target es2015 -Xir-per-module=false -Xir-dce -Xir-minimized-member-names
mkdir -p web/js
ls -la "$OUT/js"
cp "$OUT/js/logic.mjs" web/js/logic.mjs
echo "Selesai: web/js/logic.mjs"
