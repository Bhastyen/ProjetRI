import java.util.List;


public class Main {
	// function : parserDoc(String pathResources, Document.Type type) output(List<Document>)  , type = "xml" ou "brut"
	
	public static void main(String[] args) {
		List<Document> docs;
		
		// parsing des documents
		docs = parserDoc("resources/textes_brut/", Document.Type.BRUT);
	}

	
	public static List<Document> parserDoc(String path, Document.Type type){
		
		if (type == Document.Type.BRUT) {
			ParserBrut parser = new ParserBrut(path);
			return parser.parse();
		}else {
			//ParserXML parser = new ParserXML(path);
			//return parser.parse();
		}
		
		return null;
	}
}

