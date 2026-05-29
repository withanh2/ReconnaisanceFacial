package CodeDiagramme;

public class Identificateur {
	private ACP acp;
	private float seuil;
	private TypeDistance typeDistance;
	
	public float getSeuil() {
		return seuil;
	}

	public void setSeuil(float seuil) {
		this.seuil = seuil;
	}
	
	public Identificateur(ACP acp, float seuil, TypeDistance typeDistance) {
		this.acp=acp;
		this.seuil=100;
		this.typeDistance=typeDistance;
	}
	
	public Resultat identifier(img : Image) {
		double[] vecteurTest = acp.projeter(img);/*signature numerique de l'image test*/
		Visages visages = acp.getVisages(); /*récupère liste de tous les visages de référence*/
		double distanceMin = Double.MAX_VALUE; /*initialise distance min à la plus grande valeur*/
		Image imgPlusProche = null; /*variable qui stocke l'image de ref la plus ressemblante*/
		for (Image imgRef : visages.getImages()) {
			double[] vecteurRef = acp.projeter(imgRef); /*calcul projection ds acp pour chaque image*/
			double distance = calculerDistance(vecteurTest, vecteurRef); /*distance entre signature image test et réf*/
			if (distance < distanceMin) {
				distanceMin = distance;
				imgPlusProche = imgRef;
			} /*trouver le visage le plus ressemblant à la fin de la boucle*/
		}
		boolean reconnu = distanceMin <= seuil; /*visage reconnu si distance min <= au seuil*/
		String persoReco = reconnu ? imgPlusProche.getIdPersonne() : "inconnu"; /*si reconnu on récupère le nom de la personne associée à l'image la plus proche*/
		return new Resultat(img, imgPlusProche, (float) distanceMin, reconnu, persoReco); /*retourne toutes les infos*/	
		
	}
	

}
