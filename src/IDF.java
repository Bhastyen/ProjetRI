import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.THashMap;

public class IDF {
	
	public static float idf(
			String smart, 
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList) {
		switch(smart) { // select the correct tf function depending of the SMART specification
		case "n":
			return  n();
		case "i":
			return  i(term, N, postingList);
		case "t":
			return  i(term, N, postingList);
		case "l":
			return  l(term, N, postingList);
		case "f":
			return  f(term, N, postingList);
		case "p":
			return  P(term, postingList);
		case "s":
			return  s(term, N, postingList);
		case "bm25":
			return  bm25(term, N, postingList);
		default:
			System.out.println("Pas de fonction idf definie");
			return 0;
		}
			
	}
	
	public static float n() {
		
		float idf = (float) 1;
		
		return idf;
	}
	
	public static float i(
			String term, 
			int N, 	// number of documents
			THashMap<String, TLongLongMap> postingList) {
		
		float idf; // Weight to return
		int n = postingList.get(term).size(); // number of documents which contain term

		idf = (float) Math.log10((float) N / n);
		
		return idf;
	}
	
	
	public static float l(
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList) {
		
		float idf;
		int n = postingList.get(term).size(); // count how many documents contain the term
		
		idf = (float) Math.log10(1 + (float) N / n);
		return idf;
	}
	
	
	public static float f(
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList) {
		
		float idf;
		int n = postingList.get(term).size();
		
		idf = (float) (1.0/n);
		return idf;
	}
	
	
	public static float p(
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList) {

		float idf;
		int n = postingList.get(term).size();
		
		idf= (float) Math.log10((N-n)/(float) n);
		return idf;
	}


	public static float P(
			String term, 
			THashMap<String, TLongLongMap> postingList) {
		
		float idf;
		List<Integer> N = new ArrayList<Integer>();  //List of all the df
		int n = postingList.get(term).size();
		int maxN;
		
        for (Map.Entry<String, TLongLongMap> mapentry : postingList.entrySet()) {
        	N.add(mapentry.getValue().size()); // for all terms, add the number of doc in which they appear
        }
		
        maxN = Collections.max(N);
		idf= (float) Math.log10(1+ (float) maxN/n);

		return idf;
	}
	
	
	public static float s(
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList) {
		
		float idf;
		int n = postingList.get(term).size();
		
		idf = (float) Math.log10((N + 1f)/n);
		idf = (float) Math.pow(idf, 2);
		
		return idf;
	}
	
	
	private static float bm25( // idf for BM25 function
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList) {
		float idf;
		int n = postingList.get(term).size();

		idf = (float) Math.log10((N - n + 0.5) / (n + 0.5));

		return idf;
	}
}
