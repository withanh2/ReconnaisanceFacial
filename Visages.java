import org.ejml.simple.SimpleMatrix;
import java.util.List;
import java.util.ArrayList;

/** 
 * @author Nathan HAVARD
 * @brief Représente un ensemble de visages (images de référence) et fournit les opérations de l'ACP : construction de la matrice des vecteurs, calcul du visage moyen, centrage et matrice de covariance.
 */
public class Visages{
    //------------------------------------------------------------------------------------------------------------
    //------- DEFINITION DES VARIABLES ---------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private List<Image> images;
    private SimpleMatrix matrixVecteur;
    private SimpleMatrix matrixD;
    private SimpleMatrix matrixA;

    private Image image;
    private SimpleMatrix imageAanalyser;


    //------------------------------------------------------------------------------------------------------------
    //------- CONSTRUCTEURS -------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------

    /** 
     * @author Nathan HAVARD
     * @param List<Image> images liste des images de référence à utiliser
     * @brief Constructeur de Visages. Initialise la matrice des images en vecteurs et la matrice de covariance réduite. La matrice centrée (matrice A) est portée par les classes filles VisagesListeImage et VisagesAanalyser.
     */
    public Visages(List<Image> images){
        this.images = images;
        this.matrixVecteur = creerMatrixVecteur();
        this.matrixD = calculerMatriceCovarianceReduite();
        this.matrixA = centrerMatrice();
    }

    /**
     * @author Nathan HAVARD
     * @param Image image l'image à analyser
     * @param Visages modele ensemble des visages de référence, utilisé pour récupérer le visage moyen
     * @brief Constructeur pour une image à analyser. Transforme l'image en vecteur colonne et la centre
     * sur le visage moyen du modèle de référence (et non sur elle-même).
     */
    public Visages(Image image, Visages modele){
        this.image = image;
        this.matrixVecteur = image.getMatrix();
        SimpleMatrix moyen = modele.calculerVisageMoyen();
        this.imageAanalyser = matrixVecteur.minus(moyen);
    }


    //------------------------------------------------------------------------------------------------------------
    //------- FONCTION -------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    /** 
     * @author Nathan HAVARD
     * @return SimpleMatrix matrice (nbPixels x nbImages) où chaque colonne est le vecteur d'une image
     * @brief Construit la matrice contenant toutes les images sous forme de vecteurs, une image par colonne.
     */
    private SimpleMatrix creerMatrixVecteur(){
        int nbPixels = images.get(0).getMatrix().getNumRows();
        int nbImages = images.size();
        SimpleMatrix matrice = new SimpleMatrix(nbPixels, nbImages);

        for(int j = 0; j < nbImages; j++){
            SimpleMatrix vecteur = images.get(j).getMatrix();
            for(int i = 0; i < nbPixels; i++){
                matrice.set(i, j, vecteur.get(i, 0));
            }
        }

        return matrice;
    }

    /** 
     * @author Nathan HAVARD
     * @return SimpleMatrix vecteur colonne (nbPixels x 1) contenant la moyenne de chaque pixel sur l'ensemble des images
     * @brief Calcule le visage moyen : pour chaque pixel, fait la moyenne de sa valeur sur toutes les images.
     */
    public SimpleMatrix calculerVisageMoyen(){
        int nbPixels = matrixVecteur.getNumRows();
        int nbImages = matrixVecteur.getNumCols();
        SimpleMatrix moyen = new SimpleMatrix(nbPixels, 1);

        for(int i = 0; i < nbPixels; i++){
            double somme = 0;
            for(int j = 0; j < nbImages; j++){
                somme += matrixVecteur.get(i, j);
            }
            moyen.set(i, 0, somme / nbImages);
        }

        return moyen;
    }

    /** 
     * @author Nathan HAVARD
     * @return SimpleMatrix matrice (nbPixels x nbImages) où chaque colonne est une image centrée
     * @brief Centre la matrice des images en soustrayant le visage moyen à chaque colonne.
     */
    public SimpleMatrix centrerMatrice(){
        SimpleMatrix moyen = calculerVisageMoyen();
        int nbPixels = matrixVecteur.getNumRows();
        int nbImages = matrixVecteur.getNumCols();
        SimpleMatrix centree = new SimpleMatrix(nbPixels, nbImages);

        for(int j = 0; j < nbImages; j++){
            for(int i = 0; i < nbPixels; i++){
                centree.set(i, j, matrixVecteur.get(i, j) - moyen.get(i, 0));
            }
        }

        return centree;
    }

    /** 
     * @author Nathan HAVARD
     * @return SimpleMatrix matrice de covariance réduite (nbImages x nbImages) calculée comme A^T * A où A est la matrice centrée
     * @brief Calcule la matrice de covariance réduite utilisée pour l'ACP. Stocke le résultat dans matrixD.
     */
    public SimpleMatrix calculerMatriceCovarianceReduite(){
        SimpleMatrix A = centrerMatrice();
        this.matrixD = A.transpose().mult(A);
        return matrixD;
    }

    //------------------------------------------------------------------------------------------------------------
    //------- GETTERS ET SETTERS ---------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    /**
     * @author Nathan HAVARD
     * @return List<Image> liste des images de référence
     * @brief Retourne la liste des images.
     */
    public List<Image> getImages(){
        return images;
    }

