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
	public static final String GRANULARITE = "elements";
	public static final String OUTPUT_DIR = "resources/resultats/";
	public static final String OUTPUT_NAME = "BastienCelineLaetitiaPierre";
	public static final String[] PARAMETERS = new String[] {"ltn", "bm25,k=1,b=0.5"};
	public static final Boolean STOPWORD = true;
	public static final Boolean STEMMING = false;


	public static void main(String[] args) {
		long begin, end, total = 0;
		List<Document> docsBrut, docsXML;
		List<String> queries;
		ParserXMLElement parserXML;
		Indexator indexatorXML = new Indexator();

		HashMap<String, Map<Long, Long>> postingList = null;
		HashMap<Long, Map<String, Long>> postingListPerDoc = null;
		HashMap<String, Map<Long, Long>> postingListXML = null;
		HashMap<Long, Map<String, Long>> postingListPerDocXML = null;
		
		File query = new File("resources/topics_M2WI7Q_2019_20.txt");
		//File query = new File("resources/test-reduit/queryTest/query.txt");

		// parsing des documents
		//docsBrut = parserDocBrut("resources/test-reduit/TD");		
		//docsBrut = parserDocBrut("resources/textes_brut");
		
		begin = System.currentTimeMillis();
		//docsXML = parserDocXML("resources/test-reduit/XML");
		parserXML = parserDocXML("resources/coll");
		docsXML = parserXML.parse();
		indexatorXML = parserXML.getPostingLists();
		postingListXML = indexatorXML.getPostingList();
		postingListPerDocXML = indexatorXML.getPostingListPerDoc();
		end = System.currentTimeMillis();
		total += (end - begin);
		
		//OutPutFileParsingBrut(docsBrut);
		//OutPutFileParsingXML(docsXML);

		System.err.println("Memory Size : " + Runtime.getRuntime().totalMemory());
		System.err.println("Time parsing : " + ((end - begin) / 1000f));
		
		// indexation
		//Indexator indexator = new Indexator();
		//indexator.createIndex(docsBrut);
		//indexatorXML.createIndex(docsXML);
		//postingList = indexator.getPostingList();
		//postingListPerDoc = indexator.getPostingListPerDoc();
		
		//System.out.println("Indexator End");

		System.out.println("Taille " + docsXML.size());
		System.out.println("Posting list size : " + postingListXML.size());
		
		OutPutFilePostingList(postingListXML);
		//OutPutFilePostingListPerDoc(postingListPerDocXML);
		//System.out.println("Doc "+ docsBrut.get(1597).getIdDoc() + "   Brut " + docsBrut.get(1597).getLength() +
		//		" Doc " + docsXML.get(1597).getIdDoc() + "  XML " + docsXML.get(1597).getLength());

		// TEXTE BRUT : calcul du score des documents pour chaque requete et ecriture du run
		queries = readQuery(query);
		//writeAllRuns(queries, OUTPUT_DIR + "brut/", OUTPUT_NAME, "06", "articles", docsBrut, postingList, postingListPerDoc);

		// TEXTE XML : calcul du score des documents pour chaque requete et ecriture du run
		begin = System.currentTimeMillis();
		writeAllRuns(queries, OUTPUT_DIR + "xml/", OUTPUT_NAME, "07", GRANULARITE, docsXML, postingListXML, postingListPerDocXML);
		end = System.currentTimeMillis();
		total += (end - begin);
		System.err.println("Runs Time : " + (((end - begin) / 1000f)));
		
		System.err.println("Total Time : " + (total / 1000f));
		//System.out.println("Runs write");
	}

	// function : parserDoc(String pathResources, Document.Type type) output(List<Document>)  , type = "xml" ou "brut"
	public static List<Document> parserDocBrut(String path){
		ParserBrut parser = new ParserBrut(path);	
		return parser.parse();
	}
	
	public static ParserXMLElement parserDocXML(String path){
		ParserXMLElement parser = new ParserXMLElement(path);
		return parser;
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
		float score;

		if (number_doc > NUMBER_OF_DOCUMENT_BY_QUERY)
			number_doc = NUMBER_OF_DOCUMENT_BY_QUERY;

		for (int i = 0; i < number_doc; i++) {
			d = cosScore.get(i).getKey();
			
			// Pour garder des scores decroissants
			if (GRANULARITE.equals("elements")) {
				score = (number_doc - i);
			}else {
				score = cosScore.get(i).getValue();
			}
			
			buff.append(numQuery + " Q0 " + d.getIdDoc() + " " + (i+1) + " " + score + " " + nomEquipe + " " + d.getCheminDocument());
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
