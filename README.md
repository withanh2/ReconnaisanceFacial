# Reconnaissance Faciale par ACP (Eigenfaces)

Projet CY Tech — reconnaissance de visages par Analyse en Composantes Principales.
Deux points d'entrée : **`App`** (interface graphique JavaFX) et **`Main`** (console).

## Prérequis

- **Java 21**
- **JavaFX 21** et les autres dépendances (EJML, ImageIO) sont déjà dans `lib/`.
- Natives JavaFX fournies pour **Linux x64** (autre OS : voir le JavaFX SDK de ta plateforme).

## Installation

```bash
git clone https://github.com/withanh2/ReconnaisanceFacial.git
cd ReconnaisanceFacial
unzip donnees.zip -d donnees        # décompresse les images dans donnees/
```

## Jar exécutable (rendu)

Construire le jar :

```bash
./build.sh        # compile et produit ReconnaissanceFaciale.jar
```

Lancer le jar (interface graphique) :

```bash
./run.sh          # à exécuter depuis la racine du projet
```

> Le jar contient les classes du projet ; les bibliothèques restent dans `lib/`.
> JavaFX ne peut pas être lancé par simple double-clic : utilisez `run.sh`, qui
> ajoute les modules JavaFX (`--module-path lib/javafx --add-modules …`).
> L'application doit être lancée depuis la racine du projet car elle lit ses
> données en chemins relatifs (`donnees/…`, `sortie_pgm/…`).

## Lancer l'interface graphique (sans jar)

```bash
javac --module-path lib/javafx --add-modules javafx.controls -cp "lib/*" \
      App.java Main.java Image.java Visages.java ListePropre.java ACP.java PreTraitement.java

java --module-path lib/javafx --add-modules javafx.controls -cp ".:lib/*" App
```

## Lancer la version console

```bash
javac -cp "lib/*" Main.java Image.java Visages.java ListePropre.java ACP.java
java  -cp ".:lib/*" Main
```

> Séparateur de classpath : `:` sous Linux/macOS, `;` sous Windows.
