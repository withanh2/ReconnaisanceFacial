import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleEVD;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {

	

	public static void main(String[] args){

    
    
	    SimpleMatrix matExemple = new SimpleMatrix(new double[][]{
        {1.0, 1.0, 1.0},
        {1.0, 1.0, 1.0},
        {3.0, 0.0, 0.0}
        });
			
			
		ListePropre vp = calculer_ValeurPropre(matExemple);
        ArrayList<Double> vp2 = nb_vp_a_garder(vp.valeurPropre_Trie);
		for (int i = 0; i < vp.valeurPropre_Trie.length; i++){
			System.out.println("valeur propre " + i + " : " + vp.valeurPropre_Trie[i]);
		}
			
        System.out.println("--------------------------------------------------------------");

        for (int i = 0; i < vp2.size(); i++){
			System.out.println("valeur propre " + i + " : " + vp2.get(i));
		}

	}





    

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






/*



    public static ListePropre ACP(ArrayList<Double> liste_ValPropre, ListePropre listepropre){








    }



    */

        


	
}
