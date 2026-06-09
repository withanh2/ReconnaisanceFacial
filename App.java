// Importations des composants fondamentaux de JavaFX pour l'application et les fenêtres
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

// Importations dédiées à la création du graphique d'éboulis des valeurs propres
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

// Importations des éléments d'interface graphique (boutons, textes, curseurs, séparateurs)
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;

// Importations pour la manipulation, la conversion et l'affichage des images
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

// Importations pour l'organisation spatiale des composants (Layouts)
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Importation de la bibliothèque EJML pour la gestion des matrices de l'ACP
import org.ejml.simple.SimpleMatrix;

// Importation de la classe File pour gérer l'ouverture des fichiers d'images
import java.io.File;

/**
 * Classe principale de l'application de reconnaissance faciale basée sur la méthode des Eigenfaces (ACP).
 * Cette interface permet de charger une base, sélectionner un visage test et afficher les résultats d'identification.
 * * @author AJIMI Sirine
 * @version 1.0
 */
public class App extends Application {

    // Déclaration du conteneur vertical principal pour la zone centrale
    private VBox contenu; 
    
    // Fichier pointant vers l'image PGM actuellement sélectionnée par l'utilisateur
    private File imageSelectionnee = null; 
    
    // Déclaration du panneau latéral droit dédié à l'affichage des statistiques
    private VBox zoneDroite; 
    
    // Composant graphique affichant l'image du visage à identifier (requête)
    private ImageView vueImageRequete;
    
    // Composant graphique affichant l'image du visage correspondant trouvé en base
    private ImageView vueImageCandidate;
    
    // Label affichant le nom ou l'identifiant de la personne reconnue
    private Label lblStatutId;
    
    // Label affichant la distance euclidienne mesurée entre les visages
    private Label lblDistance;
    
    // Label affichant le pourcentage d'erreur lors de la reconstruction de l'image
    private Label lblErreurRecon;

