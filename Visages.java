import org.ejml.simple.SimpleMatrix;
import java.util.List;
import java.util.ArrayList;

public class Visages{
    //------------------------------------------------------------------------------------------------------------
    //------- DEFINITION DES VARIABLES ---------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private List<Image> images;
    private SimpleMatrix matrixVecteur;
    private SimpleMatrix matrixD;

    //------------------------------------------------------------------------------------------------------------
    //------- CONSTRUCTEURS -------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------

    public Visages(List<Image> images){
        this.images = images;
        this.matrixVecteur = creerMatrixVecteur();
        this.matrixD = calculerMatriceCovarianceReduite();
    }

    //------------------------------------------------------------------------------------------------------------
    //------- FONCTION -------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

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

    public SimpleMatrix calculerMatriceCovarianceReduite(){
        SimpleMatrix A = centrerMatrice();
        this.matrixD = A.transpose().mult(A);
        return matrixD;
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