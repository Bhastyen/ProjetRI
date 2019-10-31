import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;



public class Main {
	//public static final String ETAPE = "01";
	public static final int NUMBER_OF_DOCUMENT_BY_QUERY = 1500;
	public static final String GRANULARITE = "articles";
	public static final String OUTPUT_DIR = "resources/resultats/";
	public static final String OUTPUT_NAME = "BastienCelineLaetitiaPierre";
	public static final String[] PARAMETERS = new String[] {"nnn", "nnn", "nnn", "nnn", "nnn"};
	
	
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
		
		
		// TEXTE BRUT : calcul du score des documents pour chaque requete et ecriture du run
		queries = readQuery(query);
		for (int numRun = 0; numRun < PARAMETERS.length; numRun ++) {
			for (String q : queries) {
				cosScore = Models.CosineScore(q.substring(8), postingList, docsBrut, PARAMETERS[numRun]);
				writeRun(OUTPUT_DIR,  OUTPUT_NAME, q.substring(0, 8), "01", "0" + (numRun + 1), cosScore, "articles", PARAMETERS[numRun]);
			}
		}
		
		// TEXTE XML : calcul du score des documents pour chaque requete et ecriture du run
		/*for (int numRun = 0; numRun < PARAMETERS.length; numRun ++) {
			for (String q : queries) {
				cosScore = Models.CosineScore(q.substring(8), postingList, docsBrut, PARAMETERS[numRun]);
				writeRun(OUTPUT_DIR,  OUTPUT_NAME, q.substring(0, 8), "02", "0" + (numRun + 1), cosScore, GRANULARITE, PARAMETERS[numRun]);
			}
		}*/
		
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
	
	public static void writeRun(String path, String nomEquipe, String numQuery, String etape, String numRun, List<Entry<Integer, Float>> cosScore, String granularite, String ponderation) {
		File out = new File(path + nomEquipe + "_" + etape + "_" + numRun + "_" + ponderation + "_" + granularite + ".txt");
		BufferedWriter buff;
		int number_doc = cosScore.size();
		
		if (number_doc > NUMBER_OF_DOCUMENT_BY_QUERY)
			number_doc = NUMBER_OF_DOCUMENT_BY_QUERY;
		
		try {
			buff = new BufferedWriter(new FileWriter(out));
			
			for (int i = 0; i < number_doc; i++) {
				buff.append(numQuery + " Q0 " + cosScore.get(i).getKey() + " " + (i+1) + " " + cosScore.get(i).getValue() + " " + nomEquipe + " /article/");
				buff.newLine();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

