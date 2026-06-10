import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.ejml.simple.SimpleMatrix;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Interface de reconnaissance faciale par Eigenfaces (ACP), organisée en deux pages :
 * une page d'identification d'un visage et une page de synthèse des données de l'ACP.
 * @author AJIMI Sirine
 * @version 2.0
 */
public class App extends Application {

    private static final String CHEMIN_BASE = "donnees/donnee/reference";
    private static final String CHEMIN_CONNUS = "donnees/donnee/test/connus";
    private static final int LARGEUR = 92;
    private static final int HAUTEUR = 112;

    // --- Modèle (calculé une fois, en tâche de fond) ---
    private List<Image> baseImages;
    private Visages visages;
    private ACP acp;
    private ListePropre listepropre;
    private double seuil1, seuil2, seuil3;
    private volatile boolean modelePret = false;

    // --- Navigation ---
    private BorderPane root;
    private Button btnNav;
    private Node pageIdentification;
    private Node pageACP;
    private int pageActuelle = 1;

    // --- Page identification ---
    private File imageSelectionnee = null;
    private ImageView vueImageRequete;
    private ImageView vueImageCandidate;
    private Label lblGalerieTitre;
    private FlowPane galeriePersonne;
    private Button btnReconnaitre;
    private Label lblStatutId;
    private Label lblDistance;
    private Label lblErreurRecon;
    private Label lblTestT2;
    private Label lblTestRecon;
    private Label lblTestDist;
    private Label lblVerdict;
    private Label lblEtatModele;

    // --- Page ACP ---
    private Label lblNbImages;
    private Label lblNbPersonnes;
    private Label lblTaille;
    private Label lblNbComposantes;
    private Label lblTaux;
    private Label lblSeuil1;
    private Label lblSeuil2;
    private Label lblSeuil3;
    private LineChart<Number, Number> grapheInertie;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Reconnaissance Faciale par ACP — CY Tech");

        root = new BorderPane();
        root.setStyle("-fx-background-color: #FFFFFF;");
        root.setTop(construireBarreHaute());

        pageIdentification = construirePageIdentification(primaryStage);
        pageACP = construirePageACP();
        afficherPage(1);

        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.show();

