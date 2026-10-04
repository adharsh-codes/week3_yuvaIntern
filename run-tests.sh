#!/usr/bin/env bash
# Runs the suite WITHOUT Maven, using the JUnit Console Launcher.
# Usage: ./run-tests.sh [path/to/junit-platform-console-standalone.jar]
set -e
JAR="${1:-/usr/share/java/junit-platform-console-standalone.jar}"
OUT=build
rm -rf "$OUT" && mkdir -p "$OUT/main" "$OUT/test"
javac -d "$OUT/main" src/main/java/com/inventory/*.java
javac -cp "$JAR:$OUT/main" -d "$OUT/test" src/test/java/com/inventory/*.java
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -jar "$JAR" execute -cp "$OUT/main:$OUT/test" --scan-classpath "$OUT/test" \
     --details=tree --disable-banner --disable-ansi-colors
