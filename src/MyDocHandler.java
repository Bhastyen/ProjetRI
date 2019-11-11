import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;



public class MyDocHandler extends DefaultHandler {
	private boolean header = false, idDoc = false;
	private int id = 0;
	private String contenu = "";
	

	public void startDocument() {
		//System.out.println("Debut du doc");
		contenu = "";
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
		
		// recherche de la premiere occurrence de l'id
		if (qName.equals("header"))
			header = true;
		
		if (header && qName.equals("id")) {
			idDoc = true;
			header = false;
		}
		
		// je m'occupe des attributs s'ils en extistent
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
	
}
