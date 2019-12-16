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
	
	
    public ParserXMLElement(String path) {
    	this.path = path;
    	
    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	// factory.setValidating(true);
    	
    	try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
    }
    
	public List<Document> parse() {
		List<Document> docs = new ArrayList<Document>();
		MyElementHandler handler;
		File d = new File(path);
		File[] fichiers;
		
		
		try {
			if (d.isDirectory()) {
				fichiers = d.listFiles();
			
				for (int i = 0; i < fichiers.length; i++) {
					handler = new MyElementHandler();
					parser.parse(fichiers[i].getPath(), handler);
					
					//System.err.println("Docs  " + handler.getId() + " " + i + "  " + fichiers.length);
					docs.addAll(handler.getDocs());

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
