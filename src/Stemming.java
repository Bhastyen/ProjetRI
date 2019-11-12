import opennlp.tools.stemmer.PorterStemmer;


public class Stemming{
	
	public static void main(String[] args) {
		System.out.println(stemTerm("annoying"));
	}
	
	public static String stemTerm(String term){
		return new PorterStemmer().stem(term);
	}
}