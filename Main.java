import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleEVD;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


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
        System.out.println(acp.getNb_valeurPropre() + "\n");

        System.out.println("Tableau eigenface");
        System.out.println(Arrays.deepToString(acp.getTab_eigenface())+"\n");

        System.out.println("Omega");
        System.out.println(Arrays.deepToString(acp.getOmega()));


    	// ------- Création de l'image à tester -------------------------------------------------------------------------------------


		SimpleMatrix matAanalyser1 = new SimpleMatrix(new double[][]{{10.0}, {9.0},{4.0}, {45.0}});
        Image imageAanalyser = new Image(matAanalyser1);
        Visages visagesImageAnalyser = new Visages(imageAanalyser, visages);
        
        
        
        // Application de l'ACP
        double[] res = ACP.identification(acp.getOmega(), visagesImageAnalyser.getImageAanalyser(), acp.getTab_eigenface(),acp.getNb_valeurPropre());
        
        // Affichage des resultats de l'ACP
        System.out.println("\n\nOn analyser notre image de test");
        System.out.println("Voici l'indice_min : " + res[0]);
        System.out.println("Voici la distance min : " + res[1]);



    	//------- Option affichage des valeurs propres -----------------------------------------------------------------------------

    */

        /*
		ListePropre vp = calculer_ValeurPropre(matExemple);
        ArrayList<Double> vp2 = nb_vp_a_garder(vp.valeurPropre_Trie);
		for (int i = 0; i < vp.valeurPropre_Trie.length; i++){
			System.out.println("valeur propre " + i + " : " + vp.valeurPropre_Trie[i]);
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
    * @author Jules Turchi et Nathan Havard
    * @param visages objet Visages contenant la matrice centrée A et la matrice de covariance réduite A^T*A
    * @return double[] qui contient la liste des valeurs propres
    * @brief Constructeur de l'ACP. Calcule les valeurs/vecteurs propres de A^T*A, détermine le nombre
    * de valeurs propres à garder, en déduit les eigenfaces puis projette la base pour obtenir les signatures (omega).
    */
	public static Image reconnaissanceFaciale() {
		//------- Pré-traitement ---------------------------------------------------------------------------------
		
        /*création d'un objet de prétraitement*/
		//Pretraitement pt = new PreTraitement(92,112); 
		
		
		/*application du prétraitement*/
		//pt.verifierConformite(chemin);
		//Image imgTraitee = new Image(chemin);
		
		
		
		//------- Création Image depuis nos donnée -------------------------------------------------------------------



		
		//------- ACP -------------------------------------------------------------------------------------------


		List<Image> liste = chargerImages("donnees/donnee/reference");
        Visages visages = new Visages(liste);
        ACP acp = new ACP(visages);
        

    	//------- Calculer résultat -------------------------------------------------------------------------------------
        SimpleMatrix matAanalyser1 = new SimpleMatrix(new double[][]{{10.0}, {9.0},{4.0}, {45.0}});
        Image imageAanalyser = new Image(matAanalyser1);
        Visages visagesImageAnalyser = new Visages(imageAanalyser, visages);
        double[] res = ACP.identification(acp.getOmega(), visagesImageAnalyser.getImageAanalyser(), acp.getTab_eigenface(),acp.getNb_valeurPropre());
        System.out.println("\n\nOn analyser notre image de test");
        System.out.println("Voici l'indice_min : " + res[0]);
        System.out.println("Voici la distance min : " + res[1]);
        
        return 

    }
    
	public static void main(String[] args){

        System.out.println("Bienvenue sur notre application de reconnaissance faciale !");


	}
    

}
