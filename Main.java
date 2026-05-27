import org.ejml.simple.SimpleMatrix;
import java.io.*;


public class Main {

	

	public static void main(String[] args){

    
    
	    SimpleMatrix matExemple = new SimpleMatrix(new double[][]{
        {1.0, 2.0, 3.0},
        {4.0, 5.0, 6.0},
        {7.0, 8.0, 9.0}
        });
			
			
		double[] vp = calculer_ValeurPropre(matExemple);

        System.out.println("=>",vp[0],"=>",vp[1]);

	}

    public static double[] calculer_ValeurPropre(SimpleMatrix matrice){

        var decomposition = matrice.eig();

        int taille = matrice.numRows();
        double[] valeurPropre = new double[taille];

        for (int i=0; i<taille; i++){
            valeurPropre[i] = decomposition.getEigenvalue(i).getReal();
        }

    return valeurPropre;
    }
	
}
