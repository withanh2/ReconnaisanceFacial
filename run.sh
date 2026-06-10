#!/usr/bin/env bash
# Lance l'application JavaFX à partir du jar exécutable.
# IMPORTANT : à lancer depuis la racine du projet (ce script s'y place
# automatiquement) car l'application lit ses données en chemins relatifs
# (donnees/..., sortie_pgm/...).
set -e
cd "$(dirname "$0")"

java --module-path lib/javafx \
     --add-modules javafx.controls,javafx.graphics,javafx.base \
     -jar ReconnaissanceFaciale.jar
