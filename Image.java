import org.ejml.simple.SimpleMatrix;
import java.io.*;

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

    public Image(String chemin){
        this.chemin = chemin;
        File fichier = new File(chemin);
        this.idImage = fichier.getName().replace(".pgm", "");
        this.idPersonne = fichier.getParentFile().getName();
        this.matrix = lirePGM(chemin);
    }


    //------------------------------------------------------------------------------------------------------------
    //------- FONCTION -------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------


    private SimpleMatrix lirePGM(String chemin){
        try(DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(chemin)))){
            String format = dis.readLine();
            String ligne = dis.readLine();
            while(ligne.startsWith("#")){
                ligne = dis.readLine();
            }
            String[] dimensions = ligne.split(" ");
            int largeur = Integer.parseInt(dimensions[0]);
            int hauteur = Integer.parseInt(dimensions[1]);
            int maxVal = Integer.parseInt(dis.readLine().trim());

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


    public String getIdPersonne(){
        return idPersonne;
    }

    public void setIdPersonne(String idPersonne){
        this.idPersonne = idPersonne;
    }

    public String getIdImage(){
        return idImage;
    }

    public void setIdImage(String idImage){
        this.idImage = idImage;
    }

    public SimpleMatrix getMatrix(){
        return matrix;
    }

    public void setMatrix(SimpleMatrix matrix){
        this.matrix = matrix;
    }

    public String getChemin(){
        return chemin;
    }

    public void setChemin(String chemin){
        this.chemin = chemin;
    }

    //------------------------------------------------------------------------------------------------------------
    //------- TEST POUR VERIFIER  -------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------

    public static void main(String[] args){
        Image img = new Image("donnee/reference/personne_01/1.pgm");

        System.out.println("Chemin : " + img.getChemin());
        System.out.println("ID Personne : " + img.getIdPersonne());
        System.out.println("ID Image : " + img.getIdImage());
        System.out.println("Taille matrice : " + img.getMatrix().getNumRows() + " x " + img.getMatrix().getNumCols());
        img.getMatrix().print();
    }

}