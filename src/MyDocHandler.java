import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;



public class MyDocHandler extends DefaultHandler {
	private boolean header = false, idDoc = false;
	private long id = 0;
	private String contenu = "";
	private Document doc;
	
	
	public void startDocument() {
		contenu = "";
	}
	
	public void endDocument() {
		// Creation d'un nouveau document
		contenu = Document.sentenceProcessing(contenu.replaceAll(" +", " "));
		
		doc = new Document(id, contenu, "/article[1]/");
	}
		
	public void startElement(String uri, String localName, String qName, Attributes att) {
		// recherche de la premiere occurrence de l'id
		if (qName.equals("header"))
			header = true;
		
		if (header && qName.equals("id")) {
			idDoc = true;
			header = false;
		}
	} 
	
	public void endElement(String uri, String localName, String qName) {

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

	public Document getDoc() {
		return doc;
	}
	
}
