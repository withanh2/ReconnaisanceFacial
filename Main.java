import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleEVD;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.util.Collections;




public class Main {



    //----------------------------------------------------------------------------------------------------------------------------
    //------- FONCTION TEST ------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------
	



    /**
    * @author Jules Turchi et Nathan Havard
    * @brief Procedure de test du code : on crée de fausses images pour tester le bon fonctionnement de la procedure
    */
    public static void test(){

        //------- Création de la base des images ---------------------------------------------------------------------------------

        // On crée des images de 4 pixels afin de pouvoir réaliser les tests
        SimpleMatrix matExemple1 = new SimpleMatrix(new double[][]{{1.0},{2.0},{3.0},{4.0}});
        Image image1 = new Image(matExemple1);
        
        SimpleMatrix matExemple2 = new SimpleMatrix(new double[][]{{250.0},{200.0},{30.0},{240.0}});
        Image image2 = new Image(matExemple2);
        
        SimpleMatrix matExemple3 = new SimpleMatrix(new double[][]{{10.0}, {20.0},{3.0},{4.0}});
        Image image3 = new Image(matExemple3);
        
        SimpleMatrix matExemple4 = new SimpleMatrix(new double[][]{{4.0}, {8.0},{9.0}, {45.0}});
        Image image4 = new Image(matExemple4);
        
        // Création de la liste des images
        List<Image> liste = new ArrayList<>();
        liste.add(image1);
        liste.add(image2);
        liste.add(image3);
        liste.add(image4);


        // Création d'une nouvelle classe image
        Visages visages = new Visages(liste);


        // Affichage des différentes matrices pour verifier les données
        System.out.println("Matrice de vecteur");
        System.out.println(visages.getMatrixVecteur());

        System.out.println("Matrice A");
        System.out.println(visages.getMatrixA());

        System.out.println("Visage moyen");
        System.out.println(visages.getvisageMoyen());
    
		System.out.println("Matrice D");
        System.out.println(visages.getMatrixD());	

        // Création de la classe ACP 
        ACP acp = new ACP(visages);
        
        // Vérification des infos des différents attributs de classes
        System.out.println("Nb valeur propre gardé");
        System.out.println(acp.getNbValeurPropre() + "\n");

        System.out.println("Tableau eigenface");
        System.out.println(Arrays.deepToString(acp.getTabEingenface())+"\n");

        System.out.println("Omega");
        System.out.println(Arrays.deepToString(acp.getOmega()));


    	// ------- Création de l'image à tester -------------------------------------------------------------------------------------


		SimpleMatrix matAanalyser1 = new SimpleMatrix(new double[][]{{10.0}, {9.0},{4.0}, {45.0}});
        Image imageAanalyser = new Image(matAanalyser1);
        Visages visagesImageAnalyser = new Visages(imageAanalyser, visages);
        
        
        
        // Application de l'ACP
        double[] res = ACP.identification(acp.getOmega(), visagesImageAnalyser.getImageAanalyser(), acp.getTabEingenface(),acp.getNbValeurPropre());
        
        // Affichage des resultats de l'ACP
        System.out.println("\n\nOn analyser notre image choisi");
        System.out.println("Voici l'indice_min : " + res[0]);
        System.out.println("Voici la distance min : " + res[1]);



    	//------- Option affichage des valeurs propres -----------------------------------------------------------------------------

    

        /*
		ListePropre vp = calculerValeurPropre(matExemple);
        ArrayList<Double> vp2 = nbVpAGarder(vp.valeurPropreTrie);
		for (int i = 0; i < vp.valeurPropreTrie.length; i++){
			System.out.println("valeur propre " + i + " : " + vp.valeurPropreTrie[i]);
		}
			
        System.out.println("--------------------------------------------------------------");

        for (int i = 0; i < vp2.size(); i++){
			System.out.println("valeur propre " + i + " : " + vp2.get(i));
		}
        */





    }






    //----------------------------------------------------------------------------------------------------------------------------
    //------- FONCTION RECONNAISSANCE FACIALE ------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------
	
