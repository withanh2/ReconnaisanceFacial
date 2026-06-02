
import java.util.ArrayList;
import java.util.Arrays;

import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;  // Module qui permet de creer de matrice 






    


/**
* @author Jules Turchi et Nathan Havard
* @date 2026
* @brief ACP est une classe qui permet de l'Analyse en Composante Principale. Elle permet grace à la matrice des images et au
* calcul des valeurs propres et des eigenfaces de faire une projection afin de trouver l'image la plus proche de l'image étudiée
*/

public class ACP {
    

    //------------------------------------------------------------------------------------------------------------
    //------- DEFINITION DES VARIABLES ---------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private Visages visages; 
    
    private double[][] tab_eigenface; // tableau de double qui stocke les eigenface
    
    private int nb_valeurPropre; // entier qui stocke le nb de valeurs propres
    
    private double[][] omega; // tab de double qui stocke les signatures

    //------------------------------------------------------------------------------------------------------------
    //------- CONSTRUCTEURS -------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


    /**
     * @author Jules Turchi et Nathan Havard
     * @param visages objet Visages contenant la matrice centrée A et la matrice de covariance réduite A^T*A
     * @brief Constructeur de l'ACP. Calcule les valeurs/vecteurs propres de A^T*A, détermine le nombre
     * de valeurs propres à garder, en déduit les eigenfaces puis projette la base pour obtenir les signatures (omega).
     */
    public ACP(Visages visages){

        this.visages = visages;

        // Décomposition de la matrice de covariance réduite (A^T*A) en valeurs et vecteurs propres triés
        ListePropre listepropre = calculer_ValeurPropre(visages.getMatrixD());

        // Nombre de valeurs propres à garder pour atteindre le seuil de représentation
        this.nb_valeurPropre = nb_vp_a_garder(listepropre.valeurPropre_Trie).size();

        // Calcul des eigenfaces puis des signatures (projections) de la base
        this.tab_eigenface = calculer_eigenface(visages.getMatrixA(), nb_valeurPropre, listepropre);
        this.omega = projection(visages.getMatrixA(), tab_eigenface, nb_valeurPropre);
    }

    //------------------------------------------------------------------------------------------------------------
    //------- GETTER --------------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


    public Visages getVisages(){
        return visages;
    }

    public double[][] getTab_eigenface(){
        return tab_eigenface;
    }

    public int getNb_valeurPropre(){
        return nb_valeurPropre;
    }

    public double[][] getOmega(){
        return omega;
    }



    //------------------------------------------------------------------------------------------------------------
    //------- SETTER --------------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


    /**
	 * @author Jules Turchi
	 * @param visages objet Visages contenant les images de la base
	 * @brief Fonction qui met à jour les visages
	 */
    public void setVisages(Visages visages){
        this.visages = visages;
    }


    /**
	 * @author Jules Turchi
	 * @param tab_eigenface tableau contenant les eigenfaces
	 * @brief Fonction qui met à jour le tableau des eigenfaces
	 */
    public void setTab_eigenface(double[][] tab_eigenface){
        this.tab_eigenface = tab_eigenface;
    }



    /**
	 * @author Jules Turchi
	 * @param nb_valeurPropre entier qui correspond au nombre de valeurs propres
	 * @brief Fonction qui met à jour le nombre de valeurs propres
	 */
    public void setNb_valeurPropre(int nb_valeurPropre){
        this.nb_valeurPropre = nb_valeurPropre;
    }


    /**
	 * @author Jules Turchi
	 * @param omega tableau contenant les projections des images
	 * @brief Fonction qui met à jour les projections (omega)
	 */
    public void setOmega(double[][] omega){
        this.omega = omega;
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

        //System.out.println(decomposition);

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
        //System.out.println(Arrays.toString(listepropre.valeurPropre_Trie));
        //System.out.println(Arrays.deepToString(listepropre.vecteurPropre_Trie));


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

            double[] X_h = listepropre.vecteurPropre_Trie[i]; // On recupère un vecteur propre
            

            // On met ce vecteur sous la forme d'un double sous la forme d'une matrice colonne
            SimpleMatrix X_h_mat = new SimpleMatrix(X_h.length,1); 

            for (int j=0; j<X_h.length ; j++){

                X_h_mat.set(j,0,X_h[j]);

            }

            SimpleMatrix V_h = matrixA.mult(X_h_mat);  // Calcul de A*X_h



            // NORMALISATION 

            double norme = Math.sqrt(lambda);

            for (int j=0; j< taille ; j++){

                tab_eigenface[j][i] = V_h.get(j,0) / norme;
            }
        }


        return tab_eigenface;


    }





    /**
	 * @author Jules Turchi
	 * @param tab_eigenface tableau contenant les eigenfaces
     * @param  matrixA matrice centrée A
	 * @param nb_eigenface nombre d'eigenfaces 
     * @return un liste de double[] qui contient les projections
	 * @brief Fonction qui calcule les projections des eigenfaces
	*/
    public static double[][] projection(SimpleMatrix matrixA, double[][] tab_eigenface, int nb_eigenface){


        int nb_img = matrixA.numCols(); // Nombre d'image dans la matrice
        int nb_pxImg =  matrixA.numRows(); // Nombre de pixel par image dans la matrice


        double[][] omega = new double[nb_eigenface][nb_img];


        for( int i=0 ; i<nb_img ; i++){

            for( int j=0 ; j<nb_eigenface ; j++){

                double prod_scal = 0.0;

                for( int k=0 ; k<nb_pxImg ; k++){

                    prod_scal = prod_scal + tab_eigenface[k][j] * matrixA.get(k,i);

                }
                omega[j][i] = prod_scal;
            }
        }
        return omega;
    }








    /**
	 * @author Jules Turchi
     * @param image image déjà traitée ie sous forme de vecteur et centrée 
	 * @param tab_eigenface tableau contenant les eigenfaces
     * @param  tab_signature tableau de double contenant les signatures
	 * @param nb_eigenface nombre d'eigenfaces 
     * @return un liste de double[] qui contient les projections
	 * @brief Fonction qui projete l'image étudiée et qui retourne l'image la plus proche
	*/
    public static double[] identification(double[][] tab_signature, SimpleMatrix image, double[][] tab_eigenface, int nb_eigenface){

        // On récupère la dimension de l'image
        int nb_col = image.numCols(); // Nombre d'image dans la matrice
        int nb_ligne =  image.numRows(); // Nombre de pixel par image dans la matrice

        // On récupère les infos sur les signatures
        int nb_image_base = tab_signature[0].length;


        // Projection de l'image
        double[][] signature_img = projection(image,tab_eigenface,nb_eigenface);

        
        
        // Calcul des distances

        int indice_min = -1;
        double distanceMin = -1.0;

        for( int i=0 ; i<nb_image_base ; i++){

            double sum_carre = 0.0;

            for( int j=0 ; j<nb_eigenface ; j++){

                double omega_img = signature_img[j][0];
                double omega_base_img = tab_signature[j][i] ;

                double difference = omega_img - omega_base_img;

                sum_carre = sum_carre + difference*difference;
            }
            double distance = Math.sqrt(sum_carre);

            if (distance < distanceMin || distanceMin == -1){
                distanceMin = distance;
                indice_min = i;
            }
        }

        double[] res = new double[2];
        res[0] =  indice_min;
        res[1] = distanceMin;
        return res;

    }










    
}
