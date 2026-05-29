/**
 * Classe représentant le bilan complet du test d'identification d'un visage.
 * Elle regroupe les images, le score de ressemblance et la décision finale.
 */
public class Resultat {

    // Attributs de la classe
    private Image imgTest;               // L'image inconnue à tester
    private Image imgPlusProche;         // L'image de la base qui ressemble le plus
    private double distanceMin;          // Le score de ressemblance (plus il est petit, plus ça ressemble)
    private boolean reconnu;             // Vrai si la personne est identifiée, faux sinon
    private double erreurReconstruction; // Note de qualité du dessin fait par la machine

    /**
     * Constructeur pour créer l'objet Resultat avec toutes ses données.
     * * @param imgTest L'image de test soumise à l'algorithme.
     * @param imgPlusProche L'image la plus ressemblante trouvée dans la base.
     * @param distanceMin La distance minimale calculée (score d'écart).
     * @param reconnu Le statut de la reconnaissance (vrai/faux).
     * @param erreurReconstruction L'erreur calculée lors de la reconstruction.
     */
    public Resultat(Image imgTest, Image imgPlusProche, double distanceMin, boolean reconnu, double erreurReconstruction) {
        this.imgTest = imgTest;                             // Enregistre l'image testée
        this.imgPlusProche = imgPlusProche;                 // Enregistre l'image la plus proche
        this.distanceMin = distanceMin;                     // Enregistre le score de distance
        this.reconnu = reconnu;                             // Enregistre la décision (oui/non)
        this.erreurReconstruction = erreurReconstruction;   // Enregistre l'erreur de dessin
    }

    /**
     * Renvoie la distance minimale mesurée.
     * * @return Le score de distance sous forme de double.
     */
    public double getDistanceMin() {
        return this.distanceMin; // Renvoie la distance minimale
    }

    /**
     * Renvoie l'erreur calculée pour la reconstruction du visage.
     * * @return La valeur de l'erreur de reconstruction.
     */
    public double getErreurReconstruction() {
        return this.erreurReconstruction; // Renvoie l'erreur de reconstruction
    }

    /**
     * Indique si l'algorithme a validé la reconnaissance de la personne.
     * * @return Vrai si le visage est reconnu, faux sinon.
     */
    public boolean estReconnu() {
        return this.reconnu; // Dit si le visage est reconnu (vrai/faux)
    }

    /**
     * Analyse la décision et renvoie l'identifiant textuel de la personne reconnue.
     * * @return Le nom du dossier (ex: "personne_01") ou "Inconnu".
     */
    public String getIdPersoReco() {
        // Si le visage est validé et que l'image la plus proche existe
        if (this.reconnu && this.imgPlusProche != null) {
            return this.imgPlusProche.getIdPersonne(); // Renvoie le nom du dossier
        } else {
            return "Inconnu"; // Sécurité si la personne n'est pas dans la base
        }
    }
    
    /**
     * Récupère l'image qui a été utilisée pour faire le test.
     * * @return L'objet Image correspondant au test.
     */
    public Image getImgTest() { 
        return this.imgTest; // Renvoie l'image de test complète
    }
    
    /**
     * Récupère l'image de référence identifiée comme étant la plus ressemblante.
     * * @return L'objet Image de la base le plus proche.
     */
    public Image getImgPlusProche() { 
        return this.imgPlusProche; // Renvoie l'image la plus proche complète
    }
    
} 
