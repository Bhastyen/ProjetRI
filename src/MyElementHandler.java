import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

public class MyElementHandler extends DefaultHandler {
	private boolean header = false, idDoc = false;
	private int totalElement = 0;
	private long id = 0;
	private String contenu = "";
	private List<Document> docs = new ArrayList<Document>();
	private List<String> chemins = new LinkedList<String>();
	private Arbre arbreIndex;

	public void startDocument() {
		contenu = "";
		arbreIndex = new Arbre(null);
	}

	public void endDocument() {
		// on enleve les documents vides
		/*
		 * docs.removeIf(new Predicate<Document>() {
		 * 
		 * @Override public boolean test(Document d) { return
		 * (d.getStringDocument().isEmpty() || (d.getStringDocument().length() == 1 &&
		 * d.getStringDocument().charAt(0) == ' ')); } });
		 */

		// System.err.println("Nombre de doc viable : " + docs.size());
	}

	public void startElement(String uri, String localName, String qName, Attributes att) {
		// Creation de l element actuel
		arbreIndex.addFils();
		arbreIndex = arbreIndex.getFils().get(arbreIndex.getFils().size() - 1);

		// mise a jour des compteurs du pere
		if (!arbreIndex.getPere().getCompteurs().containsKey(qName))
			arbreIndex.getPere().getCompteurs().put(qName, 0);
		arbreIndex.getPere().getCompteurs().put(qName, arbreIndex.getPere().getCompteurs().get(qName) + 1);

		// ajoute l'element dans la pile avec la valeur de son compteur local
		chemins.add(0, qName + "[" + arbreIndex.getPere().getCompteurs().get(qName) + "]");

		// recherche de la premiere occurrence de l'id
		if (qName.equals("header"))
			header = true;

		if (header && qName.equals("id")) {
			idDoc = true;
			header = false;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		Document d;
		TLongList idFils = new TLongArrayList();

		// Verification si l'element a eu des fils terminé
		String contenuFils = "";

		for (Arbre fils : arbreIndex.getFils()) { // Ajout du contenu des fils au contenu de la section
			contenuFils += docs.get(fils.getIndexList()).getStringDocument();
			idFils.add(docs.get(fils.getIndexList()).getId());
		}

		// mise a jour du compteur pour id element
		totalElement += 1;

		// Creation d'un nouveau document
		contenu = contenuFils + " " + Document.sentenceProcessing(contenu.replaceAll(" +", " "));
		d = new Document(id, contenu, calculChemin());
		d.setId(calculId(id));
		d.setIdFils(idFils);

		switch (qName) {
		case "article":
			d.setType(Document.Type_Element.ARTICLE);
			break;
		case "title":
			d.setType(Document.Type_Element.TITLE);
			break;
		case "sec":
			d.setType(Document.Type_Element.SECTION);
			break;
		case "bdy":
			d.setType(Document.Type_Element.BODY);
			break;
		case "link":
			d.setType(Document.Type_Element.LINK);
			break;
		case "p":
			d.setType(Document.Type_Element.PARAGRAPH);
			break;
		case "name":
			d.setType(Document.Type_Element.NAME);
			break;
		case "b":
			d.setType(Document.Type_Element.BOLD);
			break;
		case "it":
			d.setType(Document.Type_Element.ITALIC);
			break;

		default:
			d.setType(Document.Type_Element.VIDE);
			break;
		}

		docs.add(d);

		arbreIndex.setIndexList(docs.size() - 1);
		arbreIndex = arbreIndex.getPere();

		contenu = "";

		// Suppression d'un element dans la pile pour connaitre le chemin
		chemins.remove(0);
	}

	private String calculChemin() {
		String chemin = "";

		for (int i = chemins.size() - 1; i >= 0; i--)
			chemin += chemins.get(i) + "/";
		return chemin;

	}

	private long calculId(long idDoc) {
		String id = "", n;

		/*
		 * for (int i = chemins.size()-1; i >= 0; i--) { n = chemins.get(i); id +=
		 * n.substring(n.lastIndexOf('[') + 1, n.lastIndexOf(']')); }
		 */

		id = Integer.toString(totalElement);

		return Long.parseLong(idDoc + id);
	}

	public void characters(char[] caracteres, int depart, int longueur) {
		int i = 0;
		String mot = new String(caracteres, depart, longueur);
		String idStr;

		if (idDoc) {
			id = Long.parseLong(mot);
			idDoc = false;

			// recalcul certain id d'element qui ne pouvait etre calcule sans id
			while (i < docs.size() && docs.get(i).getId() < 10000) { // tous les premiers docs qui n'avait id du doc de
																		// pret
				idStr = Long.toString(docs.get(i).getId());
				docs.get(i).setId(Long.valueOf(id + idStr));
				i++;
			}
		} else {
			contenu += mot.replace('\n', ' ').replaceAll(" +", " ") + " ";
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
