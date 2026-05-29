package CodeDiagramme;

public class Identificateur {
	/*Objet ACP contient les eigenfaces et la base de visages de référence. 
	 * Il est utilisé pour projeter les images dans l'espace*/
	private ACP acp;
	/*Seuil de distance en dessous duquel un visage est reconnu*/
	private float seuil;
	/*Type de distance utlisé pour comparer les vecteurs de projection*/
	private TypeDistance typeDistance;
	
	/*Getter de l'attribut seuil*/
	public float getSeuil() {
		return seuil;
	}
	/*Setter de l'attribut seuil*/
	public void setSeuil(float seuil) {
		this.seuil = seuil;
	}
	/*Constructeur complet*/
	public Identificateur(ACP acp, float seuil, TypeDistance typeDistance) {
		this.acp=acp;
		this.seuil=100;
		this.typeDistance=typeDistance;
	}
	
	/**
	 * Identifie un visage dans une image en la comparant à la base de référence
	 * Projette l'image dans l'espace ACP, calcule les distances avec chaque visage 
	 * de référence, et retourne un résultat contenant l'image la plus proche ainsi 
	 * que les informations de reconnaissance
	 * @param img L'image contenant le visage à identifier
	 * @return Un objet contenant l'image testée, l'image de référence la plus proche,
	 * la distance minimale calculée, un booléen qui indique si le visage est reconnu,
	 * le nom de la personne reconnue ou "inconnu"
	 */
	public Resultat identifier(Image img) {
		/*signature numérique de l'image test*/
		double[] vecteurTest = acp.projeter(img);
		/*récupère la liste de tous les visages de référence*/
		Visages visages = acp.getVisages(); 
		/*initialise distance min à la plus grande valeur*/
		double distanceMin = Double.MAX_VALUE; 
		/*variable qui stocke l'image de référence la plus ressemblante*/
		Image imgPlusProche = null; 
		for (Image imgRef : visages.getImages()) {
			double[] vecteurRef = acp.projeter(imgRef); /*calcul projection dans acp pour chaque image*/
			double distance = calculerDistance(vecteurTest, vecteurRef); /*distance entre signature image test et référence*/
			/*Le but de cette fonction est de trouver le visage le plus ressemblant à la fin de la boucle*/
			if (distance < distanceMin) {
				distanceMin = distance;
				imgPlusProche = imgRef;
			} 
		}
		/*visage reconnu si distance min <= au seuil*/
		boolean reconnu = distanceMin <= seuil; 
		/*si reconnu on récupère le nom de la personne associée à l'image la plus proche*/
		String persoReco = reconnu ? imgPlusProche.getIdPersonne() : "inconnu"; 
		/*retourne toutes les infos*/
		return new Resultat(img, imgPlusProche, (float) distanceMin, reconnu, persoReco); 	
		
	}
	

}