    /**
    * @author Nathan Havard
    * @param String du dossier avec les photo reference
    * @return List<Image> la liste d'image des photos de ce dossier 
    * @brief recupere toute les photos du dossier (.pgm) et les converti en classe Image
    */
    public static List<Image> chargerImages(String dossier){
        List<Image> liste = new ArrayList<>();
        File racine = new File(dossier);

        File[] personnes = racine.listFiles(File::isDirectory);   // sous-dossiers = personnes
        if (personnes == null){
            throw new RuntimeException("Dossier introuvable : " + dossier);
        }
        Arrays.sort(personnes);  

        for (File personne : personnes){
            File[] fichiers = personne.listFiles((d, nom) -> nom.endsWith(".pgm"));
            if (fichiers == null) continue;
            Arrays.sort(fichiers);   // 1.pgm, 2.pgm, ...

            for (File f : fichiers){
                liste.add(new Image(f.getPath()));   // le constructeur lit le PGM + extrait les ids
            }
        }
        return liste;
    }


    /**
    * @author Jules Turchi et Nathan Havard
    * @param visages objet Visages contenant la matrice centrée A et la matrice de covariance réduite A^T*A
    * @brief Constructeur de l'ACP. Calcule les valeurs/vecteurs propres de A^T*A, détermine le nombre
    * de valeurs propres à garder, en déduit les eigenfaces puis projette la base pour obtenir les signatures (omega).
    */
	public static void reconnaissanceFaciale() {
		//------- Pré-traitement ---------------------------------------------------------------------------------
		
        /*création d'un objet de prétraitement*/
		//Pretraitement pt = new PreTraitement(92,112); 
		
		
		/*application du prétraitement*/
		//pt.verifierConformite(chemin);
		//Image imgTraitee = new Image(chemin);
		
		
		
		//------- Création Image depuis nos donnée -------------------------------------------------------------------

        List<Image> liste = chargerImages("donnees/donnee/reference");

		
		//------- ACP -------------------------------------------------------------------------------------------

        Visages visages = new Visages(liste);
        ACP acp = new ACP(visages);


		//------- Export du visage moyen et des eigenfaces en PGM -----------------------------------------------

        acp.exporterPGM("sortie_pgm", 92, 112);
        System.out.println("Visage moyen et eigenfaces exportes dans le dossier sortie_pgm");


    	//------- Calculer résultat pour une image -----------------------------------------------------------------------------
        Image imageAanalyser = new Image("donnees/donnee/test/connus/axel/10.pgm");
        //Image imageAanalyser = new Image("donnees/donnee/test/inconnus/jeanne/1.pgm");
        Visages visagesImageAnalyser = new Visages(imageAanalyser, visages);
        double[] res = ACP.identification(acp.getOmega(), visagesImageAnalyser.getImageAanalyser(), acp.getTabEingenface(),acp.getNbValeurPropre());
        
        




        //--------CRITERE 1 ---------------------------------------------------------------------------
        
        double seuil = calculSeuilCritereTeta(visages,acp);
        System.out.println("Seuil : " +  seuil);






        //--------CRITERE 2 ---------------------------------------------------------------------------

        // 1 On charge les images de validation (ici les "connus")
        List<Image> listeValidation = chargerImages("donnees/donnee/test/connus");

        // 2 On construit la matrice (l x n) : une ligne = un visage centré
        double[][] visagesValidation = new double[listeValidation.size()][];
        for (int k = 0; k < listeValidation.size(); k++) {

            // centrage par rapport au visage moyen de la base (comme dans la méthode 1)
            SimpleMatrix centre = new Visages(listeValidation.get(k), visages).getImageAanalyser(); // (n x 1)

            int nbPixels = centre.numRows();
            double[] vecteur = new double[nbPixels];
            for (int p = 0; p < nbPixels; p++) {
                vecteur[p] = centre.get(p, 0);      // colonne -> tableau
            }
            visagesValidation[k] = vecteur;
        }

        // 3 LA ligne que tu cherches :
        double[] erreur = calculerErreurValidation(visagesValidation, acp.getTabEingenface());

        // 4 (suite logique) seuil Or au 95e percentile
        double seuilReconstruction = calculerSeuil(erreur, 95.0);

        System.out.println("Seuil teta : " + seuilReconstruction);
        System.out.println("Erreurs de reconstruction : " + Arrays.toString(erreur));


        // ---------- CRITÈRE 3 : Hotelling T² (seuil calibré empiriquement) -----------------------

        // Valeurs propres recalculées depuis A^T*A (non stockées dans l'ACP)
        ListePropre listepropre = ACP.calculerValeurPropre(visages.getMatrixD());

        // T² du visage à analyser
        double T2 = calculerT2(visagesImageAnalyser.getImageAanalyser(), acp, listepropre);

        // Seuil calibré sur les visages connus/inconnus de test (comme les critères 1 et 2)
        double seuilT2 = calculSeuilCritereT2(visages, acp);

        // Décision
        System.out.printf("T2 = %.3f   |   seuil calibre = %.3f%n", T2, seuilT2);
        if (T2 > seuilT2) {
            System.out.println("=> Visage INCONNU (T2 > seuil)");
        } else {
            System.out.println("=> Visage connu (T2 <= seuil)");
        }






        System.out.println("\nOn analyser notre image choisi");
        System.out.println("Voici l'indice_min : " + res[0]);
        System.out.println("Voici la distance min : " + res[1]);
        int indice = (int) Math.round(res[0]);
        Image imageReconnue = liste.get(indice);
        System.out.println("Personne reconnue : " + imageReconnue.getIdPersonne());

    }











