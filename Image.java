import org.ejml.simple.SimpleMatrix;
import java.io.*;

/** 
 * @author Nathan HAVARD
 * @brief Représente une image issue de la base de données, identifiée par sa personne, son numéro d'image, son chemin et la matrice de pixels associée.
 */
public class Image{

    //------------------------------------------------------------------------------------------------------------
    //------- DEFINITION DES VARIABLES ---------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private String idPersonne; 
    private String idImage;
    private SimpleMatrix matrix;
    private String chemin;


    //------------------------------------------------------------------------------------------------------------
    //------- CONSTRUCTEURS -------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------

    /** 
     * @author Nathan HAVARD
     * @param String chemin le chemin vers le fichier PGM de l'image
     * @brief Constructeur d'une Image à partir d'un chemin. Extrait automatiquement l'id de la personne (dossier parent), l'id de l'image (nom du fichier) et lit la matrice de pixels.
     */
    public Image(String chemin){
        this.chemin = chemin;
        File fichier = new File(chemin);
        this.idImage = fichier.getName().replace(".pgm", "");
        this.idPersonne = fichier.getParentFile().getName();
        this.matrix = lirePGM(chemin);
    }

    public Image(SimpleMatrix matrix){
        this.matrix = matrix;
    }

    //------------------------------------------------------------------------------------------------------------
    //------- FONCTION -------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------


    /** 
     * @author Nathan HAVARD
     * @param String chemin le chemin vers le fichier PGM à lire
     * @return SimpleMatrix vecteur colonne (hauteur*largeur, 1) contenant les niveaux de gris de chaque pixel
     * @brief Lit un fichier PGM binaire (format P5) et le convertit en vecteur colonne, ligne par ligne.
     */
    private SimpleMatrix lirePGM(String chemin){
        try(DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(chemin)))){
            dis.readLine();
            String ligne = dis.readLine();
            while(ligne.startsWith("#")){
                ligne = dis.readLine();
            }
            String[] dimensions = ligne.split(" ");
            int largeur = Integer.parseInt(dimensions[0]);
            int hauteur = Integer.parseInt(dimensions[1]);
            dis.readLine();

            double[] pixels = new double[largeur * hauteur];
            for(int i = 0; i < pixels.length; i++){
                pixels[i] = dis.readUnsignedByte();
            }

            return new SimpleMatrix(hauteur * largeur, 1, true, pixels);
        } catch(IOException e){
            throw new RuntimeException("Erreur lecture PGM : " + chemin, e);
        }
    }

    //------------------------------------------------------------------------------------------------------------
    //------- GETTER/SETTER -------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


    /** 
     * @author Nathan HAVARD
     * @return String idPersonne identifiant de la personne (nom du dossier parent)
     * @brief Accesseur de l'identifiant de la personne associée à l'image.
     */
    public String getIdPersonne(){
        return idPersonne;
    }

    /** 
     * @author Nathan HAVARD
     * @param String idPersonne nouvel identifiant de la personne
     * @brief Modifie l'identifiant de la personne associée à l'image.
     */
    public void setIdPersonne(String idPersonne){
        this.idPersonne = idPersonne;
    }

    /** 
     * @author Nathan HAVARD
     * @return String idImage identifiant de l'image (nom du fichier sans extension)
     * @brief Accesseur de l'identifiant de l'image.
     */
    public String getIdImage(){
        return idImage;
    }

    /** 
     * @author Nathan HAVARD
     * @param String idImage nouvel identifiant de l'image
     * @brief Modifie l'identifiant de l'image.
     */
    public void setIdImage(String idImage){
        this.idImage = idImage;
    }

    /** 
     * @author Nathan HAVARD
     * @return SimpleMatrix matrix vecteur colonne contenant les niveaux de gris des pixels de l'image
     * @brief Accesseur de la matrice de pixels.
     */
    public SimpleMatrix getMatrix(){
        return matrix;
    }

    /** 
     * @author Nathan HAVARD
     * @param SimpleMatrix matrix nouvelle matrice de pixels
     * @brief Modifie la matrice de pixels.
     */
    public void setMatrix(SimpleMatrix matrix){
        this.matrix = matrix;
    }

    /** 
     * @author Nathan HAVARD
     * @return String chemin chemin du fichier image
     * @brief Accesseur du chemin du fichier image.
     */
    public String getChemin(){
        return chemin;
    }

    /** 
     * @author Nathan HAVARD
     * @param String chemin nouveau chemin du fichier image
     * @brief Modifie le chemin du fichier image.
     */
    public void setChemin(String chemin){
        this.chemin = chemin;
    }

    //------------------------------------------------------------------------------------------------------------
    //------- TEST POUR VERIFIER  -------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------

   // public static void main(String[] args){
        //Image img = new Image("donnee/reference/personne_01/1.pgm");

        //System.out.println("Chemin : " + img.getChemin());
        //System.out.println("ID Personne : " + img.getIdPersonne());
        //System.out.println("ID Image : " + img.getIdImage());
        //System.out.println("Taille matrice : " + img.getMatrix().getNumRows() + " x " + img.getMatrix().getNumCols());
        //img.getMatrix().print();
    //}

}