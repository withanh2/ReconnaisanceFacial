#!/usr/bin/env bash
# Compile le projet et fabrique le jar exécutable ReconnaissanceFaciale.jar
# Le jar contient uniquement les classes du projet ; les bibliothèques restent
# dans lib/ et sont référencées via le Class-Path du manifeste (manifest.txt).
set -e
cd "$(dirname "$0")"

CP="lib/*:lib/javafx/*"
OUT="build/classes"

echo "==> Nettoyage"
rm -rf build
mkdir -p "$OUT"

echo "==> Compilation"
javac -cp "$CP" -d "$OUT" *.java

echo "==> Création du jar"
jar --create --file ReconnaissanceFaciale.jar --manifest manifest.txt -C "$OUT" .

echo "==> Terminé : ReconnaissanceFaciale.jar"
echo "    Lancer avec : ./run.sh"
