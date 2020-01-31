import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Arbre {
	private int indexList;
	private Map<String, Integer> compteurs;
	private List<Arbre> fils = new ArrayList<Arbre>();
	private Arbre pere;
	
	
	public Arbre(Arbre pere) {
		this.pere = pere;
		compteurs = new HashMap<>();
	}
	
	public Arbre(int indexList, Arbre pere) {
		this(pere);
		this.indexList = indexList;
	}
	
	public Arbre(int indexList, List<Arbre> fils, Arbre pere) {
		this(indexList, pere);
		this.fils = fils;
	}
	
	public void addFils(int i) {
		fils.add(new Arbre(i,this));
	}
	
	public void addFils() {
		fils.add(new Arbre(this));
	}
	
	public int getIndexList() {
		return indexList;
	}


	public void setIndexList(int indexList) {
		this.indexList = indexList;
	}


	public List<Arbre> getFils() {
		return fils;
	}


	public void setFils(List<Arbre> fils) {
		this.fils = fils;
	}


	public Arbre getPere() {
		return pere;
	}


	public void setPere(Arbre pere) {
		this.pere = pere;
	}


	public Map<String, Integer> getCompteurs() {
		return compteurs;
	}


	public void setCompteurs(Map<String, Integer> compteurs) {
		this.compteurs = compteurs;
	}

}