    //----------------------------------------------------------------------------------------------------------------------------
    //------- CRITERE 1 ----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------
		

    /**
	 * @author Jules Turchi
     * @param acp image déjà traitée ie sous forme de vecteur et centrée 
	 * @param visages tableau contenant les eigenfaces
     * @return un liste de double qui correspond au seuil
	 * @brief Fonction qui calcule le seuil avec la méthode 1 du critère teta 
	*/
    public static double calculSeuilCritereTeta(Visages visages ,ACP acp){


        // --------- DISTANCE POUR LES IMAGES CONNUES ---------------------------------------------------------------------------------------------------------------------------------------------
        List<Image> listeImgConnues = chargerImages("donnees/donnee/test/connus");
        
        List<Visages> listeVisagesConnues = new ArrayList<>();

        List<Double> listeDistanceConnues = new ArrayList<>();


        for( int i=0 ; i<listeImgConnues.size() ; i++){


            listeVisagesConnues.add(new Visages(listeImgConnues.get(i), visages));
            double[] res = acp.identification(acp.getOmega(), listeVisagesConnues.get(i).getImageAanalyser(), acp.getTabEingenface(),acp.getNbValeurPropre());
            listeDistanceConnues.add(res[1]);
        
        }
        double maxDistConnues = Collections.max(listeDistanceConnues);



        // --------- DISTANCE POUR LES IMAGES INCONNUES ---------------------------------------------------------------------------------------------------------------------------------------------
        List<Image> listeImgInconnues = chargerImages("donnees/donnee/test/inconnus");

        List<Visages> listeVisagesInconnues = new ArrayList<>();

        List<Double> listeDistanceInconnues = new ArrayList<>();


        for( int i=0 ; i<listeImgInconnues.size() ; i++){

            listeVisagesInconnues.add(new Visages(listeImgInconnues.get(i), visages));
            double[] res = acp.identification(acp.getOmega(), listeVisagesInconnues.get(i).getImageAanalyser(), acp.getTabEingenface(),acp.getNbValeurPropre());
            listeDistanceInconnues.add(res[1]);
        }
        double maxDistInconnues = Collections.min(listeDistanceInconnues);



        // --------- CALCUL SEUIL ------------------------------------------------------------------------------------------------------------------------------------------------------------------
        double seuil = (maxDistConnues + maxDistInconnues) / 2.0;

        return seuil;
    }

    
    



    
    
    //----------------------------------------------------------------------------------------------------------------------------
    //------- CRITERE 2 ----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------
		



    public static double calculerErreur(double[] j, double[] jp) {
    	double somme = 0.0;
    	for (int i=0;i<j.length; i++) {
    		somme += (j[i]-jp[i])*(j[i]-jp[i]);
    	}
    	return Math.sqrt(somme);
    }



