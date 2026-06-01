/** 
 * @author Nathan HAVARD
 * @brief Conteneur regroupant les valeurs propres et les vecteurs propres associés, triés dans l'ordre décroissant des valeurs propres.
 */
public class ListePropre{
    public double[] valeurPropre_Trie;
    public double[][] vecteurPropre_Trie;

    /** 
     * @author Nathan HAVARD
     * @param double[] valeurPropre_Trie tableau des valeurs propres triées en ordre décroissant
     * @param double[][] vecteurPropre_Trie tableau des vecteurs propres associés, dans le même ordre que les valeurs propres
     * @brief Constructeur de la classe ListePropre.
     */
    public ListePropre(double[] valeurPropre_Trie ,double[][] vecteurPropre_Trie){
        this.valeurPropre_Trie = valeurPropre_Trie;
        this.vecteurPropre_Trie = vecteurPropre_Trie;
    }
}
