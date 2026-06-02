import org.ejml.simple.SimpleMatrix;
import java.util.List;

/**
 * @author Nathan HAVARD
 * @brief Ensemble de visages de référence (liste d'images). La matrice centrée est stockée dans matrixA.
 */
public class VisagesListeImage extends Visages{
    //------------------------------------------------------------------------------------------------------------
    //------- DEFINITION DES VARIABLES ---------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private SimpleMatrix matrixA;

    //------------------------------------------------------------------------------------------------------------
    //------- CONSTRUCTEURS --------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    /**
     * @author Nathan HAVARD
     * @param images liste des images de référence à utiliser
     * @brief Constructeur de VisagesListeImage. Initialise la partie commune (Visages) puis la matrice A (matrice vecteur sans le visage moyen).
     */
    public VisagesListeImage(List<Image> images){
        super(images);
        this.matrixA = centrerMatrice();
    }

    //------------------------------------------------------------------------------------------------------------
    //------- GETTERS ET SETTERS ---------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    /**
     * @author Nathan HAVARD
     * @return SimpleMatrix matrice centrée (matrice A)
     * @brief Retourne la matrice A.
     */
    public SimpleMatrix getMatrixA(){
        return matrixA;
    }

    /**
     * @author Nathan HAVARD
     * @param matrixA matrice centrée (matrice A)
     * @brief Modifie la matrice A.
     */
    public void setMatrixA(SimpleMatrix matrixA){
        this.matrixA = matrixA;
    }
}
