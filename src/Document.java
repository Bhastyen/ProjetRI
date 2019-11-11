import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Document {
	public enum Type {XML, BRUT} ;
	
	private int idDoc;
	private String stringDocument;
	
	
	public Document(int idDoc, String stringDocument) {
		super();
		this.idDoc = idDoc;
		this.stringDocument = stringDocument.toLowerCase();
	}
	
	
	
	public int getIdDoc() {
		return idDoc;
	}
	
	public void setIdDoc(int idDoc) {
		this.idDoc = idDoc;
	}
	
	public String getStringDocument() {
		return stringDocument;
	}
	
	public void setStringDocument(String stringDocument) {
		this.stringDocument = stringDocument;
	}
	
	public int getLength() {
		int c = 0;
		String[] arrString = stringDocument.split(" ");
		
		/*for (int i = 0; i < arrString.length; i++) {
			if (arrString[i] == "" || arrString[i] == " ") {
				c += 1;
			}
		}
		
		System.out.println("Ligne vide doc " + idDoc + " : " + c);*/
		
		return arrString.length;
		
	}
	
	private static List<String> loadStopwords() throws IOException {
		
		List<String> stopwords = Files.readAllLines(Paths.get("resources/stop-words-eng.txt"));
		return stopwords;
	}

	public static String removeStopWord(String original) {
		List<String> stopword = new ArrayList<String>();;
		try {
			stopword = loadStopwords();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] allWords = original.toLowerCase().split(" ");
		//StringBuilder permet de ne pas surcharger le CPU , utile pour les concatenation
		StringBuilder builder = new StringBuilder();
		for(String word : allWords) {
			if(!stopword.contains(word)) {
				builder.append(word);
				builder.append(' ');
			}
		}

		 return builder.toString().trim();//trim enleve les surespacementss

	}
}
