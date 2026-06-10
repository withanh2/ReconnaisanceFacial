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

## Lancer l'interface graphique

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
