import opennlp.tools.stemmer.PorterStemmer;


public class Stemming{
		
	public static String stemTerm(String term){
		String pt = new PorterStemmer().stem(term);
	
		return pt;
	}
}