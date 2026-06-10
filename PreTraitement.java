
import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class PreTraitement {

	private int longueurCible;
	private int hauteurCible;
	private boolean estNivGris;
	
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

	 //------------------------------------------------------------------------------------------------------------
    //------- GETTER --------------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


	
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



	 //------------------------------------------------------------------------------------------------------------
    //------- FONCTIONS --------------------------------------------------------------------------------------------- 
    //------------------------------------------------------------------------------------------------------------


	
	/** 
	 * @author Maxime Le Glanaër
	 * @param String chemin le chemin vers l'image que l'on doit vérifier
	 * @return SimpleMatrix versMatriceGris(img) la matrice en niveau de gris conforme au model de la BDD
	 * @brief charge une image pour pouvoir la manipuler.
	 */
	public static SimpleMatrix chargerImage(String chemin) throws IOException {
		
		String ext = chemin.substring(chemin.lastIndexOf('.') + 1).toLowerCase();
		if (ext.equals("pgm")) {
			return lirePGM(chemin);
		}
    	BufferedImage img = ImageIO.read(new File(chemin));
    	if (img == null) {
        	throw new UnsupportedOperationException("Format non supporté : " + chemin);
    	}
    	return versMatriceGris(img);
	}

	/**
	 * @author Nathan havard
	 * @param String chemin le chemin vers le fichier PGM (P5)
	 * @return SimpleMatrix matrice (hauteur x largeur) des niveaux de gris [0,255]
	 * @brief Lit un PGM binaire (P5) en sautant son en-tête (magic, dimensions, valeur max).
	 */
	private static SimpleMatrix lirePGM(String chemin) throws IOException {
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(chemin)))) {
			String magic = lireLigneUtile(dis);
			if (magic == null || !magic.startsWith("P5")) {
				throw new IOException("Format PGM non supporté (attendu P5).");
			}
			String[] dim = lireLigneUtile(dis).trim().split("\\s+");
			int largeur = Integer.parseInt(dim[0]);
			int hauteur = Integer.parseInt(dim[1]);
			lireLigneUtile(dis); // valeur max (255), ignorée

			SimpleMatrix matrice = new SimpleMatrix(hauteur, largeur);
			for (int y = 0; y < hauteur; y++) {
				for (int x = 0; x < largeur; x++) {
					matrice.set(y, x, dis.readUnsignedByte());
				}
			}
			return matrice;
		}
	}

	/** Prochaine ligne d'en-tête non vide et non commentée (#). */
	@SuppressWarnings("deprecation")
	private static String lireLigneUtile(DataInputStream dis) throws IOException {
		String ligne = dis.readLine();
		while (ligne != null && (ligne.isEmpty() || ligne.startsWith("#"))) {
			ligne = dis.readLine();
		}
		return ligne;
	}


	/**
 	* @author Maxime Le Glanaër
 	* @param BufferedImage img l'image RGB à convertir
 	* @return SimpleMatrix la matrice de pixels en niveaux de gris, valeurs dans [0.0, 255.0]
 	* @brief Convertit une BufferedImage RGB en SimpleMatrix de niveaux de gris
 	* en appliquant la formule de luminance standard ITU-R BT.601.
 	*/
	public static SimpleMatrix versMatriceGris(BufferedImage img) {
    	int hauteur = img.getHeight();
    	int largeur  = img.getWidth();
    	SimpleMatrix matrice = new SimpleMatrix(hauteur, largeur);
    	for (int y = 0; y < hauteur; y++) {
        	for (int x = 0; x < largeur; x++) {
            	int pixel = img.getRGB(x, y);
            	int r = (pixel >> 16) & 0xFF;
            	int g = (pixel >> 8) & 0xFF;
            	int b = (pixel) & 0xFF;
            	double gris = (0.299 * r + 0.587 * g + 0.114 * b);
            	matrice.set(y, x, gris);
        	}
    	}
    	return matrice;
	}
	
	/** 
	 * @author Maxime Le Glanaër
	 * @param BufferedImage img l'image que l'on doit vérifier
	 * @brief Vérifie si une image a la bonne taille selon le traitement manipulé et est en niveau de gris si nécessaire
	 */
	public void verifierConformite(String chemin) throws IOException {
		SimpleMatrix img = chargerImage(chemin);
	    if (img == null) {
	        throw new IllegalArgumentException("Image non chargée (fichier introuvable ou format non supporté).");
	    }
		int hauteur = img.getNumRows();
		int largeur  = img.getNumCols();
	    if (largeur != this.getLongueurCible() || hauteur != this.getHauteurCible()) {
	        throw new IllegalArgumentException("Dimensions incorrectes : attendu " + this.getLongueurCible() + "×" + this.getHauteurCible() + ", reçu " + largeur + "×" + hauteur);
	    }
	    if (this.getEstNivGris()){
	    	for (int i = 0; i < hauteur; i++) {
		        for (int j = 0; j < largeur; j++) {
		            if (img.get(i, j) < 0.0 || img.get(i, j) > 255.0) {
    					throw new IllegalArgumentException("Valeur de pixel hors de [0,255] en (" + i + "," + j + ")");
					}
				}
			}
	    }
	}
	
	
}
