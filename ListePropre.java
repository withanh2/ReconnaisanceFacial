/** 
 * @author Nathan HAVARD
 * @brief Conteneur regroupant les valeurs propres et les vecteurs propres associés, triés dans l'ordre décroissant des valeurs propres.
 */
public class ListePropre{
    public double[] valeurPropreTrie;
    public double[][] vecteurPropreTrie;

    /** 
     * @author Nathan HAVARD
     * @param double[] valeurPropreTrie tableau des valeurs propres triées en ordre décroissant
     * @param double[][] vecteurPropreTrie tableau des vecteurs propres associés, dans le même ordre que les valeurs propres
     * @brief Constructeur de la classe ListePropre.
     */
    public ListePropre(double[] valeurPropreTrie ,double[][] vecteurPropreTrie){
        this.valeurPropreTrie = valeurPropreTrie;
        this.vecteurPropreTrie = vecteurPropreTrie;
    }
}
