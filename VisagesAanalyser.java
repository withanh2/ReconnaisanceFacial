import org.ejml.simple.SimpleMatrix;
import java.util.List;

/**
 * @author Nathan HAVARD
 * @brief Ensemble de visages à analyser. La matrice centrée est stockée dans ImageAanalyser.
 */
public class VisagesAanalyser extends Visages{
    //------------------------------------------------------------------------------------------------------------
    //------- DEFINITION DES VARIABLES ---------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private SimpleMatrix ImageAanalyser;

    //------------------------------------------------------------------------------------------------------------
    //------- CONSTRUCTEURS --------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    /**
     * @author Nathan HAVARD
     * @param images liste des images de référence à utiliser
     * @brief Constructeur de VisagesAanalyser. Initialise la partie commune (Visages) puis la matrice A (matrice vecteur sans le visage moyen).
     */
    public VisagesAanalyser(List<Image> images){
        super(images);
        this.ImageAanalyser = centrerMatrice();
    }
}
