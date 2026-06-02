package CodeDiagramme;

import java.util.List;

public class Application {
	/*Liste des visages de référence utilisés pour l'entraînement et la reconnaissance*/
	private Visages visagesRef;
	/*Liste des visages test utilisés pour évaluer les performances du système*/
	private Visages visagesTest;
	/*Objet ACP contient les eigenfaces et la base de visages de référence. 
	 * Il est utilisé pour projeter les images dans l'espace*/
	private ACP acp;
	
	/*Constructeur complet*/
	public Application (Visages visagesRef, Visages visagesTest, ACP acp) {
		this.visagesRef = visagesRef;
		this.visagesTest = visagesTest;
		this.acp = acp;
	}
	
	/**
	 * Fais le processus complet de reconnaissance faciale pour une image donée.
	 * Affiche le résultat et retourne l'image de référence la plus proche.
	 * @param img Image brute contenant le visage à identifier
	 * @return L'image de référence la plus proche du visage testé.
	 */
	public Image identifierVisage(String chemin) {
		/*création d'un objet de prétraitement*/
		Pretraitement pt = new PreTraitement(92,112); 
		/*application du prétraitement*/
		pt.verifierConformite(chemin);
		Image imgTraitee = new Image(chemin);
		/*création de l'identificateur en passant l'acp*/
		Identificateur id = new Identificateur(acp);
		/*compare l'image à toute la base de référence*/
		Resultat r = id.identifier(imgTraitee);
		/*Boucle qui affiche le résultat selon le visage reconnu ou non*/
		if (r.isReconnu()) { 
			System.out.println("Visage reconnu : " + r.getIdPersoReco());
			System.out.println("Distance : " + r.getDistanceMin());
		} else {
			System.out.println("Visage non reconnu");
			System.out.println("Distance : " + r.getDistanceMin());
		}
		Image imageReconnue = r.getImgPlusProche();
		/*retourne l'image la plus proche du Resultat*/
		return imageReconnue;
	}
	
	public void entrainer() {
		this.acp.getEigenfaces();
	}
	
	public Visages getVisagesRef() {
		return visagesRef;
	}

	public void setVisagesRef(Visages visagesRef) {
		this.visagesRef = visagesRef;
	}

	public Visages getVisagesTest() {
		return visagesTest;
	}

	public void setVisagesTest(Visages visagesTest) {
		this.visagesTest = visagesTest;
	}

	public ACP getAcp() {
		return acp;
	}

	public void setAcp(ACP acp) {
		this.acp = acp;
	}
}
