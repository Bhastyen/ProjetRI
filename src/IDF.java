import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IDF {
	
	public static float idf(
			String smart, 
			String term, 
			int N,
			Map<String, Map<Long,Long>> postingList) {
		switch(smart) {
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
			Map<String, Map<Long, Long>> postingList) {
		
		float idf; // Weight to return
		int n = postingList.get(term).size(); // number of documents which contain term
		
//		N=1000;
//		if (term.equals("a")) {
//			n=10;
//		}
//		if (term.equals("b")) {
//			n=25;
//		}
//		if (term.equals("c")) {
//			n=10;
//		}
//		if (term.equals("d")) {
//			n=24;
//		}
//		if (term.equals("e")) {
//			n=250;
//		}
//		System.out.println(n);
		idf = (float) Math.log10((float) N / n);
		return idf;
		
	}
	
	
	public static float l(
			String term, 
			int N,
			Map<String, Map<Long, Long>> postingList) {
		
		float idf;
		int n = postingList.get(term).size(); // count how many documents contain the term
		
		idf= (float) Math.log10(1 + (float) N / n);
		return idf;
	}
	
	
	public static float f(
			String term, 
			int N,
			Map<String, Map<Long, Long>> postingList) {
		
		float idf;
		int n = postingList.get(term).size();
		
		idf = (float) (1.0/n);
		return idf;
	}
	
	
	public static float p(
			String term, 
			int N,
			Map<String, Map<Long, Long>> postingList) {

		float idf;
		int n = postingList.get(term).size();
		
		idf= (float) Math.log10((N-n)/(float) n);
		return idf;
	}


	public static float P(
			String term, 
			Map<String, Map<Long, Long>> postingList) {
		
		float idf;
		List<Integer> N = new ArrayList<Integer>();  //List of all the df
		int n = postingList.get(term).size();
		int maxN;
		

        for (Map.Entry<String, Map<Long, Long>> mapentry : postingList.entrySet()) {
        	N.add(mapentry.getValue().size()); // for all terms, add the number of doc in which they appear
        }
		
        maxN = Collections.max(N);
		idf= (float) Math.log10(1+ (float) maxN/n);

		return idf;
	}
	
	
	public static float s(
			String term, 
			int N,
			Map<String, Map<Long, Long>> postingList) {
		
		float idf;
		int n = postingList.get(term).size();
		
		idf = (float) Math.log10((N+1f)/n);
		idf = (float) Math.pow(idf, 2);
		return idf;
	}
	
	
	private static float bm25(
			String term, 
			int N,
			Map<String, Map<Long, Long>> postingList) {
		float idf;
		int n = postingList.get(term).size();

		// for test
		/*
		N = 1000;
		if (term.equals("a")) {
			n=10;
		}
		if (term.equals("b")) {
			n=25;
		}
		if (term.equals("c")) {
			n=10;
		}
		if (term.equals("d")) {
			n=24;
		}
		if (term.equals("e")) {
			n=250;
		}*/
		
		//
		idf = (float) Math.log10((N - n + 0.5) / (n + 0.5));
//		System.out.println("idf :"+N + " "+ n);
		return idf;
	}
}
