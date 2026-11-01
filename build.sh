#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out; mkdir -p out
find src/main/java -name '*.java' -print0 | xargs -0 javac --release 17 -encoding UTF-8 -d out
cp -R src/main/resources/* out/
jar --create --file MissingHero.jar --main-class com.missinghero.Main -C out .
echo 'Built MissingHero.jar'
