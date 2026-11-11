#!/bin/sh
# build-jar.sh - makes gemora.jar from the source code (needed only after you change Java code).
# Needs JDK 11 or newer. Pictures, pages (webapp) and data do NOT need a new jar.
cd "$(dirname "$0")" || exit 1
rm -rf build && mkdir -p build/classes
javac --release 11 -encoding UTF-8 -cp "lib/*" -d build/classes $(find src -name "*.java") || exit 1
jar cfm gemora.jar manifest.txt -C build/classes .   # manifest.txt = Main-Class + the jars in lib
rm -rf build
echo "gemora.jar is ready"