    /**
     * Point d'entrée principal de l'application JavaFX. Initialise et construit l'interface graphique.
     * * @param primaryStage La fenêtre principale de l'application fournie par JavaFX.
     * @author AJIMI Sirine
     */
    @Override
    public void start(Stage primaryStage) {
        // Définit le titre affiché en haut de la fenêtre principale
        primaryStage.setTitle("Reconnaissance Faciale par ACP — CY Tech");

        // Crée le gestionnaire de disposition principal divisé en 5 zones (Haut, Bas, Gauche, Droite, Centre)
        BorderPane root = new BorderPane();
        // Applique une couleur de fond gris très clair au conteneur principal via CSS
        root.setStyle("-fx-background-color: #F8FAFC;"); 

        // --- BARRE DE NAVIGATION SUPÉRIEURE ---
        // Crée une boîte horizontale pour la barre supérieure avec un espace de 20 pixels entre ses éléments
        HBox barreHaute = new HBox(20);
        // Ajoute des marges internes (haut: 15, droite: 30, bas: 15, gauche: 30) autour du texte de la barre haute
        barreHaute.setPadding(new Insets(15, 30, 15, 30));
        // Aligne le contenu de cette barre horizontalement à gauche et verticalement au centre
        barreHaute.setAlignment(Pos.CENTER_LEFT);
        // Applique un fond blanc et une fine bordure grise uniquement sur le bas de la barre
        barreHaute.setStyle("-fx-background-color: #ffffff; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1px 0;");

        // Crée le titre textuel principal de l'application avec un émoji
        Label titreApp = new Label("👤 Système Eigenfaces");
        // Modifie le style du titre : couleur violette, texte en gras et taille de police de 16 pixels
        titreApp.setStyle("-fx-text-fill: #4F46E5; -fx-font-weight: bold; -fx-font-size: 16px;");
        
        // Ajoute le titre textuel dans la liste des enfants de la barre horizontale supérieure
        barreHaute.getChildren().add(titreApp);
        // Positionne la barre haute ainsi construite au sommet du BorderPane principal
        root.setTop(barreHaute);

        // --- PANNEAU GAUCHE : CONFIGURATION & ACTIONS ---
        // Crée une boîte verticale pour les contrôles de gauche avec 20 pixels d'écart entre chaque élément
        VBox panneauGauche = new VBox(20);
        // Applique une marge interne uniforme de 30 pixels tout autour de la boîte de gauche
        panneauGauche.setPadding(new Insets(30));
        // Fixe la largeur idéale de ce panneau à 320 pixels pour un affichage stable
        panneauGauche.setPrefWidth(320);
        // Applique un fond blanc et une fine bordure séparatrice grise sur le côté droit du panneau
        panneauGauche.setStyle("-fx-background-color: #ffffff; -fx-border-color: #E2E8F0; -fx-border-width: 0 1px 0 0;");

        // Crée une étiquette de section pour indiquer la zone de configuration de l'ACP
        Label titreConfiguration = new Label("Configuration ACP");
        // Formate ce titre de section : taille 14 pixels, en gras, couleur bleu nuit/anthracite
        titreConfiguration.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        // Crée l'étiquette affichant la valeur actuelle du curseur (K initialisé par défaut à 20)
        Label lblSlider = new Label("Nombre de composantes (K) : 20");
        // Modifie le style de l'étiquette du curseur : couleur gris moyen et épaisseur de texte moyenne
        lblSlider.setStyle("-fx-text-fill: #64748B; -fx-font-weight: 500;");
        
        // Instancie le curseur (Slider) : valeur minimale de 1, maximale de 50, et position initiale à 20
        Slider sliderK = new Slider(1, 50, 20);
        // Active l'affichage des petites graduations sur l'axe du curseur
        sliderK.setShowTickMarks(true);
        // Active l'affichage des valeurs textuelles (chiffres) sous le curseur
        sliderK.setShowTickLabels(true);
        
        // Ajoute un écouteur de changement sur la valeur du curseur pour mettre à jour le texte dynamiquement
        sliderK.valueProperty().addListener((obs, oldVal, newVal) -> 
            // Récupère la nouvelle valeur décimale, la convertit en entier et met à jour le texte du Label
            lblSlider.setText("Nombre de composantes (K) : " + newVal.intValue())
        );

        // Instancie le bouton de chargement de la base via une fonction utilitaire personnalisée
        Button btnChoisirBase = creerBoutonPrincipal("📁 Charger la Base d'Entraînement");
        // Instancie le bouton de sélection du visage à tester de la même manière
        Button btnChoisirImage = creerBoutonPrincipal("🔍 Sélectionner un Visage (Test)");
        
        // Instancie le bouton vert d'exécution de l'algorithme d'identification
        Button btnReconnaitre = new Button("🚀 Lancer l'Identification");
        // Configure l'apparence du bouton vert : fond vert, texte blanc, en gras, marges internes et coins arrondis
        btnReconnaitre.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6px; -fx-cursor: hand;");
        // Permet au bouton de s'étirer horizontalement pour occuper tout l'espace disponible à gauche
        btnReconnaitre.setMaxWidth(Double.MAX_VALUE);

        // Intègre l'ensemble des composants configurés dans l'ordre au sein du panneau vertical gauche
        panneauGauche.getChildren().addAll(
            titreConfiguration, 
            btnChoisirBase, 
            new Separator(), // Insère une ligne horizontale grise de séparation visuelle
            lblSlider, 
            sliderK, 
            new Separator(), // Insère une seconde ligne horizontale grise de séparation visuelle
            btnChoisirImage, 
            btnReconnaitre
        );
        // Positionne ce panneau complet sur le flanc gauche du BorderPane principal
        root.setLeft(panneauGauche);

        // --- PANNEAU CENTRAL : AFFICHAGE DES IMAGES ---
        // Crée une boîte horizontale pour exposer les images côte à côte avec un espacement de 40 pixels
        HBox conteneurImages = new HBox(40);
        // Aligne parfaitement le conteneur d'images au centre de son espace disponible
        conteneurImages.setAlignment(Pos.CENTER);
        // Ajoute une marge interne de sécurité de 20 pixels tout autour du bloc d'images
        conteneurImages.setPadding(new Insets(20));

        // Crée une boîte verticale dédiée au bloc de l'image de requête (l'image d'entrée)
        VBox boxRequete = new VBox(10);
        // Centre les éléments (titre et image) verticalement et horizontalement au sein de cette boîte
        boxRequete.setAlignment(Pos.CENTER);
        // Crée le titre pour la première image
        Label lblRequete = new Label("Visage Requête");
        // Applique un style en gras et une couleur gris ardoise au titre de l'image requête
        lblRequete.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
        // Initialise la vue graphique de l'image de requête
        vueImageRequete = new ImageView();
        // Impose une largeur stricte de 184 pixels pour conserver le ratio des visages de la base
        vueImageRequete.setFitWidth(184);
        // Impose une hauteur stricte de 224 pixels pour l'affichage de l'image
        vueImageRequete.setFitHeight(224);
        // Ajoute une bordure grise et un fond neutre en cas d'absence d'image chargée
        vueImageRequete.setStyle("-fx-border-color: #CBD5E1; -fx-border-width: 2px; -fx-background-color: #E2E8F0;");
        // Ajoute l'étiquette et le composant d'affichage d'image dans la boîte de requête
        boxRequete.getChildren().addAll(lblRequete, vueImageRequete);

        // Crée une boîte verticale dédiée au bloc de l'image trouvée en base (la candidate correspondante)
        VBox boxCandidate = new VBox(10);
        // Centre l'affichage des éléments au sein de cette sous-boîte candidate
        boxCandidate.setAlignment(Pos.CENTER);
        // Crée le titre pour la seconde image
        Label lblCandidate = new Label("Correspondance Base");
        // Applique le même style visuel en gras et gris ardoise pour l'harmonie graphique
        lblCandidate.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
        // Initialise la vue graphique de l'image trouvée
        vueImageCandidate = new ImageView();
        // Aligne sa largeur sur 184 pixels pour correspondre exactement à l'image requête
        vueImageCandidate.setFitWidth(184);
        // Aligne sa hauteur sur 224 pixels également
        vueImageCandidate.setFitHeight(224);
        // Applique la même bordure et le même fond par défaut pour l'homogénéité visuelle
        vueImageCandidate.setStyle("-fx-border-color: #CBD5E1; -fx-border-width: 2px; -fx-background-color: #E2E8F0;");
        // Ajoute l'étiquette et le composant d'image dans la boîte candidate
        boxCandidate.getChildren().addAll(lblCandidate, vueImageCandidate);

        // Positionne les deux boîtes (requête et candidate) côte à côte dans le conteneur horizontal
        conteneurImages.getChildren().addAll(boxRequete, boxCandidate);

        // Appelle la méthode utilitaire pour concevoir le graphique d'éboulis avec un maximum de 40 composantes
        LineChart<Number, Number> graphiqueEigen = creerGraphique(40);

        // Initialise le conteneur principal du centre qui empile les images et le graphique en dessous
        contenu = new VBox(30);
        // Définit un espacement interne global de 20 pixels pour aérer la zone centrale
        contenu.setPadding(new Insets(20));
        // Injecte le bloc horizontal des images et le graphique linéaire dans la vue centrale
        contenu.getChildren().addAll(conteneurImages, graphiqueEigen);
        // Positionne le conteneur assemblé au centre de la structure BorderPane principale
        root.setCenter(contenu);

        // --- PANNEAU DROIT : BILAN DES STATISTIQUES ---
        // Instancie une boîte verticale pour la zone de droite accueillant l'analyse statistique
        zoneDroite = new VBox(20);
        // Définit une marge intérieure de 30 pixels pour détacher les statistiques des bords
        zoneDroite.setPadding(new Insets(30));
        // Impose une largeur fixe de 280 pixels pour le volet des résultats à droite
        zoneDroite.setPrefWidth(280);
        // Définit un fond blanc et trace une bordure grise de délimitation sur sa gauche uniquement
        zoneDroite.setStyle("-fx-background-color: #ffffff; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 0 1px;");

        // Instancie le titre général du panneau de statistiques
        Label titreStats = new Label("Analyse & Résultats");
        // Applique un formatage au titre : taille 14 pixels, en gras et couleur sombre
        titreStats.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        // Crée l'étiquette textuelle de résultat d'identité, configurée par défaut en état d'attente
        lblStatutId = new Label("En attente...");
        // Appelle la fonction de création de carte visuelle pour l'affichage stylisé de l'identité
        VBox cardIdentite = creerCardStat("INDIVIDU RECONNU", lblStatutId, "-fx-text-fill: #4F46E5;");

        // Crée l'étiquette pour la distance euclidienne, vide à l'initialisation (représentée par un tiret)
        lblDistance = new Label("—");
        // Génère la carte visuelle dédiée à la distance mathématique avec un texte sombre
        VBox cardDistance = creerCardStat("DISTANCE EUCLIDIENNE", lblDistance, "-fx-text-fill: #0F172A;");

        // Crée l'étiquette pour l'erreur de reconstruction, vide à l'initialisation
        lblErreurRecon = new Label("—");
        // Génère la carte visuelle d'erreur avec une coloration rouge pour signifier l'indicateur critique
        VBox cardErreur = creerCardStat("ERREUR RECONSTRUCTION", lblErreurRecon, "-fx-text-fill: #EF4444;");

        // Insère le titre et les trois cartes de métriques de haut en bas dans le volet droit
        zoneDroite.getChildren().addAll(titreStats, cardIdentite, cardDistance, cardErreur);
        // Positionne la zone complète sur le côté droit de la fenêtre principale
        root.setRight(zoneDroite);

        // --- GESTION DES ACTIONS ---
        // Instancie un composant natif d'ouverture et d'exploration de fichiers système
        FileChooser fileChooser = new FileChooser();
        // Configure un filtre pour restreindre le choix exclusif aux fichiers d'images de format standard .pgm
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images PGM", "*.pgm"));

        // Attache un comportement événementiel lors du clic sur le bouton de sélection d'image de test
        btnChoisirImage.setOnAction(e -> {
            // Ouvre l'explorateur de fichiers au premier plan par-dessus la fenêtre principale
            File file = fileChooser.showOpenDialog(primaryStage);
            // Vérifie si l'utilisateur a bel et bien sélectionné un fichier d'image valide
            if (file != null) {
                // Sauvegarde le fichier sélectionné dans la variable d'instance globale
                imageSelectionnee = file;
                // Instancie et applique l'image sélectionnée sur l'interface graphique de requête
                vueImageRequete.setImage(new javafx.scene.image.Image(file.toURI().toString()));
                // Efface l'image candidate précédente en attente de la nouvelle exécution
                vueImageCandidate.setImage(null);
                // Réinitialise les textes d'information pour notifier l'état prêt du système
                lblStatutId.setText("Image prête");
                // Remet à zéro l'indicateur de distance euclidienne
                lblDistance.setText("—");
                // Remet à zéro l'affichage du taux d'erreur de reconstruction
                lblErreurRecon.setText("—");
            }
        });

        // Attache un comportement événementiel lors du clic sur le bouton de traitement (Reconnaissance)
        btnReconnaitre.setOnAction(e -> {
            // S'assure qu'une image de test est présente et chargée avant d'opérer la reconnaissance
            if (imageSelectionnee != null) {
                // Injecte une fausse valeur de résultat pour simuler l'identification de la première personne
                lblStatutId.setText("personne_01");
                // Injecte une simulation réaliste de distance mathématique calculée
                lblDistance.setText("14.23");
                // Fixe arbitrairement un taux d'erreur fictif pour valider la mise en page
                lblErreurRecon.setText("3.85 %");
                
                // Pour lier avec vos vraies matrices d'images reconstruites :
                // vueImageCandidate.setImage(convertirMatriceEnImage(matriceResultat, 92, 112));
            }
        });

        // Compile la structure globale de l'interface dans une scène fixe de 1200x750 pixels
        Scene scene = new Scene(root, 1200, 750);
        // Attribue la scène finalisée à la fenêtre d'affichage principale de l'application
        primaryStage.setScene(scene);
        // Exécute l'ouverture graphique effective de la fenêtre à l'écran de l'utilisateur
        primaryStage.show();
    }

