public class Resultat {

    // Attributs stricts du diagramme de classe
    private Image imgTest;
    private Image imgPlusProche;
    private float distanceMin;
    private boolean reconnu; 
    private double erreurReconstruction;

    
     //Constructeur complet pour créer le résultat d'un test
     
    public Resultat(Image imgTest, Image imgPlusProche, float distanceMin, boolean reconnu, double erreurReconstruction) {
        this.imgTest = imgTest;
        this.imgPlusProche = imgPlusProche;
        this.distanceMin = distanceMin;
        this.reconnu = reconnu;
        this.erreurReconstruction = erreurReconstruction;
    }

    
     // +getDistanceMin(): float
     
    public float getDistanceMin() {
        return this.distanceMin;
    }

    
    // +getErreurReconstruction(): double
     
    public double getErreurReconstruction() {
        return this.erreurReconstruction;
    }

   
     // renvoie simplement la valeur du booléen 'reconnu'.
     
    public boolean estReconnu() {
        return this.reconnu;
    }

    
     // Renvoie l'identifiant de la personne si elle est reconnue, sinon "Inconnu"
     
    public String getIdPersoReco() {
        // Si le booléen est vrai et qu'on a bien une image proche
        if (this.reconnu && this.imgPlusProche != null) {
            // On appelle la méthode getIdPersonne() de la classe Image
            return this.imgPlusProche.getIdPersonne();
        } else {
            return "Inconnu";
        }
    }

    
    public Image getImgTest() { return this.imgTest; }
    public Image getImgPlusProche() { return this.imgPlusProche; }
    
    public enum TypeDistance {
        EUCLIDIENNE,
        MANHATTAN
    }
}