
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

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
    
    private double[][] tabEingenface; // tableau de double qui stocke les eigenface
    
    private int nbValeurPropre; // entier qui stocke le nb de valeurs propres
    
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
        ListePropre listepropre = calculerValeurPropre(visages.getMatrixD());

        // Nombre de valeurs propres à garder pour atteindre le seuil de représentation
        this.nbValeurPropre = nbVpAGarder(listepropre.valeurPropreTrie).size();

        // Calcul des eigenfaces puis des signatures (projections) de la base
        this.tabEingenface = calculerEigenface(visages.getMatrixA(), nbValeurPropre, listepropre);
        this.omega = projection(visages.getMatrixA(), tabEingenface, nbValeurPropre);
    }

    //------------------------------------------------------------------------------------------------------------
    //------- GETTER --------------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


    public Visages getVisages(){
        return visages;
    }

    public double[][] getTabEingenface(){
        return tabEingenface;
    }

    public int getNbValeurPropre(){
        return nbValeurPropre;
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
	 * @param tabEingenface tableau contenant les eigenfaces
	 * @brief Fonction qui met à jour le tableau des eigenfaces
	 */
    public void settabEingenface(double[][] tabEingenface){
        this.tabEingenface = tabEingenface;
    }



    /**
	 * @author Jules Turchi
	 * @param nbValeurPropre entier qui correspond au nombre de valeurs propres
	 * @brief Fonction qui met à jour le nombre de valeurs propres
	 */
    public void setnbValeurPropre(int nbValeurPropre){
        this.nbValeurPropre = nbValeurPropre;
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
    public static double[] recupererValeurPropre(int taille ,SimpleEVD<SimpleMatrix> decomposition){


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
    public static double[][] recupererVecteurPropre( int taille, SimpleEVD<SimpleMatrix> decomposition){

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
    public static ListePropre calculerValeurPropre(SimpleMatrix matrixVecteur){

        SimpleEVD<SimpleMatrix> decomposition = matrixVecteur.eig(); //Récupère des listes contenants les valeurs propres et les vecteur propre associé

        //System.out.println(decomposition);

        int taille = matrixVecteur.numRows();

        // On récupère les valeurs propres et les vecteurs propres

        double [] listeValPropre = recupererValeurPropre(taille,decomposition);
        double [][] listeVectPropre = recupererVecteurPropre(taille,decomposition);


        Integer[] indices = new Integer[taille];
        for (int i = 0; i < taille; i++){
            indices[i] = i;
        }


        // On trie la liste des valeurs propre par ordre décroissant en gardant l'association (indice) avec vecteurs propres
        Arrays.sort(indices, (a, b) -> Double.compare(listeValPropre[b], listeValPropre[a]));

        double[] valeurPropreTrie       = new double[taille];
        double[][] vecteurPropreTrie    = new double[taille][taille];
        for (int i = 0; i < taille; i++){
            valeurPropreTrie[i] = listeValPropre[indices[i]];
            vecteurPropreTrie[i] = listeVectPropre[indices[i]];
        }
        ListePropre listepropre = new ListePropre(valeurPropreTrie,vecteurPropreTrie);
        //System.out.println(Arrays.toString(listepropre.valeurPropreTrie));
        //System.out.println(Arrays.deepToString(listepropre.vecteurPropreTrie));


        return listepropre;
    }




    /**
	 * @author Jules Turchi
	 * @param valeurPropreTrie tableau contenant les valeurs propres dans l'odre décroissant 
     * @return un liste de double[] qui contient la liste des valeurs propres à garder
	 * @brief Fonction qui récupère la liste des valeurs propres à garder pour avoir un taux de représentation supérieur au seuil
	 */
    public static ArrayList<Double> nbVpAGarder(double[] valeurPropreTrie){

        double seuil = 0.95; // Seuil pour la représentation des données
        
        int taille = valeurPropreTrie.length;
        double somTotaleVp = 0;
        double somPartielleVp = 0; 
        int continuer=1;
        int nbVp = 0;

        ArrayList<Double> listeValPropre = new ArrayList<>();

        // Calcul somme totale des valeurs propres (denominateur)
        for (int i = 0; i < taille; i++){
            somTotaleVp = somTotaleVp + valeurPropreTrie[i];
        } 

        // On ajoute des valeurs propres a prendre en compte tant que le seuil n'est pas franchis
        while ((nbVp < taille) && (continuer==1)) {
            
            somPartielleVp = somPartielleVp + valeurPropreTrie[nbVp];
            listeValPropre.add(valeurPropreTrie[nbVp]);
            nbVp ++;

            // Condition arret
            if ((somPartielleVp / somTotaleVp) > seuil){
                
                continuer = 0;
            
            }

        }

        return listeValPropre;

    }








    /**
	 * @author Jules Turchi
	 * @param matrixA tableau contenant les valeurs propres dans l'odre décroissant
     * @param nbVp nb de valeur propre gardéees ie nb de d'eigenfaces à calculer 
	 * @param listepropre valeurs et vecteurs propre de A^T*A triées par ordre décroissant 
     * @return un liste de double[] qui contient les eigenfaces
	 * @brief Fonction qui calcule et normalise les vecteurs propres
	 */
    public static double[][] calculerEigenface(SimpleMatrix matrixA, int nbVp , ListePropre listepropre ){

        int taille = matrixA.numRows(); // Taille de la matrice A 

        double[][] tabEingenface = new double[taille][nbVp];
    

        /**
        * Notation : 
        * xH : vecteur propre de A^T*A
        * vH : eigenface, vecteur propre de A*A^T
        */

        for (int i=0; i<nbVp ; i++){

            double lambda = listepropre.valeurPropreTrie[i];  // On récupère la valeur propre

            double[] xH = listepropre.vecteurPropreTrie[i]; // On recupère un vecteur propre
            

            // On met ce vecteur sous la forme d'un double sous la forme d'une matrice colonne
            SimpleMatrix xHMat = new SimpleMatrix(xH.length,1); 

            for (int j=0; j<xH.length ; j++){

                xHMat.set(j,0,xH[j]);

            }

            SimpleMatrix vH = matrixA.mult(xHMat);  // Calcul de A*xH



            // NORMALISATION 

            double norme = Math.sqrt(lambda);

            for (int j=0; j< taille ; j++){

                tabEingenface[j][i] = vH.get(j,0) / norme;
            }
        }


        return tabEingenface;


    }





    /**
	 * @author Jules Turchi
	 * @param tabEingenface tableau contenant les eigenfaces
     * @param  matrixA matrice centrée A
	 * @param nbEigenface nombre d'eigenfaces 
     * @return un liste de double[] qui contient les projections
	 * @brief Fonction qui calcule les projections des eigenfaces
	*/
    public static double[][] projection(SimpleMatrix matrixA, double[][] tabEingenface, int nbEigenface){


        int nbImg = matrixA.numCols(); // Nombre d'image dans la matrice
        int nbPxImg =  matrixA.numRows(); // Nombre de pixel par image dans la matrice


        double[][] omega = new double[nbEigenface][nbImg];


        for( int i=0 ; i<nbImg ; i++){

            for( int j=0 ; j<nbEigenface ; j++){

                double prodScal = 0.0;

                for( int k=0 ; k<nbPxImg ; k++){

                    prodScal = prodScal + tabEingenface[k][j] * matrixA.get(k,i);

                }
                omega[j][i] = prodScal;
            }
        }
        return omega;
    }








    /**
	 * @author Jules Turchi
     * @param image image déjà traitée ie sous forme de vecteur et centrée 
	 * @param tabEingenface tableau contenant les eigenfaces
     * @param  tabSignature tableau de double contenant les signatures
	 * @param nbEigenface nombre d'eigenfaces 
     * @return un liste de double[] qui contient les projections
	 * @brief Fonction qui projete l'image étudiée et qui retourne l'image la plus proche
	*/
    public static double[] identification(double[][] tabSignature, SimpleMatrix image, double[][] tabEingenface, int nbEigenface){

        // On récupère la dimension de l'image
        int nbCol = image.numCols(); // Nombre d'image dans la matrice
        int nbLigne =  image.numRows(); // Nombre de pixel par image dans la matrice

        // On récupère les infos sur les signatures
        int nbImageBase = tabSignature[0].length;


        // Projection de l'image
        double[][] signatureImg = projection(image,tabEingenface,nbEigenface);

        
        
        // Calcul des distances

        int indiceMin = -1;
        double distanceMin = -1.0;

        for( int i=0 ; i<nbImageBase ; i++){

            double sumCarre = 0.0;

            for( int j=0 ; j<nbEigenface ; j++){

                double omegaImg = signatureImg[j][0];
                double omegaBaseImg = tabSignature[j][i] ;

                double difference = omegaImg - omegaBaseImg;

                sumCarre = sumCarre + difference*difference;
            }
            double distance = Math.sqrt(sumCarre);

            if (distance < distanceMin || distanceMin == -1){
                distanceMin = distance;
                indiceMin = i;
            }
        }

        double[] res = new double[2];
        res[0] =  indiceMin;
        res[1] = distanceMin;
        return res;

    }




    /**
     * @author Jules Turchi et Nathan Havard
     * @param valeurs tableau de valeurs à convertir en niveaux de gris
     * @param etirer  true pour étirer l'intervalle [min, max] sur [0, 255] (cas des eigenfaces qui
     *                contiennent des valeurs négatives et de faible amplitude), false pour simplement
     *                arrondir et borner les valeurs dans [0, 255] (cas du visage moyen déjà en niveaux de gris)
     * @return un tableau d'entiers contenant les niveaux de gris dans [0, 255]
     * @brief Convertit un vecteur de valeurs réelles en niveaux de gris affichables dans un PGM.
     */
    private static int[] versNiveauxGris(double[] valeurs, boolean etirer){

        int[] gris = new int[valeurs.length];

        if (etirer){

            // On cherche le min et le max pour ramener l'intervalle [min, max] sur [0, 255]
            double min = valeurs[0];
            double max = valeurs[0];
            for (double v : valeurs){
                if (v < min) min = v;
                if (v > max) max = v;
            }

            double ecart = max - min;
            for (int i = 0; i < valeurs.length; i++){
                if (ecart == 0){
                    gris[i] = 0;
                } else {
                    gris[i] = (int) Math.round((valeurs[i] - min) / ecart * 255.0);
                }
            }

        } else {

            // Valeurs déjà en niveaux de gris : on arrondit et on borne dans [0, 255]
            for (int i = 0; i < valeurs.length; i++){
                int valeur = (int) Math.round(valeurs[i]);
                if (valeur < 0)   valeur = 0;
                if (valeur > 255) valeur = 255;
                gris[i] = valeur;
            }
        }

        return gris;
    }




    /**
     * @author Nathan Havard
     * @param chemin  chemin du fichier PGM à écrire
     * @param pixels  tableau des niveaux de gris (dans [0, 255]), rangés ligne par ligne
     * @param largeur largeur de l'image en pixels
     * @param hauteur hauteur de l'image en pixels
     * @brief Écrit un vecteur de pixels dans un fichier PGM binaire (format P5).
     */
    private static void ecrirePGM(String chemin, int[] pixels, int largeur, int hauteur){

        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(chemin)))){

            // En-tête PGM : format, dimensions puis valeur max d'un pixel
            dos.writeBytes("P5\n");
            dos.writeBytes(largeur + " " + hauteur + "\n");
            dos.writeBytes("255\n");

            // Corps : un octet par pixel, dans l'ordre de lecture (ligne par ligne)
            for (int i = 0; i < pixels.length; i++){
                dos.writeByte(pixels[i]);
            }

        } catch (IOException e){
            throw new RuntimeException("Erreur écriture PGM : " + chemin, e);
        }
    }




    /**
     * @author Nathan Havard
     * @param dossier dossier de sortie dans lequel écrire les fichiers PGM (créé s'il n'existe pas)
     * @param largeur largeur des images en pixels (92 pour notre base)
     * @param hauteur hauteur des images en pixels (112 pour notre base)
     * @brief Transforme le visage moyen et chaque eigenface en fichiers PGM afin de pouvoir les visualiser.
     *        Le visage moyen est enregistré tel quel (visageMoyen.pgm) et chaque eigenface est étirée sur
     *        l'échelle des niveaux de gris (eigenface_0.pgm, eigenface_1.pgm, ...).
     */
    public void exporterPGM(String dossier, int largeur, int hauteur){

        // Création du dossier de sortie si besoin
        File rep = new File(dossier);
        if (!rep.exists()){
            rep.mkdirs();
        }

        //------- Visage moyen ----------------------------------------------------------------------------------
        SimpleMatrix moyen = visages.getvisageMoyen();
        int nbPixels = moyen.getNumRows();

        double[] pixelsMoyen = new double[nbPixels];
        for (int i = 0; i < nbPixels; i++){
            pixelsMoyen[i] = moyen.get(i, 0);
        }
        ecrirePGM(dossier + File.separator + "visageMoyen.pgm", versNiveauxGris(pixelsMoyen, false), largeur, hauteur);

        //------- Eigenfaces (une par colonne de tabEingenface) -------------------------------------------------
        for (int i = 0; i < nbValeurPropre; i++){

            double[] eigenface = new double[nbPixels];
            for (int j = 0; j < nbPixels; j++){
                eigenface[j] = tabEingenface[j][i];
            }
            ecrirePGM(dossier + File.separator + "eigenface_" + i + ".pgm", versNiveauxGris(eigenface, true), largeur, hauteur);
        }
    }
    
    
    
    
    
    
    
    
    
    
    }	
    
    
   









    

