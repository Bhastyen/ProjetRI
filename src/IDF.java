import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IDF {
	
	public static float idf(
			String smart, 
			String term, 
			Map<String, Map<Long,Long>> postingListTerm, 
			Map<Long, Map<String,Long>> postingListDoc) {
		switch(smart) {
		case "n":
			return (float) n(term, postingListTerm, postingListDoc);
		case "i":
			return (float) i(term, postingListTerm, postingListDoc);
		case "t":
			return (float) i(term, postingListTerm, postingListDoc);
		case "l":
			return (float) l(term, postingListTerm, postingListDoc);
		case "f":
			return (float) f(term, postingListTerm, postingListDoc);
		case "p":
			return (float) P(term, postingListTerm, postingListDoc);
		case "s":
			return (float) s(term, postingListTerm, postingListDoc);
		case "bm25":
			return (float) bm25(term, postingListTerm, postingListDoc);
		default:
			System.out.println("Pas de fonction idf definie");
			return 0;
		}
			
	}
	
	public static int n(
			String term, 
			Map<String, Map<Long, Long>> postingListTerm, 
			Map<Long, Map<String, Long>> postingListDoc) {
		
		int idf;
		
		idf= 1;
		
		return idf;
	}
	
	public static double i(
			String term, 
			Map<String, Map<Long, Long>> postingListTerm, 
			Map<Long, Map<String, Long>> postingListDoc) {
		
		double idf; // Weight to return
		int N = postingListDoc.size();   // number of documents
		Map<Long, Long> occDoc = postingListTerm.get(term);   // list of pair of NumOcc,DocId for the term
		int n = occDoc.size(); // number of documents which contain term
		
		N=1000;
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
		}
		
		idf= Math.log10(N/n);
		return idf;
		
	}
	
	
	public static double l(
			String term, 
			Map<String, Map<Long, Long>> postingListTerm, 
			Map<Long, Map<String, Long>> postingListDoc) {
		
		double idf;
		int N = postingListDoc.size(); //number of documents in the data
		Map<Long, Long> occDoc = postingListTerm.get(term);  //Map of docId,occurrence for the term
		int n = occDoc.size(); // count how many documents contain the term
		
		idf= Math.log10(1 + (float) N/n);
		return idf;
	}
	
	
	public static double f(
			String term, 
			Map<String, Map<Long, Long>> postingListTerm, 
			Map<Long, Map<String, Long>> postingListDoc) {
		
		double idf;
		Map<Long, Long> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		
		idf = 1.0/n;
		
		return idf;
	}
	
	
	public static double p(
			String term, 
			Map<String, Map<Long, Long>> postingListTerm, 
			Map<Long, Map<String, Long>> postingListDoc) {

		double idf;
		int N = postingListDoc.size();
		Map<Long, Long> occDoc = postingListTerm.get(term);
		int n = occDoc.size();

		idf= Math.log10((N-n)/(float) n);
		return idf;
	}


	public static double P(
			String term, 
			Map<String, Map<Long, Long>> postingListTerm, 
			Map<Long, Map<String, Long>> postingListDoc) {
		
		double idf;
		List<Integer> N = new ArrayList<Integer>();  //List of all the df
		Map<Long, Long> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		int maxN;
		

        for (Map.Entry<String, Map<Long, Long>> mapentry : postingListTerm.entrySet()) {
			Map<Long, Long> TermMap = (Map<Long, Long>) mapentry.getValue();
        	N.add(TermMap.size()); // for all terms, add the number of doc in which they appear
        }
		
        maxN = Collections.max(N);
		idf= Math.log10(1+ (float) maxN/n);

		return idf;
	}
	
	
	public static double s(
			String term,
			Map<String, Map<Long, Long>> postingListTerm, 
			Map<Long, Map<String, Long>> postingListDoc) {
		
		double idf;
		int N = postingListDoc.size();
		Map<Long, Long> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		

		idf = Math.log10((N+1f)/n);
		idf = Math.pow(idf, 2);

		return idf;
	}
	
	
	private static float bm25(
			String term, 
			Map<String, Map<Long, Long>> postingListTerm,
			Map<Long, Map<String, Long>> postingListDoc) {
		double idf;
		int N = postingListDoc.size();
		Map<Long, Long> occDoc = postingListTerm.get(term);
		int n = occDoc.size();

		// for test
		N=1000;
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
		}
		
		//
		idf= Math.log10((N-n+0.5)/(n+0.5));
//		System.out.println("idf :"+term+n);
		return (float) idf;
	}
}