    /**
     * @author Nathan HAVARD
     * @param images liste des images de référence
     * @brief Modifie la liste des images.
     */
    public void setImages(List<Image> images){
        this.images = images;
    }

    /**
     * @author Nathan HAVARD
     * @return SimpleMatrix matrice des images sous forme de vecteurs
     * @brief Retourne la matrice des vecteurs.
     */
    public SimpleMatrix getMatrixVecteur(){
        return matrixVecteur;
    }

    /**
     * @author Nathan HAVARD
     * @param matrixVecteur matrice des images sous forme de vecteurs
     * @brief Modifie la matrice des vecteurs.
     */
    public void setMatrixVecteur(SimpleMatrix matrixVecteur){
        this.matrixVecteur = matrixVecteur;
    }

    /**
     * @author Nathan HAVARD
     * @return SimpleMatrix matrice de covariance réduite
     * @brief Retourne la matrice de covariance réduite.
     */
    public SimpleMatrix getMatrixD(){
        return matrixD;
    }

    /**
     * @author Nathan HAVARD
     * @param matrixD matrice de covariance réduite
     * @brief Modifie la matrice de covariance réduite.
     */
    public void setMatrixD(SimpleMatrix matrixD){
        this.matrixD = matrixD;
    }

    /**
     * @author Nathan HAVARD
     * @return SimpleMatrix matrice centrée (matrice A)
     * @brief Retourne la matrice centrée.
     */
    public SimpleMatrix getMatrixA(){
        return matrixA;
    }

    /**
     * @author Nathan HAVARD
     * @param matrixA matrice centrée (matrice A)
     * @brief Modifie la matrice centrée.
     */
    public void setMatrixA(SimpleMatrix matrixA){
        this.matrixA = matrixA;
    }

    /**
     * @author Nathan HAVARD
     * @return Image image à analyser
     * @brief Retourne l'image à analyser.
     */
    public Image getImage(){
        return image;
    }

    /**
     * @author Nathan HAVARD
     * @param image image à analyser
     * @brief Modifie l'image à analyser.
     */
    public void setImage(Image image){
        this.image = image;
    }

    /**
     * @author Nathan HAVARD
     * @return SimpleMatrix vecteur de l'image à analyser, centré sur le visage moyen
     * @brief Retourne l'image à analyser centrée.
     */
    public SimpleMatrix getImageAanalyser(){
        return imageAanalyser;
    }

    /**
     * @author Nathan HAVARD
     * @param imageAanalyser vecteur de l'image à analyser, centré sur le visage moyen
     * @brief Modifie l'image à analyser centrée.
     */
    public void setImageAanalyser(SimpleMatrix imageAanalyser){
        this.imageAanalyser = imageAanalyser;
    }

    //------------------------------------------------------------------------------------------------------------
    //------- TEST POUR VERIFIER  --------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    //public static void main(String[] args){
    //    Image img1 = new Image("donnee/reference/personne_01/1.pgm");
    //    Image img2 = new Image("donnee/reference/personne_02/1.pgm");
    //
    //    List<Image> liste = new ArrayList<>();
    //    liste.add(img1);
    //    liste.add(img2);
    //
    //    Visages visages = new Visages(liste);
    //    SimpleMatrix mat = visages.matrixVecteur;
    //
    //    System.out.println("Taille matrice : " + mat.getNumRows() + " x " + mat.getNumCols());
    //
    //    System.out.println("\n--- Verification colonne 0 = image 1 ---");
    //    for(int i = 0; i < 5; i++){
    //        System.out.println("pixel " + i + " : matrice=" + mat.get(i, 0) + "  image1=" + img1.getMatrix().get(i, 0));
    //    }
    //
    //    System.out.println("\n--- Verification colonne 1 = image 2 ---");
    //    for(int i = 0; i < 5; i++){
    //        System.out.println("pixel " + i + " : matrice=" + mat.get(i, 1) + "  image2=" + img2.getMatrix().get(i, 0));
    //    }
    //
    //    SimpleMatrix moyen = visages.calculerVisageMoyen();
    //    System.out.println("\n--- Visage moyen (5 premiers pixels) ---");
    //    for(int i = 0; i < 5; i++){
    //        System.out.println("pixel " + i + " : moyenne=" + moyen.get(i, 0)
    //            + "  (img1=" + img1.getMatrix().get(i, 0) + " + img2=" + img2.getMatrix().get(i, 0) + ") / 2");
    //    }
    //
    //    SimpleMatrix centree = visages.centrerMatrice();
    //    System.out.println("\n--- Matrice centree (5 premiers pixels) ---");
    //    for(int i = 0; i < 5; i++){
    //        System.out.println("pixel " + i + " : col0=" + centree.get(i, 0) + "  col1=" + centree.get(i, 1)
    //            + "  (original: " + mat.get(i, 0) + " et " + mat.get(i, 1) + "  moyenne=" + moyen.get(i, 0) + ")");
    //    }
    //
    //    SimpleMatrix cov = visages.calculerMatriceCovarianceReduite();
    //    System.out.println("\n--- Matrice de covariance reduite ---");
    //    System.out.println("Taille : " + cov.getNumRows() + " x " + cov.getNumCols());
    //    cov.print();
    //}
}