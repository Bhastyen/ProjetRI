import java.util.ArrayList;
import java.util.List;

public class Arbre {

	private int indexList;
	private List<Arbre> fils = new ArrayList<Arbre>();
	private Arbre pere;
	
	
	public Arbre(int indexList, List<Arbre> fils,Arbre pere) {
		super();
		this.indexList = indexList;
		this.fils = fils;
		this.pere=pere;
	}
	
	
	public Arbre(int indexList,Arbre pere) {
		super();
		this.indexList = indexList;
		this.pere=pere;
		
	}
	
	public Arbre(Arbre pere) {
		this.pere=pere;
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

}
