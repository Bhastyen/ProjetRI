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
	
	public static final String[] PARAMETERS = new String[] {"nnn", "ltn", "ltc"};   // test bm25 b = 0.75, k = 1.2
	public static final Boolean STEMMING = true;
	public static final Boolean STOPWORD = true;


	public static void main(String[] args) {
		long begin, time, total = 0; 
		List<Document> docsBrut, docsXML;
		List<String> queries;

		HashMap<String, Map<Integer, Long>> postingList = null;
		HashMap<Integer, Map<String, Long>> postingListPerDoc = null;
		File query = new File("resources/topics_M2WI7Q_2019_20.txt");

		// parsing des documents
		begin = System.currentTimeMillis();
		docsBrut = parserDoc("resources/textes_brut/", Document.Type.BRUT);
		time = (System.currentTimeMillis() - begin); total += time;
		System.out.println("Parsing brut done in " + (time/1000f));

		begin = System.currentTimeMillis();
		docsXML = parserDoc("resources/coll", Document.Type.XML);
		time = (System.currentTimeMillis() - begin); total += time;
		System.out.println("Parsing XML done in " + (time/1000f));

		OutPutFileParsing(docsBrut, "parsingBrut.txt");
		OutPutFileParsing(docsXML, "parsingXML.txt");

		// indexation
		begin = System.currentTimeMillis();
		Indexator indexator = new Indexator();
		indexator.createIndex(docsBrut);
		postingList = indexator.getPostingList();
		postingListPerDoc = indexator.getPostingListPerDoc();
		time = (System.currentTimeMillis() - begin); total += time;
		System.out.println("Indexing done in " + (time/1000f));

		System.out.println("Posting list size : " + postingList.size());
		
		OutPutFilePostingList(postingList);
		 
		// get queries
		queries = readQuery(query);
		
		// TEXTE BRUT : calcul du score des documents pour chaque requete et ecriture du run
		begin = System.currentTimeMillis();
		writeAllRuns(queries, OUTPUT_DIR + "brut/", OUTPUT_NAME, "03", "articles", docsBrut, postingList, postingListPerDoc);
		time = (System.currentTimeMillis() - begin); total += time;
		System.out.println("Runs brut done in " + (time/1000f));

		// TEXTE XML : calcul du score des documents pour chaque requete et ecriture du run
		begin = System.currentTimeMillis();
		writeAllRuns(queries, OUTPUT_DIR + "xml/", OUTPUT_NAME, "03", "articles", docsXML, postingList, postingListPerDoc);
		time = (System.currentTimeMillis() - begin); total += time;
		System.out.println("Runs XML done in " + (time/1000f));

		System.out.println("Search done in " + (total/1000f));
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
			File out = new File(path + nomEquipe + "_" + etape + "_" + "0"+(numRun+1) + "_" + PARAMETERS[numRun].toUpperCase() + "_" + "articles_stopwords" + ".txt");

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

	public static  void  OutPutFilePostingList(HashMap<String, Map<Integer, Long>> postingList) {
		BufferedWriter buff;
		File out = new File("resources/postingList.txt");
		
		try {
			buff = new BufferedWriter(new FileWriter(out));
			for (Entry<String, Map<Integer, Long>> p : postingList.entrySet()) {//String : key (mot) Map Integer:doc id Long nombre occurence
				buff.append("key " + p.getKey() + " DocId/nbOccu" + p.getValue().toString());
				buff.newLine();
			}

			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static  void  OutPutFileParsing(List<Document> docs, String nameOut) {
		BufferedWriter buff;
		File out = new File("resources/" + nameOut);
		
		try {
			buff = new BufferedWriter(new FileWriter(out));
			for (Document doc : docs) {//String : key (mot) Map Integer:doc id Long nombre occurence
				buff.append("IdDoc " + doc.getIdDoc() + " Contenu " + doc.getStringDocument());
				buff.newLine();
				buff.newLine();
				buff.newLine();
				buff.newLine();
			}

			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
}
