import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;



public class ParserXMLElement{
	private String path;
    private SAXParser parser;
    private Indexator postingLists;
	
	
    public ParserXMLElement(String path) {
    	this.path = path;
    	
    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	postingLists = new Indexator();
    	// factory.setValidating(true);
    	
    	try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
    }
    
	public List<Document> parse() {
		List<Document> docs = new ArrayList<Document>();   // ensemble de tous les documents cree a partir des fichiers
		List<Document> elements = new ArrayList<Document>();   // ensemble des documents qui vienne d'un fichier
		MyElementHandler handler;    // parse le fichier xml
		File d = new File(path);
		File[] fichiers;
		
		
		try {
			if (d.isDirectory()) {
				fichiers = d.listFiles();
			
				for (int i = 0; i < fichiers.length; i++) {
					handler = new MyElementHandler();
					parser.parse(fichiers[i].getPath(), handler);
					
					//System.err.println("Docs  " + handler.getId() + " " + i + "  " + fichiers.length);
					elements = handler.getDocs();
					postingLists.createIndex(elements);  // ajout du contenu dans la posting list
					
					// suppression du contenu
					for (int j = 0; j < elements.size(); j++) {
						elements.get(j).setStringDocument("");
					}

					// ajout de nouveaux elements
					docs.addAll(elements);
					
					//System.out.println("ID : " + handler.getId());
					parser.reset();
					
					handler = null;
				}
				
			}
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		
		return docs;
	}

	public Indexator getPostingLists() {
		return postingLists;
	}

	public void setPostingLists(Indexator postingLists) {
		this.postingLists = postingLists;
	}

}
