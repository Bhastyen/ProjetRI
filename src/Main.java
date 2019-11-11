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
import java.util.Map;
import java.util.Map.Entry;



public class Main {
	//public static final String ETAPE = "01";
	public static final int NUMBER_OF_DOCUMENT_BY_QUERY = 1500;
	public static final String GRANULARITE = "articles";
	public static final String OUTPUT_DIR = "resources/resultats/";
	public static final String OUTPUT_NAME = "BastienCelineLaetitiaPierre";
	public static final String[] PARAMETERS = new String[] {"nnn", "nnc", "ltn", "ltc", "lts"};


	public static void main(String[] args) {
		List<Document> docsBrut, docsXML;
		List<String> queries;
		HashMap<String, Map<Integer, Long>> postingList = null;
		HashMap<Integer, Map<String, Long>> postingListPerDoc = null;
		File query = new File("resources/topics_M2WI7Q_2019_20.txt");

		// parsing des documents
		docsBrut = parserDoc("resources/textes_brut/", Document.Type.BRUT);
		docsXML = parserDoc("resources/coll/", Document.Type.XML);

		// indexation
		Indexator indexator = new Indexator();
		indexator.createIndex(docsBrut);
		postingList = indexator.getPostingList();
		postingListPerDoc = indexator.getPostingListPerDoc();
		System.out.println("Indexator End");
		
		System.out.println("Posting list in : " + postingList.get("in").size());
		//System.out.println("Doc "+ docsBrut.get(1597).getIdDoc() + "   Brut " + docsBrut.get(1597).getLength() +
		//		" Doc " + docsXML.get(1597).getIdDoc() + "  XML " + docsXML.get(1597).getLength());
		
		// TEXTE BRUT : calcul du score des documents pour chaque requete et ecriture du run
		queries = readQuery(query);
		writeAllRuns(queries, OUTPUT_DIR + "brut/", OUTPUT_NAME, "03", "articles", docsBrut, postingList, postingListPerDoc);

		// TEXTE XML : calcul du score des documents pour chaque requete et ecriture du run
		writeAllRuns(queries, OUTPUT_DIR + "xml/", OUTPUT_NAME, "03", "articles", docsXML, postingList, postingListPerDoc);

		System.out.println("Runs write");
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

	public static void writeAllRuns(List<String> queries, String path,
										String nomEquipe, String etape,
											String granularite, List<Document> docs,
												HashMap<String, Map<Integer, Long>> postingList,
													HashMap<Integer, Map<String, Long>> postingListPerDoc) {

		List<Entry<Integer, Float>> cosScore;

		for (int numRun = 0; numRun < PARAMETERS.length; numRun ++) {
			BufferedWriter buff;
			File out = new File(path + nomEquipe + "_" + etape + "_" + "0"+(numRun+1) + "_" + PARAMETERS[numRun].toUpperCase() + "_" + "articles" + ".txt");

			try {
				buff = new BufferedWriter(new FileWriter(out));

				for (String q : queries) {
					cosScore = Models.CosineScore(q.substring(8), postingList, postingListPerDoc, docs, PARAMETERS[numRun]);
					writeRun(buff, nomEquipe, q.substring(0, 7), cosScore);
				}

				buff.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static void writeRun(BufferedWriter buff, String nomEquipe, String numQuery, List<Entry<Integer, Float>> cosScore) throws IOException {
		int number_doc = cosScore.size();

		if (number_doc > NUMBER_OF_DOCUMENT_BY_QUERY)
			number_doc = NUMBER_OF_DOCUMENT_BY_QUERY;

		for (int i = 0; i < number_doc; i++) {
			buff.append(numQuery + " Q0 " + cosScore.get(i).getKey() + " " + (i+1) + " " + cosScore.get(i).getValue() + " " + nomEquipe + " /article[1]");
			buff.newLine();
		}

	}
}