    /**
     * @author Marie Santini
     * @param J le visage centré à reconstruire (vecteur de taille n)
     * @param eigenfaces matrice (n x p) des eigenfaces
     * chaque colonne est une eigenface
     * @return le visage reconstruit Jp
     */
    public static double[] reconstruire(double[] j, double[][] eigenfaces) {
        double[] jp = new double[j.length];
        for (int i = 0; i < eigenfaces[0].length; i++) {
            double coord = 0.0;
            for (int k = 0; k < j.length; k++) {
                coord += j[k] * eigenfaces[k][i];
            }
            for (int k = 0; k < j.length; k++) {
                jp[k] += coord * eigenfaces[k][i];
            }
        }
        return jp;
    }
    /**
     * @author Marie Santini
     * @param erreurs tableau des erreurs de reconstruction des visages de validation
     * @param percentile le percentile choisi, 95.0% ou 99.0%
     * @return le seuil Or
     */
    public static double calculerSeuil(double[] erreurs, double percentile) {
        double[] triees = erreurs.clone();
        Arrays.sort(triees);
        int index = (int) Math.ceil((percentile / 100.0) * triees.length) - 1;
        return triees[index];
    }




    /**
     * @author Marie Santini
     * @param visagesValidation matrice (l x n) contenant les l visages de validation centrés,
     * chaque ligne est un visage de n pixels
     * @param eigenfaces matrice (n x p) des eigenfaces,
     * chaque colonne est une eigenface
     * @return tableau de taille l contenant les erreurs
     */
    public static double[] calculerErreurValidation(double[][] visagesValidation, double[][] eigenfaces) {
    	double[] erreurs = new double[visagesValidation.length];
    	for (int k = 0; k<visagesValidation.length; k++) {
    		double[] jp = reconstruire(visagesValidation[k], eigenfaces);
            erreurs[k] = calculerErreur(visagesValidation[k], jp);
        }
        return erreurs;
    }



    //----------------------------------------------------------------------------------------------------------------------------
    //-------CRITERE 3 ----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------
	

    /**
     * @author Marie Santini
     * @param imageTest image test centrée sous forme de vecteur colonne
     * @param tabEigenface tableau 2D contenant les eigenfaces
     * @param nbEigenface nombre d'eigenfaces à utiliser pour la projection
     * @return tableau de double contenant les coordonnées beta du visage dans l'espace ACP
     * @brief Calcule les coordonnées de projection d'un visage test dans la base ACP.
     * 		  Pour chaque eigenface i, calcule le produit scalaire entre l'image et l'eigenface.
     */
    
    public static double[] calculerBeta(SimpleMatrix imageTest, double[][] tabEigenface, int nbEigenface) {
    	int nbPixels = imageTest.numRows();
        double[] beta = new double[nbEigenface];

        for (int i = 0; i < nbEigenface; i++) {
            double prodScal = 0.0;
            for (int k = 0; k < nbPixels; k++) {
                prodScal += tabEigenface[k][i] * imageTest.get(k, 0);
            }
            beta[i] = prodScal;
        }
        return beta;
    }
 
    /**
     * @author Marie Santini
     * @param listepropre objet ListePropre contenant les valeurs propres triées par ordre décroissant
     * @param nbEigenface nombre de valeurs propres à extraire
     * @return tableau de double contenant les nbEigenface premières valeurs propres.
     * @brief Extrait les nbEigenface premières valeurs propres depuis la liste triée.
     *        Ces valeurs propres lambda représentent la variance de chaque composante principale
     *        et sont utilisées pour normaliser les scores dans le calcul de la statistique T².
     */
    public static double[] calculerLambda(ListePropre listepropre, int nbEigenface) {
        double[] lambda = new double[nbEigenface];
        for (int i = 0; i < nbEigenface; i++) {
            lambda[i] = listepropre.valeurPropreTrie[i];
        }
        return lambda;
    }
    
