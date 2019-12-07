import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;



public class MyDocHandler extends DefaultHandler {
	private boolean header = false, idDoc = false;
	private int id = 0;
	private String contenu = "";
	private List<Document> docs = new ArrayList<Document>();
	private List<String> chemins = new LinkedList<String>();
	private Map<String, Integer> compteurElements = new HashMap<String,Integer>();
	private int totalElement = 0;
	private Arbre arbreIndex ;
	
	
	public void startDocument() {
		//System.out.println("Debut du doc");
		contenu = "";
		arbreIndex= new Arbre(null);
	}
	
	public void endDocument() {
		//System.out.println("Fin du doc");
		contenu = contenu.replaceAll(" +", " ");
	}
		
	public void startElement(String uri, String localName, String qName, Attributes att) {
		// hierarchisation de l'affichage grace aux identations + affiche le nom des elements
		//System.out.println("name : " + qName);
		//System.out.println("localName : " + localName);
		//System.out.println("URI : " + uri);
		
		//Creation de l element actuel
		arbreIndex.addFils();
		arbreIndex = arbreIndex.getFils().get(arbreIndex.getFils().size()-1);
		
		//Ajout d'un element dans la pile pour connaitre le chemin
		if(compteurElements.get(qName) == null)
			compteurElements.put(qName, 1);
		
		else {
			compteurElements.put(qName, compteurElements.get(qName)+1);
		}
		chemins.add(0, qName +"["+compteurElements.get(qName)+"]");
		totalElement++;
		// recherche de la premiere occurrence de l'id
		if (qName.equals("header"))
			header = true;
		
		if (header && qName.equals("id")) {
			idDoc = true;
			header = false;
		}
		
		// je m'occupe des attributs s'ils en existent
		
		/*if (att.getLength() > 0) {
			System.out.print("(");
			for (int i = 0; i<att.getLength(); i++)
				if (i == att.getLength()-1)
					System.out.print(att.getQName(i)+"="+att.getValue(i));
				else System.out.print(att.getQName(i)+"="+att.getValue(i)+",");
			System.out.println(")");
		}*/
	} 
		
	public void endElement(String uri, String localName, String qName) {
		
		//Verification si l'element a eu des fils terminé
		String contenuFils = "";
		for (Arbre fils : arbreIndex.getFils()) {//Ajout du contenu des fils au contenu de la section
			contenuFils += docs.get(fils.getIndexList());
		}
		//Creation d'un nouveau document
		contenu = contenuFils+ Document.sentenceProcessing(contenu.replaceAll(" +", " "));
		docs.add(new Document(id, contenu, calculChemin()));
		arbreIndex.setIndexList(docs.size()-1);
		arbreIndex = arbreIndex.getPere();
		
		contenu = "";

		//Suppression d'un element dans la pile pour connaitre le chemin
		chemins.remove(0);

	}
	
	private String calculChemin() {
		String chemin = "";
		
		for (String element : chemins) {
			chemin += "\\" + element;
		}
		return chemin;
		
	}
	public void characters(char[] caracteres, int depart, int longueur) {
		String mot = new String(caracteres, depart, longueur);
		
		if (idDoc) {
			id = Integer.parseInt(mot);
			idDoc = false;
		}else {
			contenu += mot.replaceAll("[!,;:^']", " ").replaceAll(" +", " ").replace('\n', ' ') + " ";
		}
		
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public List<Document> getDocs() {
		return docs;
	}

	public void setDocs(List<Document> docs) {
		this.docs = docs;
	}
	
}