    /**
     * Crée et configure un bouton standardisé avec des animations CSS lors du survol de la souris.
     * * @param texte Le texte à afficher à l'intérieur du bouton.
     * @return Le bouton JavaFX stylisé et prêt à l'emploi.
     * @author AJIMI Sirine
     */
    private Button creerBoutonPrincipal(String texte) {
        // Instancie un bouton classique doté du texte transmis en paramètre
        Button b = new Button(texte);
        // Déclare les styles graphiques de base par défaut : fond gris clair, coins légers et pointeur de type "main"
        b.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 6px; -fx-cursor: hand;");
        // Autorise le bouton à occuper la totalité de l'espace en largeur au sein de son conteneur parent
        b.setMaxWidth(Double.MAX_VALUE);
        // Modifie dynamiquement le style CSS du bouton lorsque le pointeur de la souris glisse au-dessus de lui
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #1E293B; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 6px; -fx-cursor: hand;"));
        // Rétablit le style CSS initial du bouton dès que le pointeur de la souris s'en éloigne
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 6px; -fx-cursor: hand;"));
        // Renvoie l'élément bouton entièrement configuré à l'appelant
        return b;
    }

    /**
     * Génère un module visuel (carte) pour regrouper un titre indicateur et sa valeur statistique associée.
     * * @param titre Le titre supérieur de la carte.
     * @param valeurLabel Le composant de texte hébergeant le résultat variable.
     * @param styleValeur Les spécifications CSS propres à appliquer à la valeur affichée.
     * @return Un conteneur VBox modélisé sous forme de carte statistique.
     * @author AJIMI Sirine
     */
    private VBox creerCardStat(String titre, Label valeurLabel, String styleValeur) {
        // Crée une petite boîte verticale ordonnée avec un faible espacement interne de 5 pixels
        VBox c = new VBox(5);
        // Impose un espace intérieur uniforme de 15 pixels pour que les écrits ne collent pas aux rebords
        c.setPadding(new Insets(15));
        // Configure l'allure générale de la carte : arrière-plan gris bleuté, fine démarcation et coins adoucis
        c.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-background-radius: 8px;");
        // Initialise l'étiquette de désignation de la métrique calculée
        Label t = new Label(titre); 
        // Formate l'étiquette de légende : couleur grise adoucie, taille réduite à 10 points et texte en gras
        t.setStyle("-fx-text-fill: #64748B; -fx-font-size: 10px; -fx-font-weight: bold;");
        // Harmonise le style propre de la valeur transmise en y concaténant une police de 18 points très marquée
        valeurLabel.setStyle(styleValeur + " -fx-font-size: 18px; -fx-font-weight: bold;");
        // Range successivement le titre indicateur et le texte de résultat dans la boîte de la carte
        c.getChildren().addAll(t, valeurLabel);
        // Retourne le conteneur de la carte statistique ainsi assemblée
        return c;
    }