    /**
     * @author Marie Santini
     * @param beta tableau de double contenant les coordonnées de projection du visage dans l'espace ACP.
     * @param lambda tableau de double contenant les variances des composantes principales (valeurs propres).
     * @return la statistique de Hotelling T² sous forme de double
     * @brief Calcule la statistique de Hotelling T² : 
     *        Cette statistique mesure si le visage testé appartient à la région de l'espace ACP
     *        habituellement occupée par les visages de la base de référence.
     *        Un T² faible indique un visage reconnu, un T² élevé indique un visage inconnu.
     */

    public static double calculStat(double[] beta, double[] lambda) {
    	double t2 = 0.0;
    	int k = beta.length;
    	for (int i=0; i<k; i++) {
    		t2 += (beta[i]*beta[i]) / lambda[i];
    	}
    	return t2;
    }
    
    /**
     * @author Marie Santini
     * @param n nombre d'images dans la base d'apprentissage
     * @param K nombre de composantes retenues (nbEigenface)
     * @return le seuil théorique T²_alpha
     * @brief Calcule le seuil théorique de la statistique de Hotelling T²
     */
    public static double calculerSeuilTheorique(int n, int k) {
        return (k * (n - 1.0)) / (n - k);
    }


    /**
     * @author Marie Santini
     * @param imageCentre image test déjà centrée (vecteur colonne n x 1)
     * @param acp l'ACP contenant les eigenfaces et le nombre de composantes retenues
     * @param listepropre les valeurs propres triées (variances des composantes)
     * @return la statistique de Hotelling T² du visage
     * @brief Enchaine projection (beta), recuperation des variances (lambda) et calcul du T².
     */
    public static double calculerT2(SimpleMatrix imageCentre, ACP acp, ListePropre listepropre) {
        double[] beta = calculerBeta(imageCentre, acp.getTabEingenface(), acp.getNbValeurPropre());
        double[] lambda = calculerLambda(listepropre, acp.getNbValeurPropre());
        return calculStat(beta, lambda);
    }


    /**
     * @author Marie Santini
     * @param visages objet Visages contenant la base d'apprentissage (visage moyen)
     * @param acp l'ACP calculee sur la base
     * @return le seuil de decision sur le T², calibre empiriquement
     * @brief Calcule le T² des visages connus et inconnus de test, puis renvoie un seuil
     *        qui separe les deux (milieu entre le plus grand T² connu et le plus petit T² inconnu).
     *        Approche identique au critere 1 : evite la loi de Fisher et le probleme d'echelle.
     */
    public static double calculSeuilCritereT2(Visages visages, ACP acp) {

        // Valeurs propres (non stockees dans l'ACP) recalculees depuis A^T*A
        ListePropre listepropre = ACP.calculerValeurPropre(visages.getMatrixD());

        // --------- T² POUR LES IMAGES CONNUES ---------
        List<Image> listeImgConnues = chargerImages("donnees/donnee/test/connus");
        List<Double> listeT2Connues = new ArrayList<>();
        for (int i = 0; i < listeImgConnues.size(); i++) {
            SimpleMatrix centre = new Visages(listeImgConnues.get(i), visages).getImageAanalyser();
            listeT2Connues.add(calculerT2(centre, acp, listepropre));
        }
        double maxT2Connues = Collections.max(listeT2Connues);

        // --------- T² POUR LES IMAGES INCONNUES ---------
        List<Image> listeImgInconnues = chargerImages("donnees/donnee/test/inconnus");
        List<Double> listeT2Inconnues = new ArrayList<>();
        for (int i = 0; i < listeImgInconnues.size(); i++) {
            SimpleMatrix centre = new Visages(listeImgInconnues.get(i), visages).getImageAanalyser();
            listeT2Inconnues.add(calculerT2(centre, acp, listepropre));
        }
        double minT2Inconnues = Collections.min(listeT2Inconnues);

        // --------- SEUIL : milieu entre les deux ---------
        return (maxT2Connues + minT2Inconnues) / 2.0;
    }










    //----------------------------------------------------------------------------------------------------------------------------
    //------- FONCTION MAIN ------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------
	
    
    
	public static void main(String[] args){

        System.out.println("Bienvenue sur notre application de reconnaissance faciale !");
        reconnaissanceFaciale();
        //test();

	}
    

}
