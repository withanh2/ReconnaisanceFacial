
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
	
	/** 
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
	
	/** 
	 * @author Maxime Le Glanaër
	 * @return int tailleCible du pré-traitement manipulé.
	 * @brief Accesseur de la taille cible d'un prétraitement
	 */
	public int getLongueurCible() {
		return(this.longueurCible);
	}
	
	/** 
	 * @author Maxime Le Glanaër
	 * @return int tailleCible du pré-traitement manipulé.
	 * @brief Accesseur de la taille cible d'un prétraitement
	 */
	public int getHauteurCible() {
		return(this.hauteurCible);
	}
	
	/**
	 * @author Maxime Le Glanaër
	 * @return boolean estNivGris, si oui ou non l'image doit être grise
	 * @brief Accesseur de estNivGris
	 */
	public boolean getEstNivGris() {
		return(this.estNivGris);
	}

	/** 
	 * @author Maxime Le Glanaër
	 * @param String chemin le chemin vers l'image que l'on doit vérifier
	 * @brief charge une image dans le buffer pour pouvoir la manipuler.
	 */
	public static double[][] chargerImage(String chemin) throws IOException {
    String ext = chemin.substring(chemin.lastIndexOf('.') + 1).toLowerCase();
    return switch (ext) {
        case "pgm" -> lirePGM(chemin);
        case "jpg", "jpeg","png", "bmp"  -> {
            BufferedImage img = ImageIO.read(new File(chemin));
        }
        default -> throw new UnsupportedOperationException("Format non supporté : " + ext);
    	};
	}
	
	/** 
	 * @author Maxime Le Glanaër
	 * @param BufferedImage img l'image que l'on doit vérifier
	 * @brief Vérifie si une image a la bonne taille selon le traitement manipulé et est en niveau de gris si nécessaire
	 */
	public void verifierConformite(String chemin) {
		double[][] img = chargerImage(chemin)
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
	
	
}
