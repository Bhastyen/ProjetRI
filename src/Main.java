import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;



public class Main {
	public static final String OUTPUT_DIR = "resources/resultats/";
	public static final String OUTPUT_NAME = "BastienCelineLaetitiaPierre";
	
	
	public static void main(String[] args) {
		List<Document> docsBrut, docsXML;
		List<String> queries;
		HashMap<String, List<Pair>> postingList = null;
		List<Entry<Integer, Float>> cosScore;
		File query = new File("resources/topics_M2WI7Q_2019_20.txt");
		
		// parsing des documents
		docsBrut = parserDoc("resources/textes_brut/", Document.Type.BRUT);
		//docsXML = parserDoc("resources/coll/", Document.Type.XML);
		
		// indexation
		
		
		// calcul du score des documents pour chaque requete
		queries = readQuery(query);
		for (String q : queries) {
			cosScore = Models.CosineScore(q.substring(8), postingList, docsBrut);  // TODO ajouter param run
			// writeRun(String path, String numQuery, List<Entry<Integer, Float>> cosScore);
		}
	}

	// function : parserDoc(String pathResources, Document.Type type) output(List<Document>)  , type = "xml" ou "brut"
	public static List<Document> parserDoc(String path, Document.Type type){
		
		if (type == Document.Type.BRUT) {
			ParserBrut parser = new ParserBrut(path);
			return parser.parse();
		}else {
			ParserXML parser = new ParserXML(path);
			return parser.parse();
		}
	}
	
	private static List<String> readQuery(File fileQ) {
		String ligne;
		BufferedReader reader;
		List<String> queries = new ArrayList<>();
		
		try {
			reader = new BufferedReader(new FileReader(fileQ));
		
			// decoupe le fichier en ligne
			while ((ligne = reader.readLine()) != null) {
				queries.add(ligne);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Probleme lors de la lecture du fichier : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Probleme lors de la lecture du fichier : " + e.getMessage());
		}
		
		return queries;
	}
	
	
}

