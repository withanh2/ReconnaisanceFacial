public class Resultat {

    // Attributs de la classe
    private Image imgTest; // L'image inconnue à tester
    private Image imgPlusProche; // L'image de la base qui ressemble le plus
    private double distanceMin; // Le score de ressemblance (plus il est petit, plus ça ressemble)
    private boolean reconnu; // Vrai si la personne est identifiée, faux sinon
    private double erreurReconstruction; // Note de qualité du dessin fait par la machine

    // Constructeur : crée l'objet Resultat avec toutes ses données
    public Resultat(Image imgTest, Image imgPlusProche, double distanceMin, boolean reconnu, double erreurReconstruction) {
        this.imgTest = imgTest; // Enregistre l'image testée
        this.imgPlusProche = imgPlusProche; // Enregistre l'image la plus proche
        this.distanceMin = distanceMin; // Enregistre le score de distance
        this.reconnu = reconnu; // Enregistre la décision (oui/non)
        this.erreurReconstruction = erreurReconstruction; // Enregistre l'erreur de dessin
    }

    // Renvoie la distance minimale
    public double getDistanceMin() {
        return this.distanceMin;
    }

    // Renvoie l'erreur de reconstruction
    public double getErreurReconstruction() {
        return this.erreurReconstruction;
    }

    // Dit si le visage est reconnu (vrai/faux)
    public boolean estReconnu() {
        return this.reconnu;
    }

    // Renvoie le nom de la personne trouvée ("personne_01") ou "Inconnu"
    public String getIdPersoReco() {
        // Si le visage est validé et que l'image la plus proche existe
        if (this.reconnu && this.imgPlusProche != null) {
            return this.imgPlusProche.getIdPersonne(); // Renvoie le nom du dossier
        } else {
            return "Inconnu"; // Sécurité si la personne n'est pas dans la base
        }
    }
    
    // Renvoie l'image de test complète
    public Image getImgTest() { 
        return this.imgTest; 
    }
    
    // Renvoie l'image la plus proche complète
    public Image getImgPlusProche() { 
        return this.imgPlusProche; 
    }
    
}