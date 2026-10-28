#!/usr/bin/env bash
# Kompilasi dan jalankan uji logika domain tanpa Android SDK.
# Butuh JDK 17+ dan Gradle terpasang (jar Kotlin compiler diambil dari folder lib Gradle).
# Pakai: tools/check-domain.sh [path-ke-gradle-lib]
set -euo pipefail
LIB="${1:-$(dirname "$(dirname "$(readlink -f "$(which gradle)")")")/lib}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$(mktemp -d)"
CP="$LIB/kotlin-compiler-embeddable-2.0.20.jar:$LIB/kotlin-stdlib-2.0.20.jar:$LIB/kotlin-script-runtime-2.0.20.jar:$LIB/kotlin-reflect-2.0.20.jar:$LIB/kotlin-daemon-embeddable-2.0.20.jar:$LIB/trove4j-1.0.20200330.jar:$LIB/annotations-24.0.1.jar:$LIB/kotlinx-coroutines-core-jvm-1.6.4.jar"
java -cp "$CP" org.jetbrains.kotlin.cli.jvm.K2JVMCompiler \
  -no-stdlib -no-reflect -jvm-target 17 -nowarn \
  -cp "$LIB/kotlin-stdlib-2.0.20.jar" \
  -d "$OUT" \
  "$ROOT"/app/src/main/java/id/fintrack/app/domain/*.kt "$ROOT/tools/DomainCheck.kt"
java -cp "$OUT:$LIB/kotlin-stdlib-2.0.20.jar" DomainCheckKt
trap 'rm -r -- "$OUT"' EXIT
