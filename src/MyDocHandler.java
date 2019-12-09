import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;



public class MyDocHandler extends DefaultHandler {
	private boolean header = false, idDoc = false;
	private int totalElement = 0;
	private long id = 0;
	private String contenu = "";
	private List<Document> docs = new ArrayList<Document>();
	private List<String> chemins = new LinkedList<String>();
	private Map<String, Integer> compteurElements = new HashMap<String,Integer>();
	private Arbre arbreIndex;
	
	
	public void startDocument() {
		//System.out.println("Debut du doc");
		contenu = "";
		arbreIndex= new Arbre(null);
	}
	
	public void endDocument() {
		// on enleve les documents vides
		docs.removeIf(new Predicate<Document>() {
			@Override
			public boolean test(Document d) {
				return (d.getStringDocument().isEmpty());
			}
		});
	}
		
	public void startElement(String uri, String localName, String qName, Attributes att) {
		// hierarchisation de l'affichage grace aux identations + affiche le nom des elements
		//System.out.println("name : " + qName);
		//System.out.println("localName : " + localName);
		//System.out.println("URI : " + uri);
		
		//Creation de l element actuel
		arbreIndex.addFils();
		arbreIndex = arbreIndex.getFils().get(arbreIndex.getFils().size() - 1);
		
		//Ajout d'un element dans la pile pour connaitre le chemin
		if(compteurElements.get(qName) == null)
			compteurElements.put(qName, 1);
		else {
			compteurElements.put(qName, compteurElements.get(qName)+1);
		}
		
		chemins.add(0, qName +"["+compteurElements.get(qName)+"]");
		totalElement ++;
		
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
		Document d;
		
		// Verification si l'element a eu des fils terminé
		String contenuFils = "";
		
		for (Arbre fils : arbreIndex.getFils()) { // Ajout du contenu des fils au contenu de la section
			contenuFils += docs.get(fils.getIndexList()).getStringDocument();
		}
		
		// Creation d'un nouveau document
		contenu = contenuFils + Document.sentenceProcessing(contenu.replaceAll(" +", " "));
		d = new Document(id, contenu, calculChemin());
		d.setId(calculId(id));
		docs.add(d);
		
		arbreIndex.setIndexList(docs.size() - 1);
		arbreIndex = arbreIndex.getPere();
		
		contenu = "";

		// Suppression d'un element dans la pile pour connaitre le chemin
		chemins.remove(0);
	}
	
	private String calculChemin() {
		String chemin = "";
		
		for (int i = chemins.size()-1; i >= 0; i--)
			chemin += chemins.get(i) + "/";
		return chemin;
		
	}
	
	private long calculId(long idDoc) {
		String id = "", n;
		
		for (int i = chemins.size()-1; i >= 0; i--) {
			n = chemins.get(i);
			id += n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']'));
		}
		
		return Long.parseLong(idDoc + id);
	}
	
	public void characters(char[] caracteres, int depart, int longueur) {
		String mot = new String(caracteres, depart, longueur);
		
		if (idDoc) {
			id = Long.parseLong(mot);
			idDoc = false;
		}else {
			contenu += mot.replaceAll("[!,;:^']", " ").replaceAll(" +", " ").replace('\n', ' ') + " ";
		}
		
	}
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
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
