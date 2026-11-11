#!/bin/sh
# start.sh - starts Gemora (Mac / Linux). Needs JDK 17 or newer. Then open http://localhost:8080/
cd "$(dirname "$0")" || exit 1
mkdir -p out
javac -encoding UTF-8 -cp "lib/*" -d out $(find src -name "*.java") || exit 1
java -cp "out:lib/*" com.jewellery.Main
