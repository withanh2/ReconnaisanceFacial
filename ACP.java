
import java.util.Arrays;
import org.ejml.simple.SimpleMatrix;  // Module qui permet de creer de matrice 

public class ACP {
    

    //------------------------------------------------------------------------------------------------------------
    //------- DEFINITION DES VARIABLES ---------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private Visages visages;

    private double[][] valeurPropre;

    private double[][] vecteurPropre;

    private int nb_vecteurPropre;


    //------------------------------------------------------------------------------------------------------------
    //------- CONSTRUCTEURS -------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


    public ACP(Visages visages){

        this.visages = visages;
        this.nb_vecteurPropre = 0;
        this.valeurPropre = null;
        this.vecteurPropre = null;

    }

    public ACP(Visages visages, int nb_vecteurPropre){
        this.visages = visages;
        this.nb_vecteurPropre = nb_vecteurPropre;
        this.valeurPropre = null;
        this.vecteurPropre = null;
    }


    //------------------------------------------------------------------------------------------------------------
    //------- GETTER --------------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


    public double[][] getValeurPropre(){
        return valeurPropre;
    }

    public double[][] getVecteurPropre(){
        return vecteurPropre;
    }


     public int getNb_vecteurPropre(){
        return nb_vecteurPropre;
    }



    //------------------------------------------------------------------------------------------------------------
    //------- SETTER --------------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


    public void setValeurPropre( double[][] valeurPropre){
        this.valeurPropre = valeurPropre;
    }

    public void setVecteurPropre(double[][] vecteurPropre){
        this.vecteurPropre = vecteurPropre;
    }


     public void setNb_vecteurPropre(int nb_vecteurPropre){
        this.nb_vecteurPropre = nb_vecteurPropre;
    }




    //------------------------------------------------------------------------------------------------------------
    //------- FONCTION ------------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------




    

    public static double[] recuperer_ValeurPropre(int taille ,SimpleEVD<SimpleMatrix> decomposition){


        double[] valeurPropre = new double[taille]; // Création d'un tableau pour stocker les valeurs propres

        // Parcours du gros tableau pour résupérer uniquement les valeurs propres
        for (int i=0; i<taille; i++){
            valeurPropre[i] = decomposition.getEigenvalue(i).getReal();
        }

    return valeurPropre;
    }









    public static double[][] recuperer_VecteurPropre( int taille, SimpleEVD<SimpleMatrix> decomposition){

        double[][] vecteurPropre = new double[taille][taille]; // Chaque ligne = un vecteur propre

        // Parcours pour récupérer chaque vecteur propre associé
        for (int i=0; i<taille; i++){
            SimpleMatrix vecteur = decomposition.getEigenVector(i);
            for (int j=0; j<taille; j++){
                vecteurPropre[i][j] = vecteur.get(j, 0);
            }
        }

    return vecteurPropre;
    }











    public static ListePropre calculer_ValeurPropre(SimpleMatrix matrice){

        SimpleEVD<SimpleMatrix> decomposition = matrice.eig(); //Récupère des listes contenants les valeurs propres et les vecteur propre associé

        System.out.println(decomposition);

        int taille = matrice.numRows();

        // On met les valeurs propre dans l'odre décroissant

        double [] liste_ValPropre = recuperer_ValeurPropre(taille,decomposition);
        double [][] liste_VectPropre = recuperer_VecteurPropre(taille,decomposition);


        Integer[] indices = new Integer[taille];
        for (int i = 0; i < taille; i++){
            indices[i] = i;
        }

        Arrays.sort(indices, (a, b) -> Double.compare(liste_ValPropre[b], liste_ValPropre[a]));

        double[] valeurPropre_Trie       = new double[taille];
        double[][] vecteurPropre_Trie    = new double[taille][taille];
        for (int i = 0; i < taille; i++){
            valeurPropre_Trie[i] = liste_ValPropre[indices[i]];
            vecteurPropre_Trie[i] = liste_VectPropre[indices[i]];
        }
        ListePropre listepropre = new ListePropre(valeurPropre_Trie,vecteurPropre_Trie);
        System.out.println(Arrays.toString(listepropre.valeurPropre_Trie));
        System.out.println(Arrays.deepToString(listepropre.vecteurPropre_Trie));


        return listepropre;
    }











    public static ArrayList<Double> nb_vp_a_garder(double[] valeurPropre_Trie){

        double seuil = 0.95;
        int taille = valeurPropre_Trie.length;
        double som_totale_vp = 0;
        double som_partielle_vp = 0;
        int continuer=1;
        int nb_vp = 0;

        ArrayList<Double> liste_ValPropre = new ArrayList<>();


        for (int i = 0; i < taille; i++){
            som_totale_vp = som_totale_vp + valeurPropre_Trie[i];
        } 


        while ((nb_vp < taille) && (continuer==1)) {
            
            som_partielle_vp = som_partielle_vp + valeurPropre_Trie[nb_vp];
            liste_ValPropre.add(valeurPropre_Trie[nb_vp]);
            nb_vp ++;

            if ((som_partielle_vp / som_totale_vp) > seuil){
                
                continuer = 0;
            
            }

        }


        return liste_ValPropre;

    }





    





    

        
    
}