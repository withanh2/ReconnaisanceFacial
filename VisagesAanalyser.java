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

    //------------------------------------------------------------------------------------------------------------
    //------- GETTERS ET SETTERS ---------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    /**
     * @author Nathan HAVARD
     * @return SimpleMatrix matrice centrée de l'image à analyser
     * @brief Retourne la matrice de l'image à analyser.
     */
    public SimpleMatrix getImageAanalyser(){
        return ImageAanalyser;
    }

    /**
     * @author Nathan HAVARD
     * @param ImageAanalyser matrice centrée de l'image à analyser
     * @brief Modifie la matrice de l'image à analyser.
     */
    public void setImageAanalyser(SimpleMatrix ImageAanalyser){
        this.ImageAanalyser = ImageAanalyser;
    }
}
