import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;



public class Document {
	public static final List<String> STOP_WORD = loadStopwords();
	public enum Type {XML, BRUT} ;
	public enum Type_Element {VIDE, ARTICLE, SECTION, PARAGRAPH, TITLE, BOLD, ITALIC, NAME, LINK, ELEMENT};
	
	private long id;
	private long idDoc;
	private String stringDocument;
	private String cheminDocument;
	private int length;
	private TLongList idFils;
	private Type_Element type = Type_Element.VIDE;
	
	
	public Document(long idDoc, String stringDocument) {
		String[] arrString = stringDocument.split(" ");

		this.id = idDoc;
		this.idDoc = idDoc;
		this.stringDocument = stringDocument.toLowerCase();
		cheminDocument = "/article[1]";
		setIdFils(new TLongArrayList());
		
		length = arrString.length;  // TODO robertson coeff suivant type
	}
	

	public Document(long idDoc, String stringDocument, String cheminDocument) {
		this(idDoc, stringDocument);
		this.setCheminDocument(cheminDocument);
	}
	
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getIdDoc() {
		return idDoc;
	}


	public void setIdDoc(long idDoc) {
		this.idDoc = idDoc;
	}


	public String getStringDocument() {
		return stringDocument;
	}
	
	public void setStringDocument(String stringDocument) {
		this.stringDocument = stringDocument;
	}
	
	public TLongList getIdFils() {
		return idFils;
	}


	public void setIdFils(TLongList idFils) {
		this.idFils = idFils;
	}

	public int get_length() {
		return length;
	}
	
	private static List<String> loadStopwords(){
		List<String> stopwords = null;
		
		try {
			stopwords = Files.readAllLines(Paths.get("resources/stop-words-eng.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return stopwords;
	}

	public static String removeStopWord(String original) {
		

		/*String[] allWords = original.toLowerCase().split(" ");
		//StringBuilder permet de ne pas surcharger le CPU , utile pour les concatenation
		StringBuilder builder = new StringBuilder();
		for(String word : allWords) {
			if(!stopword.contains(word)) {
				builder.append(word);
				builder.append(' ');
			}
		}

		 return builder.toString().trim();//trim enleve les surespacementss*/
		return "";

	}
	
	public static String createStemming(String original) {
		return Stemming.stemTerm(original);	
	}
	
	public static String sentenceProcessing(String original) {
		
		original = original.replaceAll("[^A-Za-z ]", " ");
		
		String[] allWords = original.toLowerCase().split(" ");
		//StringBuilder permet de ne pas surcharger le CPU , utile pour les concatenation
		StringBuilder builder = new StringBuilder();
		
		List<String> stopword = STOP_WORD;
		
		/*if (Main.STOPWORD) {//if STOPWORD
			try {
				stopword = loadStopwords();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
		for (String word : allWords) {
			if (Main.STOPWORD) {//if STOPWORD
				if (!stopword.contains(word)) {
					if (Main.STEMMING) {//if STEMMING
						word = createStemming(word);
					}
					
					builder.append(word);
					builder.append(' ');
				}
			} else if(Main.STEMMING) {//if STEMMING
				word = createStemming(word);
				builder.append(word);
				builder.append(' ');
			} else {
				builder.append(word);
				builder.append(' ');
			}
		}

		return builder.toString().trim();  // trim enleve les surespacementss
	}


	public String getCheminDocument() {
		return cheminDocument;
	}


	public void setCheminDocument(String cheminDocument) {
		this.cheminDocument = cheminDocument;
	}


	public Type_Element getType() {
		return type;
	}


	public void setType(Type_Element type) {
		this.type = type;
	}


	public static HashMap<Long, Document> getDocumentsHashMap(List<Document> docs) {
		HashMap<Long, Document> docsMap = new HashMap<>();
		
		for (int i = 0; i < docs.size(); i++) {
			docsMap.put(docs.get(i).getId(), docs.get(i));
		}
		
		return docsMap;
	}
}