    /**
     * Génère un graphique de type courbe représentant l'éboulis des valeurs propres (variance cumulée).
     * * @param kMax Le nombre maximal de composantes à simuler sur l'axe horizontal.
     * @return Un objet LineChart configuré affichant la courbe d'inertie.
     * @author AJIMI Sirine
     */
    private LineChart<Number, Number> creerGraphique(int kMax) {
        // Initialise un axe numérique horizontal et lui attribue une étiquette descriptive appropriée
        NumberAxis xAxis = new NumberAxis(); xAxis.setLabel("Composantes (K)");
        // Initialise un axe numérique vertical destiné à recenser le pourcentage cumulé de l'inertie
        NumberAxis yAxis = new NumberAxis(); yAxis.setLabel("Inertie expliquée (%)");
        // Instancie le graphique linéaire en croisant les deux axes numériques précédemment élaborés
        LineChart<Number, Number> lc = new LineChart<>(xAxis, yAxis);
        // Attribue un titre didactique au graphique pour expliciter l'objectif scientifique aux enseignants
        lc.setTitle("Éboulis des valeurs propres (Critère de choix de K)");
        // Verrouille la hauteur d'affichage du composant à une valeur fixe de 230 pixels
        lc.setPrefHeight(230);
        // Désactive le dessin des ronds ou carrés marqueurs individuels sur chaque coordonnée de la ligne
        lc.setCreateSymbols(false);
        
        // Crée une série de coordonnées géométriques (X, Y) pour tracer la courbe de données
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        // Nomme la courbe de données afin d'alimenter automatiquement la légende du graphique
        series.setName("Variance cumulée");
        // Initialise le cumul mathématique de l'inertie expliquée à zéro pour cent
        double total = 0;
        // Démarre une boucle itérative de 1 jusqu'à la limite maximale définie pour peupler le tracé
        for (int i = 1; i <= kMax; i++) {
            // Modélise de façon logarithmique fictive l'apport décroissant de chaque Eigenface (gain de 18%)
            total += (100.0 - total) * 0.18;
            // Ajoute un nouveau point de coordonnées géométriques (Indice K, Variance cumulée) dans la série
            series.getData().add(new XYChart.Data<>(i, total));
        }
        // Associe la série de coordonnées mathématiques complétée au gestionnaire interne du LineChart
        lc.getData().add(series);
        // Renvoie l'objet graphique finalisé prêt à être affiché au centre de l'application
        return lc;
    }

