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
	public static final String[] PARAMETERS = new String[] {"ltn", "bm25,k=1,b=0.5"};
	public static final Boolean STEMMING = false;
	public static final Boolean STOPWORD = false;


	public static void main(String[] args) {
		List<Document> docsBrut, docsXML;
		List<String> queries;

		HashMap<String, Map<Long, Long>> postingList = null;
		HashMap<Long, Map<String, Long>> postingListPerDoc = null;
		HashMap<String, Map<Long, Long>> postingListXML = null;
		HashMap<Long, Map<String, Long>> postingListPerDocXML = null;
		
		File query = new File("resources/topics_M2WI7Q_2019_20.txt");
		//File query = new File("resources/test-reduit/queryTest/query.txt");

		// parsing des documents
		//docsBrut = parserDoc("resources/test-reduit/TD", Document.Type.BRUT);
		//docsXML = parserDoc("resources/test-reduit/XML", Document.Type.XML);
		//docsBrut = parserDoc("resources/textes_brut", Document.Type.BRUT);
		docsXML = parserDoc("resources/coll", Document.Type.XML);
		
		//OutPutFileParsingBrut(docsBrut);
		OutPutFileParsingXML(docsXML);

		// indexation
		//Indexator indexator = new Indexator();
		Indexator indexatorXML = new Indexator();
		//indexator.createIndex(docsBrut);
		indexatorXML.createIndex(docsXML);
		//postingList = indexator.getPostingList();
		//postingListPerDoc = indexator.getPostingListPerDoc();
		postingListXML = indexatorXML.getPostingList();
		postingListPerDocXML = indexatorXML.getPostingListPerDoc();
		//System.out.println("Indexator End");

		//System.out.println("Posting list size : " + postingListXML.size());
		
		OutPutFilePostingList(postingListXML);
		OutPutFilePostingListPerDoc(postingListPerDocXML);
		//System.out.println("Doc "+ docsBrut.get(1597).getIdDoc() + "   Brut " + docsBrut.get(1597).getLength() +
		//		" Doc " + docsXML.get(1597).getIdDoc() + "  XML " + docsXML.get(1597).getLength());

		// TEXTE BRUT : calcul du score des documents pour chaque requete et ecriture du run
		queries = readQuery(query);
		//writeAllRuns(queries, OUTPUT_DIR + "brut/", OUTPUT_NAME, "06", "articles", docsBrut, postingList, postingListPerDoc);

		// TEXTE XML : calcul du score des documents pour chaque requete et ecriture du run
		writeAllRuns(queries, OUTPUT_DIR + "xml/", OUTPUT_NAME, "06", "elements", docsXML, postingListXML, postingListPerDocXML);
		
		System.err.println("Size : " + docsXML.size());

		//System.out.println("Runs write");
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
			HashMap<String, Map<Long, Long>> postingList,
			HashMap<Long, Map<String, Long>> postingListPerDoc) {

		List<Entry<Document, Float>> cosScore;

		for (int numRun = 0; numRun < PARAMETERS.length; numRun ++) {
			BufferedWriter buff;
			File out = new File(path + nomEquipe + "_" + etape + "_" + "0" + (numRun + 1) + "_" + PARAMETERS[numRun].toUpperCase() + "_" + GRANULARITE + ".txt");

			try {
				buff = new BufferedWriter(new FileWriter(out));

				for (String q : queries) {
					cosScore = Models.CosineScore(q.substring(8), postingList, postingListPerDoc, docs, PARAMETERS[numRun]);
					writeRun(buff, nomEquipe, q.substring(0, 7), cosScore);
					
					System.err.println("Nombre resultat : " + cosScore.size());
				}

				buff.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static void writeRun(BufferedWriter buff, String nomEquipe, String numQuery, List<Entry<Document, Float>> cosScore) throws IOException {
		Document d;
		int number_doc = cosScore.size();

		if (number_doc > NUMBER_OF_DOCUMENT_BY_QUERY)
			number_doc = NUMBER_OF_DOCUMENT_BY_QUERY;

		for (int i = 0; i < number_doc; i++) {
			d = cosScore.get(i).getKey();
			buff.append(numQuery + " Q0 " + d.getIdDoc() + " " + (i+1) + " " + cosScore.get(i).getValue() + " " + nomEquipe + " " + d.getCheminDocument());
			buff.newLine();
		}

	}

	public static  void  OutPutFilePostingList(HashMap<String, Map<Long, Long>> postingList) {
		BufferedWriter buff;
		File out = new File("resources/postingList.txt");
		//System.out.println("Ecrit posting fichier");
		
		try {
			buff = new BufferedWriter(new FileWriter(out));
			for (Entry<String, Map<Long, Long>> p : postingList.entrySet()) {//String : key (mot) Map Integer:doc id Long nombre occurence
				buff.append("key " + p.getKey() + " DocId/nbOccu" + p.getValue().toString());
				buff.newLine();
			}

			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//System.out.println("Ecrit posting fichier FIN");

	}

	public static void OutPutFilePostingListPerDoc(HashMap<Long, Map<String, Long>> postingListPerDocXML) {
		BufferedWriter buff;
		File out = new File("resources/postingListPerDoc.txt");
		
		try {
			buff = new BufferedWriter(new FileWriter(out));
			for (Entry<Long, Map<String, Long>> p : postingListPerDocXML.entrySet()) {//String : key (mot) Map Integer:doc id Long nombre occurence
				buff.append("key " + p.getKey() + " term/nbOccu" + p.getValue().toString());
				buff.newLine();
			}

			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static  void  OutPutFileParsingBrut(List<Document> docs) {
		BufferedWriter buff;
		File out = new File("resources/parsingBrut.txt");
		//System.out.println("Ecrit parser Brut fichier");
		try {
			buff = new BufferedWriter(new FileWriter(out));
//			System.out.println("dans try");
			for (Document doc : docs) {//String : key (mot) Map Integer:doc id Long nombre occurence
//				System.out.println(" Brut doc size : " + doc.getLength());
				buff.append("idDoc " + doc.getId() + "Contenu " + doc.getStringDocument());
				buff.newLine();
				buff.newLine();
				buff.newLine();
				buff.newLine();
			}

			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Ecrit parsing Brut fichier FIN");

	}
	//Faire une seule methode pour Brut et XML mais j'avais la flemme pour tester
	public static  void  OutPutFileParsingXML(List<Document> docs) {
		BufferedWriter buff;
		File out = new File("resources/parsingXML.txt");
		//System.out.println("Ecrit parser XML fichier");
		try {
			buff = new BufferedWriter(new FileWriter(out));
//			System.out.println("dans try");
			for (Document doc : docs) {//String : key (mot) Map Integer:doc id Long nombre occurence
//				System.out.println(" XML doc size : " + doc.getLength());
				buff.append("idDoc " + doc.getId() + " Contenu " + doc.getStringDocument() + " Chemin " + doc.getCheminDocument());
				buff.newLine();
				buff.newLine();
			}

			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Ecrit parsing XML fichier FIN");

	}
	
	
}
