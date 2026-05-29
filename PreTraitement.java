
import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class PreTraitement {

	int longueurCible;
	int hauteurCible;
	boolean estNivGris;
	
	/*
	 * @author Maxime Le Glanaër
	 * @param int n la taille de la matrice carrée standard dans notre BDD, boolean gris indicateur de si on veut une image en niv de gris ou non.
	 * @brief Constructeur d'un prétraitement pour les images externes avant de les comparer avec celles de la BDD.
	 */
	public PreTraitement(int longueur,int haut, boolean gris){
		this.longueurCible = longueur;
		this.hauteurCible = haut;
		this.estNivGris = gris;
	}
	
	public PreTraitement(int n, int m) {
		this(n,m,true);
	}
	
	public PreTraitement(int n) {
		this(n,n,true);
	}
	
	/*
	 * @author Maxime Le Glanaër
	 * @return int tailleCible du pré-traitement manipulé.
	 * @brief Accesseur de la taille cible d'un prétraitement
	 */
	public int getLongueurCible() {
		return(this.longueurCible);
	}
	
	/*
	 * @author Maxime Le Glanaër
	 * @return int tailleCible du pré-traitement manipulé.
	 * @brief Accesseur de la taille cible d'un prétraitement
	 */
	public int getHauteurCible() {
		return(this.hauteurCible);
	}
	
	/*
	 * @author Maxime Le Glanaër
	 * @return boolean estNivGris, si oui ou non l'image doit être grise
	 * @brief Accesseur de estNivGris
	 */
	public boolean getEstNivGris() {
		return(this.estNivGris);
	}
	
	/*
	 * @author Maxime Le Glanaër
	 * @param BufferedImage img l'image que l'on doit vérifier
	 * @brief Vérifie si une image a la bonne taille selon le traitement manipulé et est en niveau de gris si nécessaire
	 */
	public void verifierConformite(BufferedImage img) {
	    if (img == null) {
	        throw new IllegalArgumentException("Image non chargée (fichier introuvable ou format non supporté).");
	    }

	    if (img.getWidth() != this.getLongueurCible() || img.getHeight() != this.getHauteurCible()) {
	        throw new IllegalArgumentException("Dimensions incorrectes : attendu " + this.getLongueurCible() + "×" + this.getHauteurCible() + ", reçu " + img.getWidth() + "×" + img.getHeight());
	    }
	    if (this.getEstNivGris()){
	    	for (int i = 0; i < img.getHeight(); i++) {
		        for (int j = 0; j < img.getWidth(); j++) {
		            int pixel = img.getRGB(i, j);
		            int r = (pixel >> 16) & 0xFF;
		            int g = (pixel >> 8) & 0xFF;
		            int b = (pixel) & 0xFF;
		            if (r != g || g!=b) {
		            	throw new IllegalArgumentException("L'image n'est pas en niveau de gris, inutilisable");
		            }
				}
			}
	    }
	}
	
	/*
	 * @author Maxime Le Glanaër
	 * @param BufferedImage img l'image extérieure à transformer
	 * @return Image imgRep l'image transformée
	 * @brief Transforme l'image extérieure (matrice de pixels) supposée vérifiée en Image.
	 */
	public DMatrixRMaj transformer(BufferedImage img) {
		DMatrixRMaj imgRep = new DMatrixRMaj(this.getLongueurCible(), this.getHauteurCible());
		for (int i = 0; i < img.getHeight(); i++) {
	        for (int j = 0; j < img.getWidth(); j++) {
	            int pixel = img.getRGB(i, j);
	            int r = (pixel >> 16) & 0xFF;
	            imgRep.set(i, j, r);
			}
		}
		return (imgRep);
	}
	
}
