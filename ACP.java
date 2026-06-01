
import java.util.ArrayList;
import java.util.Arrays;

import org.ejml.simple.SimpleEVD;
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





    /**
	 * @author Jules Turchi
	 * @param taille entier qui correspond à la taille de la matrice
     * @param decomposition  stocke le résultat de la fonction .eig() liste qui associe les valeurs propres aux vecteurs propres
     * @return double[] qui contient la liste des valeurs propres
	 * @brief Fonction qui récupère la liste des valeurs propres
	 */
    public static double[] recuperer_ValeurPropre(int taille ,SimpleEVD<SimpleMatrix> decomposition){


        double[] valeurPropre = new double[taille]; // Création d'un tableau pour stocker les valeurs propres

        // Parcours du gros tableau pour résupérer uniquement les valeurs propres
        for (int i=0; i<taille; i++){
            valeurPropre[i] = decomposition.getEigenvalue(i).getReal();
        }
        return valeurPropre;
    }





    /**
	 * @author Jules Turchi
	 * @param taille entier qui correspond à la taille de la matrice
     * @param decomposition  stocke le résultat de la fonction .eig() liste qui associe les valeurs propres aux vecteurs propres
     * @return double[] qui contient la liste des vecteurs propres
	 * @brief Fonction qui récupère la liste des vecteurs propres
	 */
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


    /**
	 * @author Jules Turchi et Nathan Havard
     * @param matrixVecteur matrice d'entier 
     * @return une ListePropre qui contient la liste des valeurs propres et vecteurs propres
	 * @brief Fonction qui récupère la liste des vecteurs propres et vecteurs propres, classe les valeurs propres 
     * et vecteurs propres associées par ordre croissant puis les ajoute dans un nouvel élément de la classe ListePropre	 
    */
    public static ListePropre calculer_ValeurPropre(SimpleMatrix matrixVecteur){

        SimpleEVD<SimpleMatrix> decomposition = matrixVecteur.eig(); //Récupère des listes contenants les valeurs propres et les vecteur propre associé

        System.out.println(decomposition);

        int taille = matrixVecteur.numRows();

        // On récupère les valeurs propres et les vecteurs propres

        double [] liste_ValPropre = recuperer_ValeurPropre(taille,decomposition);
        double [][] liste_VectPropre = recuperer_VecteurPropre(taille,decomposition);


        Integer[] indices = new Integer[taille];
        for (int i = 0; i < taille; i++){
            indices[i] = i;
        }


        // On trie la liste des valeurs propre par ordre décroissant en gardant l'association (indice) avec vecteurs propres
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




    /**
	 * @author Jules Turchi
	 * @param valeurPropre_Trie tableau contenant les valeurs propres dans l'odre décroissant 
     * @return un liste de double[] qui contient la liste des valeurs propres à garder
	 * @brief Fonction qui récupère la liste des valeurs propres à garder pour avoir un taux de représentation supérieur au seuil
	 */
    public static ArrayList<Double> nb_vp_a_garder(double[] valeurPropre_Trie){

        double seuil = 0.95; // Seuil pour la représentation des données
        
        int taille = valeurPropre_Trie.length;
        double som_totale_vp = 0;
        double som_partielle_vp = 0; 
        int continuer=1;
        int nb_vp = 0;

        ArrayList<Double> liste_ValPropre = new ArrayList<>();

        // Calcul somme totale des valeurs propres (denominateur)
        for (int i = 0; i < taille; i++){
            som_totale_vp = som_totale_vp + valeurPropre_Trie[i];
        } 

        // On ajoute des valeurs propres a prendre en compte tant que le seuil n'est pas franchis
        while ((nb_vp < taille) && (continuer==1)) {
            
            som_partielle_vp = som_partielle_vp + valeurPropre_Trie[nb_vp];
            liste_ValPropre.add(valeurPropre_Trie[nb_vp]);
            nb_vp ++;

            // Condition arret
            if ((som_partielle_vp / som_totale_vp) > seuil){
                
                continuer = 0;
            
            }

        }

        return liste_ValPropre;

    }








    /**
	 * @author Jules Turchi
	 * @param matrixA tableau contenant les valeurs propres dans l'odre décroissant
     * @param nb_vp nb de valeur propre gardéees ie nb de d'eigenfaces à calculer 
	 * @param listepropre valeurs et vecteurs propre de A^T*A triées par ordre décroissant 
     * @return un liste de double[] qui contient les eigenfaces
	 * @brief Fonction qui calcule et normalise les vecteurs propres
	 */
    public static double[][] calculer_eigenface(SimpleMatrix matrixA, int nb_vp , ListePropre listepropre ){

        int taille = matrixA.numRows(); // Taille de la matrice A 

        double[][] tab_eigenface = new double[taille][nb_vp];
    

        /**
        * Notation : 
        * X_h : vecteur propre de A^T*A
        * V_h : eigenface, vecteur propre de A*A^T
        */

        for (int i=0; i<nb_vp ; i++){

            double lambda = listepropre.valeurPropre_Trie[i];  // On récupère la valeur propre

            double X_h = listepropre.vecteurPropre_Trie[i]; // On recupère un vecteur propre
            

            // On met ce vecteur sous la forme d'un double sous la forme d'une matrice colonne
            SimpleMatrix X_h_mat = new SimpleMatrix(X_h.length,1); 

            for (int j=0; j<X_h.length ; j++){

                X_h_mat.set(j,0,X_h[j]);

            }

            SimpleMatrix V_h = A.mult(X_h_mat);  // Calcul de A*X_h



            // NORMALISATION 

            double norme = Math.sqrt(lambda);


            for (int j=0; j< taille ; j++){

                tab_eigenface[j][i] = X_h_mat.get(i,0) / norme;
            }
        }


        return tab_eigenface;


    }





    /**
	 * @author Jules Turchi
	 * @param tab_eigenface tableau contenant les eigenfaces
     * @param  image vecteur contenant les pixel d'une image 
	 * @param visage_moyen moyenne des pixels 
     * @return un liste de double[] qui contient les eigenfaces
	 * @brief Fonction qui calcule et normalise les vecteurs propres
	*/
    public static double[] projection(double[] tab_eigenface, double[] image, double[] visage_moyen){


        int nb_eigenface = tab_eigenface.length;
        int taille_img =  image.length;


        // Création d'un nouveau vecteur contenant l'image centrée
        double[] img_centree = new double [taille_img];
        for (int i=0; i<taille_img ; i++){

            img_centree[i] = image[i] - visage_moyen[i]; 

        }


        // Projection 



}




    
}
