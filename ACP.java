
import java.util.Arrays;
import org.ejml.simple.SimpleMatrix;  // Module

public class ACP {
    

    //------------------------------------------------------------------------------------------------------------
    //------- DEFINITION DES VARIABLES ---------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private Visage visage;

    private double[][] valeurPropre;

    private double[][] vecteurPropre;

    private int nb_vecteurPropre;


    //------------------------------------------------------------------------------------------------------------
    //------- CONSTRUCTEURS -------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


    public ACP(Visage visage){

        this.visage = visage;
        this.nb_vecteurPropre = 0;
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


    public static double[][] calculer_ValeurPropre(SimpleMatrix matrice){



    }








    

    
}