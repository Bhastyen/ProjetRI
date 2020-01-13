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
    private List<String> queries;
	
    
    public ParserXMLElement(String path) {
    	this.path = path;
    	this.queries = null;
    	
    	postingLists = new Indexator();
    }
    
    public ParserXMLElement(String path, List<String> queries) {
    	this(path);
    	
    	this.queries = queries;
    }
    
	public List<Document> parse() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		List<Document> docs = new ArrayList<Document>();   // ensemble de tous les documents cree a partir des fichiers
		List<Document> elements = new ArrayList<Document>();   // ensemble des documents qui vienne d'un fichier
		MyElementHandler handler;    // parse le fichier xml
		File d = new File(path);
		File[] fichiers;
		
		
		try {
			if (d.isDirectory()) {
				fichiers = d.listFiles();
			
				for (int i = 0; i < fichiers.length; i++) {
					try {
						parser = factory.newSAXParser();

						handler = new MyElementHandler();
						parser.parse(fichiers[i].getPath(), handler);
						
						//System.err.println("Docs  " + handler.getId() + " " + i + "  " + fichiers.length);
						elements = handler.getDocs();
						postingLists.createIndex(elements, queries);  // ajout du contenu dans la posting list en ne prenant 
						// en compte que les termes apparaissant dans la requete
						
						// suppression du contenu pour liberer de la memoire
						for (int j = 0; j < elements.size(); j++) {
							elements.get(j).setStringDocument("");
						}
						
						// ajout de nouveaux elements
						docs.addAll(elements);
						handler = null;
						elements = null;
					} catch (ParserConfigurationException/* | InterruptedException*/ e) {
						e.printStackTrace();
					}
					
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
