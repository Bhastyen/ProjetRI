import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;



public class ParserXML{
	private String path;
    private SAXParser parser;
    private List<String> queries;
	
	
    public ParserXML(String path, List<String> queries) {
    	this.path = path;
    	this.queries = queries;
    	
    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	// factory.setValidating(true);
    	
    	try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException | SAXException e) {
			System.err.println("Probleme lors de la creation du parser SAX : " + e.getMessage()); 
		}
    }
    
	public List<Document> parse() {
		List<Document> docs = new ArrayList<Document>();
		MyDocHandler handler;
		File d = new File(path);
		File[] fichiers;
		
		
		try {
			if (d.isDirectory()) {
				fichiers = d.listFiles();
			
				for (int i = 0; i < fichiers.length; i++) {
					handler = new MyDocHandler();
					parser.parse(fichiers[i].getPath(), handler);
					
					//System.err.println("Docs  " + handler.getId() + " " + i + "  " + fichiers.length);
					docs.add(handler.getDoc());

					//System.out.println("ID : " + handler.getId());
					
					//docs.add(new Document(handler.getId(), Document.sentenceProcessing(handler.getContenu())));
					//docs + docsHandler
					//HashMap(id,docs); docs = new list
				}
			}
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		
		return docs;
	}

}