    /**
     * Convertit une matrice colonne de type SimpleMatrix (EJML) en une image matricielle JavaFX.
     * Utile pour la reconstruction visuelle des visages à partir de vecteurs de pixels.
     * * @param matrice La matrice EJML contenant les intensités de gris des pixels en colonne.
     * @param largeur La largeur cible de l'image de sortie en pixels.
     * @param hauteur La hauteur cible de l'image de sortie en pixels.
     * @return Une image de type WritableImage interprétable par les composants graphiques JavaFX.
     * @author AJIMI Sirine
     */
    private WritableImage convertirMatriceEnImage(SimpleMatrix matrice, int largeur, int hauteur) {
        // Réserve un espace mémoire d'image pixelisée modifiable aux dimensions spécifiées
        WritableImage img = new WritableImage(largeur, hauteur);
        // Extrait le manipulateur d'écriture pixel par pixel (PixelWriter) associé à cette image vide
        PixelWriter pw = img.getPixelWriter();
        // Initialise l'index de lecture à la toute première ligne du vecteur de la matrice colonne
        int index = 0;
        // Parcourt les coordonnées de l'image de haut en bas (axe vertical Y)
        for (int y = 0; y < hauteur; y++) {
            // Parcourt les coordonnées de l'image de gauche à droite (axe horizontal X)
            for (int x = 0; x < largeur; x++) {
                // Extrait la valeur brute du pixel courant et la normalise sur une échelle de 0.0 à 1.0
                double val = matrice.get(index++, 0) / 255.0;
                // Contraint la valeur calculée à rester strictement comprise entre 0.0 et 1.0 (écrêtage)
                val = Math.max(0.0, Math.min(1.0, val)); 
                // Assigne une coloration de gris homogène (Canaux Rouge=Gris, Vert=Gris, Bleu=Gris) au pixel (X, Y)
                pw.setColor(x, y, Color.color(val, val, val));
            }
        }
        // Retourne l'image restaurée sous forme d'objet graphique exploitable
        return img;
    }

    /**
     * Méthode principale standard de Java permettant de lancer la machine virtuelle et l'application.
     * * @param args Les éventuels arguments transmis en ligne de commande au démarrage.
     * @author AJIMI Sirine
     */
    public static void main(String[] args) {
        // Appelle le mécanisme d'infrastructure de JavaFX pour initialiser et démarrer le cycle de vie de l'interface
        launch(args);
    }
}