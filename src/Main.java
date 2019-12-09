import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	public static final String[] PARAMETERS = new String[] {"ltn"};
	public static final Boolean STEMMING = true;
	public static final Boolean STOPWORD = false;


	public static void main(String[] args) {
		List<Document> docsBrut, docsXML;
		List<String> queries;

		HashMap<String, Map<Integer, Long>> postingList = null;
		HashMap<Integer, Map<String, Long>> postingListPerDoc = null;
		HashMap<String, Map<Integer, Long>> postingListXML = null;
		HashMap<Integer, Map<String, Long>> postingListPerDocXML = null;
		//File query = new File("resources/topics_M2WI7Q_2019_20.txt");
		File query = new File("resources/test-reduit/queryTest/query.txt");

		// parsing des documents
		docsBrut = parserDoc("resources/test-reduit/TD", Document.Type.BRUT);

		docsXML = parserDoc("resources/test-reduit/XML", Document.Type.XML);

		OutPutFileParsingBrut(docsBrut);
		OutPutFileParsingXML(docsXML);

		// indexation
		Indexator indexator = new Indexator();
		Indexator indexatorXML = new Indexator();
		indexator.createIndex(docsBrut);
		indexatorXML.createIndex(docsXML);
		postingList = indexator.getPostingList();
		postingListPerDoc = indexator.getPostingListPerDoc();
		postingListXML = indexatorXML.getPostingList();
		postingListPerDocXML = indexatorXML.getPostingListPerDoc();
		System.out.println("Indexator End");

		System.out.println("Posting list size : " + postingList.size());
		
		 OutPutFilePostingList(postingList);
		//System.out.println("Doc "+ docsBrut.get(1597).getIdDoc() + "   Brut " + docsBrut.get(1597).getLength() +
		//		" Doc " + docsXML.get(1597).getIdDoc() + "  XML " + docsXML.get(1597).getLength());

		// TEXTE BRUT : calcul du score des documents pour chaque requete et ecriture du run
		queries = readQuery(query);
		writeAllRuns(queries, OUTPUT_DIR + "brut/", OUTPUT_NAME, "03", "articles", docsBrut, postingList, postingListPerDoc);

		// TEXTE XML : calcul du score des documents pour chaque requete et ecriture du run
		writeAllRuns(queries, OUTPUT_DIR + "xml/", OUTPUT_NAME, "03", "articles", docsXML, postingListXML, postingListPerDocXML);

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
					//TODO futur Hash par liste de document du "gros doc" faire loop pour appeler cosine score
					//TODO comparatif score entre parents / enfants
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
		System.out.println("Ecrit posting fichier");
		
		try {
			buff = new BufferedWriter(new FileWriter(out));
			System.out.println("dans try");
			for (Entry<String, Map<Integer, Long>> p : postingList.entrySet()) {//String : key (mot) Map Integer:doc id Long nombre occurence
				buff.append("key " + p.getKey() + " DocId/nbOccu" + p.getValue().toString());
				buff.newLine();
			}

			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Ecrit posting fichier FIN");

	}
	public static  void  OutPutFileParsingBrut(List<Document> docs) {
		BufferedWriter buff;
		File out = new File("resources/parsingBrut.txt");
		System.out.println("Ecrit parser Brut fichier");
		try {
			buff = new BufferedWriter(new FileWriter(out));
			System.out.println("dans try");
			for (Document doc : docs) {//String : key (mot) Map Integer:doc id Long nombre occurence
				System.out.println(" Brut doc size : " + doc.getLength());
				buff.append("idDoc " + doc.getIdDoc() + "Contenu " + doc.getStringDocument());
				buff.newLine();
				buff.newLine();
				buff.newLine();
				buff.newLine();
			}

			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Ecrit parsing Brut fichier FIN");

	}
	//Faire une seule methode pour Brut et XML mais j'avais la flemme pour tester
	public static  void  OutPutFileParsingXML(List<Document> docs) {
		BufferedWriter buff;
		File out = new File("resources/parsingXML.txt");
		System.out.println("Ecrit parser XML fichier");
		try {
			buff = new BufferedWriter(new FileWriter(out));
			System.out.println("dans try");
			for (Document doc : docs) {//String : key (mot) Map Integer:doc id Long nombre occurence
				System.out.println(" XML doc size : " + doc.getLength());
				buff.append("idDoc " + doc.getIdDoc() + "Contenu " + doc.getStringDocument());
				buff.newLine();
				buff.newLine();
				buff.newLine();
				buff.newLine();
			}

			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Ecrit parsing XML fichier FIN");

	}
	
	
}
