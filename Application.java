package CodeDiagramme;

import java.util.List;

public class Application {
	private Visages visagesRef;
	private Visages visagesTest;
	private ACP acp;
	
	public Application (Visages visagesRef, Visages visagesTest, ACP acp) {
		this.visagesRef = visagesRef;
		this.visagesTest = visagesTest;
		this.acp = acp;
	}
	
	public Image identifierVisage(Image img) {
		Pretraitement pt = new PreTraitement();
		Image imgTraitee = pt.traiter(img);
		Identificateur id = new Identificateur(acp);
		Resultat r = id.identifier(imgTraitee);
		if (r.isReconnu()) {
			System.out.println("Visage reconnu : " + r.getIdPersoReco());
			System.out.println("Distance : " + r.getDistanceMin());
		} else {
			System.out.println("Visage non reconnu");
			System.out.println("Distance : " + r.getDistanceMin());
		}
		Image imageReconnue = r.getImgPlusProche();
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