        chargerModele();
    }

    // ----------------------------------------------------------------------
    // BARRE SUPÉRIEURE + NAVIGATION
    // ----------------------------------------------------------------------

    private HBox construireBarreHaute() {
        HBox barre = new HBox(20);
        barre.setPadding(new Insets(15, 30, 15, 30));
        barre.setAlignment(Pos.CENTER_LEFT);
        barre.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-width: 0 0 1px 0;");

        Label titreApp = new Label("Système Eigenfaces");
        titreApp.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;");

        btnNav = new Button();
        btnNav.setStyle("-fx-background-color: #EEEEEE; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 4px; -fx-cursor: hand;");
        btnNav.setOnAction(e -> afficherPage(pageActuelle == 1 ? 2 : 1));

        barre.getChildren().addAll(titreApp, btnNav);
        return barre;
    }

    private void afficherPage(int p) {
        pageActuelle = p;
        if (p == 1) {
            root.setCenter(pageIdentification);
            btnNav.setText("Données ACP  →");
        } else {
            root.setCenter(pageACP);
            btnNav.setText("←  Identification");
        }
    }

    // ----------------------------------------------------------------------
    // PAGE 1 : IDENTIFICATION
    // ----------------------------------------------------------------------

    private Node construirePageIdentification(Stage primaryStage) {
        BorderPane page = new BorderPane();

        // --- Panneau gauche : actions ---
        VBox panneauGauche = new VBox(20);
        panneauGauche.setPadding(new Insets(30));
        panneauGauche.setPrefWidth(320);
        panneauGauche.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-width: 0 1px 0 0;");

        Label titre = new Label("Identification");
        titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        Button btnChoisirImage = creerBoutonPrincipal("Sélectionner un visage (test)");

        btnReconnaitre = new Button("Lancer l'identification");
        btnReconnaitre.setStyle("-fx-background-color: #222222; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 4px; -fx-cursor: hand;");
        btnReconnaitre.setMaxWidth(Double.MAX_VALUE);
        btnReconnaitre.setDisable(true);

        lblEtatModele = new Label("Chargement du modèle…");
        lblEtatModele.setWrapText(true);
        lblEtatModele.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");

        panneauGauche.getChildren().addAll(titre, btnChoisirImage, new Separator(), btnReconnaitre, lblEtatModele);
        page.setLeft(panneauGauche);

        // --- Centre : les deux images en haut, la galerie de la personne en dessous ---
        HBox conteneurImages = new HBox(40);
        conteneurImages.setAlignment(Pos.CENTER);
        vueImageRequete = creerVueImage();
        vueImageCandidate = creerVueImage();
        conteneurImages.getChildren().addAll(
            creerBlocImage("Visage Requête", vueImageRequete),
            creerBlocImage("Visage le plus proche", vueImageCandidate)
        );

        lblGalerieTitre = new Label();
        lblGalerieTitre.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        galeriePersonne = new FlowPane(15, 15);
        galeriePersonne.setAlignment(Pos.CENTER);

        VBox centre = new VBox(25);
        centre.setPadding(new Insets(30, 20, 20, 20));
        centre.setAlignment(Pos.TOP_CENTER);
        centre.getChildren().addAll(conteneurImages, new Separator(), lblGalerieTitre, galeriePersonne);
        page.setCenter(centre);

        // --- Panneau droit : résultats ---
        VBox panneauDroit = new VBox(15);
        panneauDroit.setPadding(new Insets(30));
        panneauDroit.setPrefWidth(320);
        panneauDroit.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-width: 0 0 0 1px;");

        Label titreStats = new Label("Résultats");
        titreStats.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        lblStatutId = new Label("—");
        lblDistance = new Label("—");
        lblErreurRecon = new Label("—");
        lblTestT2 = new Label("—");
        lblTestRecon = new Label("—");
        lblTestDist = new Label("—");
        lblVerdict = new Label("—");

        panneauDroit.getChildren().addAll(
            titreStats,
            creerCardStat("PERSONNE RECONNUE", lblStatutId),
            creerCardStat("DISTANCE EUCLIDIENNE", lblDistance),
            creerCardStat("ERREUR RECONSTRUCTION", lblErreurRecon),
            creerCardRobustesse(),
            creerCardStat("VERDICT", lblVerdict)
        );
        page.setRight(panneauDroit);

        // --- Actions ---
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images PGM", "*.pgm"));

        btnChoisirImage.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file == null) {
                return;
            }
            // Réinitialisation de l'affichage des résultats
            vueImageCandidate.setImage(null);
            galeriePersonne.getChildren().clear();
            lblGalerieTitre.setText("");
            lblStatutId.setText("—");
            lblDistance.setText("—");
            lblErreurRecon.setText("—");
            lblTestT2.setText("—");
            lblTestRecon.setText("—");
            lblTestDist.setText("—");

            // Vérification de conformité dès le choix de l'image
            try {
                new PreTraitement(LARGEUR, HAUTEUR).verifierConformite(file.getPath());
            } catch (Exception ex) {
                imageSelectionnee = null;
                vueImageRequete.setImage(null);
                btnReconnaitre.setDisable(true);
                lblVerdict.setText("Image non conforme : " + ex.getMessage());
                return;
            }

            // Image conforme : on l'affiche et on autorise l'identification
            imageSelectionnee = file;
            afficherPGM(vueImageRequete, new Image(file.getPath()));
            btnReconnaitre.setDisable(!modelePret);
            lblVerdict.setText("Prêt à identifier");
        });

        btnReconnaitre.setOnAction(e -> lancerIdentification());

        return page;
    }

    private void lancerIdentification() {
        if (!modelePret || imageSelectionnee == null) {
            return;
        }

        Image img = new Image(imageSelectionnee.getPath());
        Visages vTest = new Visages(img, visages);
        SimpleMatrix centre = vTest.getImageAanalyser();

        // Identification : indice du visage le plus proche + distance
        double[] res = ACP.identification(acp.getOmega(), centre, acp.getTabEingenface(), acp.getNbValeurPropre());
        int indice = (int) Math.round(res[0]);
        double distance = res[1];
        Image proche = baseImages.get(indice);

        // Erreur de reconstruction (critère 2) et statistique T² (critère 3)
        double[] J = new double[centre.numRows()];
        for (int i = 0; i < J.length; i++) {
            J[i] = centre.get(i, 0);
        }
        double erreur = Main.calculerErreur(J, Main.reconstruire(J, acp.getTabEingenface()));
        double t2 = Main.calculerT2(centre, acp, listepropre);

        // Décision combinée (cf. robustesse) : cohérence (T² + reconstruction) puis distance
        boolean coherent = (t2 < seuil3) && (erreur < seuil2);
        boolean reconnu = coherent && (distance < seuil1);

        lblDistance.setText(String.format("%.2f", distance));
        lblErreurRecon.setText(String.format("%.2f", erreur));

        // Détail de la robustesse : chaque test avec son résultat
        lblTestT2.setText(formatTest("Cohérence T²", t2, seuil3, t2 < seuil3, 3));
        lblTestRecon.setText(formatTest("Reconstruction", erreur, seuil2, erreur < seuil2, 2));
        lblTestDist.setText(formatTest("Distance min", distance, seuil1, distance < seuil1, 2));

        if (reconnu) {
            // Personne reconnue : on affiche le visage le plus proche et ses photos de référence
            afficherPGM(vueImageCandidate, proche);
            remplirGaleriePersonne(proche.getIdPersonne());
            lblStatutId.setText(proche.getIdPersonne());
            lblVerdict.setText("Reconnu");
        } else {
            // Personne inconnue : on n'affiche pas de visage le plus proche
            vueImageCandidate.setImage(null);
            galeriePersonne.getChildren().clear();
            lblGalerieTitre.setText("");
            lblStatutId.setText("Inconnu");
            lblVerdict.setText("Personne inconnue");
        }
    }

    /** Affiche toutes les photos de référence de la personne dans la galerie sous les deux visages. */
    private void remplirGaleriePersonne(String idPersonne) {
        galeriePersonne.getChildren().clear();
        lblGalerieTitre.setText("Photos de référence de « " + idPersonne + " »");
        for (Image im : baseImages) {
            if (im.getIdPersonne().equals(idPersonne)) {
                ImageView v = new ImageView(convertirMatriceEnImage(im.getMatrix(), LARGEUR, HAUTEUR));
                v.setFitWidth(76);
                v.setFitHeight(92);
                v.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1px;");
                galeriePersonne.getChildren().add(v);
            }
        }
    }

    // ----------------------------------------------------------------------
    // PAGE 2 : DONNÉES DE L'ACP
    // ----------------------------------------------------------------------

    private Node construirePageACP() {
        VBox page = new VBox(25);
        page.setPadding(new Insets(40));

        Label titre = new Label("Données de l'ACP");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        lblNbImages = new Label("…");
        lblNbPersonnes = new Label("…");
        lblTaille = new Label("…");
        lblNbComposantes = new Label("…");
        lblTaux = new Label("…");
        lblSeuil1 = new Label("…");
        lblSeuil2 = new Label("…");
        lblSeuil3 = new Label("…");

        GridPane grille = new GridPane();
        grille.setHgap(20);
        grille.setVgap(20);
        grille.add(creerCardStat("NOMBRE D'IMAGES", lblNbImages), 0, 0);
        grille.add(creerCardStat("NOMBRE DE PERSONNES", lblNbPersonnes), 1, 0);
        grille.add(creerCardStat("COMPOSANTES GARDÉES (K)", lblNbComposantes), 0, 1);
        grille.add(creerCardStat("TAUX D'INERTIE CONSERVÉ", lblTaux), 1, 1);
        grille.add(creerCardStat("SEUIL CRITÈRE 1 — DISTANCE (milieu connus / inconnus)", lblSeuil1), 0, 2);
        grille.add(creerCardStat("SEUIL CRITÈRE 2 — RECONSTRUCTION (95e percentile)", lblSeuil2), 1, 2);
        grille.add(creerCardStat("SEUIL CRITÈRE 3 — T² (milieu connus / inconnus)", lblSeuil3), 0, 3);
        grille.add(creerCardStat("TAILLE DES IMAGES", lblTaille), 1, 3);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Nombre de composantes (K)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Inertie cumulée (%)");
        grapheInertie = new LineChart<>(xAxis, yAxis);
        grapheInertie.setTitle("Inertie cumulée en fonction du nombre de composantes");
        grapheInertie.setCreateSymbols(false);
        grapheInertie.setMinWidth(420);
        grapheInertie.setPrefHeight(420);

        HBox corps = new HBox(30);
        corps.setAlignment(Pos.TOP_LEFT);
        corps.getChildren().addAll(grille, grapheInertie);
        HBox.setHgrow(grapheInertie, Priority.ALWAYS);

        page.getChildren().addAll(titre, corps);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #FFFFFF; -fx-background-color: #FFFFFF;");
        return scroll;
    }

    private void remplirStatsACP() {
        int k = acp.getNbValeurPropre();
        lblNbImages.setText(String.valueOf(baseImages.size()));
        lblNbPersonnes.setText(String.valueOf(compterPersonnes(baseImages)));
        int nbPixels = baseImages.get(0).getMatrix().numRows();
        lblTaille.setText(LARGEUR + " × " + HAUTEUR + " px (" + nbPixels + " pixels)");
        lblNbComposantes.setText(String.valueOf(k));
        lblTaux.setText(String.format("%.1f %%", tauxInertie(k)));
        lblSeuil1.setText(String.format("%.2f", seuil1));
        lblSeuil2.setText(String.format("%.2f", seuil2));
        lblSeuil3.setText(String.format("%.4f", seuil3));
        remplirGrapheInertie();
    }

    /** Trace l'inertie cumulée (%) en fonction du nombre de composantes K, avec un repère à 95%. */
    private void remplirGrapheInertie() {
        double[] vp = listepropre.valeurPropreTrie;
        double total = 0;
        for (double v : vp) {
            total += Math.max(v, 0);
        }

        XYChart.Series<Number, Number> courbe = new XYChart.Series<>();
        courbe.setName("Inertie cumulée");
        double cumul = 0;
        for (int i = 0; i < vp.length; i++) {
            cumul += Math.max(vp[i], 0);
            courbe.getData().add(new XYChart.Data<>(i + 1, total == 0 ? 0 : cumul / total * 100.0));
        }

        XYChart.Series<Number, Number> repere95 = new XYChart.Series<>();
        repere95.setName("Seuil 95%");
        repere95.getData().add(new XYChart.Data<>(1, 95));
        repere95.getData().add(new XYChart.Data<>(vp.length, 95));

        grapheInertie.getData().clear();
        grapheInertie.getData().add(courbe);
        grapheInertie.getData().add(repere95);
    }

    private int compterPersonnes(List<Image> images) {
        Set<String> personnes = new HashSet<>();
        for (Image im : images) {
            personnes.add(im.getIdPersonne());
        }
        return personnes.size();
    }

    /** Part de l'inertie expliquée par les K composantes retenues (somme des K plus grandes valeurs propres / somme totale). */
    private double tauxInertie(int k) {
        double[] vp = listepropre.valeurPropreTrie;
        double total = 0;
        double gardee = 0;
        for (int i = 0; i < vp.length; i++) {
            double val = Math.max(vp[i], 0);
            total += val;
            if (i < k) {
                gardee += val;
            }
        }
        return total == 0 ? 0 : gardee / total * 100.0;
    }

    // ----------------------------------------------------------------------
    // CHARGEMENT DU MODÈLE (tâche de fond)
    // ----------------------------------------------------------------------

    private void chargerModele() {
        new Thread(() -> {
            try {
                List<Image> base = Main.chargerImages(CHEMIN_BASE);
                Visages v = new Visages(base);
                ACP a = new ACP(v);
                ListePropre lp = ACP.calculerValeurPropre(v.getMatrixD());
                double s1 = Main.calculSeuilCritereTeta(v, a);
                double s2 = calculerSeuilReconstruction(v, a);
                double s3 = Main.calculSeuilCritereT2(v, a);

                this.baseImages = base;
                this.visages = v;
                this.acp = a;
                this.listepropre = lp;
                this.seuil1 = s1;
                this.seuil2 = s2;
                this.seuil3 = s3;
                this.modelePret = true;

                Platform.runLater(() -> {
                    remplirStatsACP();
                    lblEtatModele.setText("Modèle prêt (" + base.size() + " images).");
                    btnReconnaitre.setDisable(false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> lblEtatModele.setText("Erreur de chargement : " + ex.getMessage()));
            }
        }, "chargement-modele").start();
    }

    /** Seuil du critère 2 : 95e percentile des erreurs de reconstruction des visages connus. */
    private double calculerSeuilReconstruction(Visages visages, ACP acp) {
        List<Image> connus = Main.chargerImages(CHEMIN_CONNUS);
        double[][] validation = new double[connus.size()][];
        for (int k = 0; k < connus.size(); k++) {
            SimpleMatrix centre = new Visages(connus.get(k), visages).getImageAanalyser();
            double[] vecteur = new double[centre.numRows()];
            for (int p = 0; p < vecteur.length; p++) {
                vecteur[p] = centre.get(p, 0);
            }
            validation[k] = vecteur;
        }
        double[] erreurs = Main.calculerErreurValidation(validation, acp.getTabEingenface());
        return Main.calculerSeuil(erreurs, 95.0);
    }

    // ----------------------------------------------------------------------
    // OUTILS UI
    // ----------------------------------------------------------------------

    private Button creerBoutonPrincipal(String texte) {
        Button b = new Button(texte);
        b.setStyle("-fx-background-color: #EEEEEE; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 4px; -fx-cursor: hand;");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #DDDDDD; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 4px; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #EEEEEE; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 4px; -fx-cursor: hand;"));
        return b;
    }

    /** Carte détaillant les 3 tests de la décision combinée (robustesse). */
    private VBox creerCardRobustesse() {
        VBox c = new VBox(6);
        c.setPadding(new Insets(15));
        c.setPrefWidth(300);
        c.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #CCCCCC; -fx-background-radius: 8px;");
        Label t = new Label("DÉTAIL DE LA DÉCISION (ROBUSTESSE)");
        t.setWrapText(true);
        t.setMaxWidth(270);
        t.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px; -fx-font-weight: bold;");
        for (Label l : new Label[]{lblTestT2, lblTestRecon, lblTestDist}) {
            l.setStyle("-fx-text-fill: #000000; -fx-font-size: 12px;");
        }
        c.getChildren().addAll(t, lblTestT2, lblTestRecon, lblTestDist);
        return c;
    }

    /** Formate une ligne de test : "nom : valeur < seuil  ✓" (ou ≥ et ✗ si le test échoue). */
    private String formatTest(String nom, double valeur, double seuil, boolean ok, int dec) {
        String op = ok ? "<" : "≥";
        String marque = ok ? "✓" : "✗";
        return String.format("%s : %." + dec + "f %s %." + dec + "f  %s", nom, valeur, op, seuil, marque);
    }

    private VBox creerCardStat(String titre, Label valeurLabel) {
        VBox c = new VBox(5);
        c.setPadding(new Insets(15));
        c.setPrefWidth(300);
        c.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #CCCCCC; -fx-background-radius: 8px;");
        Label t = new Label(titre);
        t.setWrapText(true);
        t.setMaxWidth(270);
        t.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px; -fx-font-weight: bold;");
        valeurLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 18px; -fx-font-weight: bold;");
        c.getChildren().addAll(t, valeurLabel);
        return c;
    }

    private ImageView creerVueImage() {
        ImageView vue = new ImageView();
        vue.setFitWidth(184);
        vue.setFitHeight(224);
        vue.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 2px; -fx-background-color: #EEEEEE;");
        return vue;
    }

    private VBox creerBlocImage(String titre, ImageView vue) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        Label lbl = new Label(titre);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        box.getChildren().addAll(lbl, vue);
        return box;
    }

    private void afficherPGM(ImageView vue, Image img) {
        vue.setImage(convertirMatriceEnImage(img.getMatrix(), LARGEUR, HAUTEUR));
    }

    /** Convertit une matrice colonne (intensités 0–255) en image JavaFX niveaux de gris. */
    private WritableImage convertirMatriceEnImage(SimpleMatrix matrice, int largeur, int hauteur) {
        WritableImage img = new WritableImage(largeur, hauteur);
        PixelWriter pw = img.getPixelWriter();
        int index = 0;
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                double val = matrice.get(index++, 0) / 255.0;
                val = Math.max(0.0, Math.min(1.0, val));
                pw.setColor(x, y, Color.color(val, val, val));
            }
        }
        return img;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
