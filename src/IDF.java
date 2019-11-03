import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IDF {
	
	public static float idf(
			String smart, 
			String term, 
			Map<String, Map<Integer,Long>> postingListTerm, 
			Map<Integer, Map<String,Long>> postingListDoc) {
		
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
			return (float) p(term, postingListTerm, postingListDoc);
		case "P":
			return (float) P(term, postingListTerm, postingListDoc);
		case "s":
			return (float) s(term, postingListTerm, postingListDoc);
		default:
			System.out.println("Pas de fonction idf definie");
			return 0;
		}
			
	}
	
	public static int n(
			String term, 
			Map<String, Map<Integer, Long>> postingListTerm, 
			Map<Integer, Map<String, Long>> postingListDoc) {
		
		int idf;
		
		idf= 1;
		
		return idf;
	}
	
	public static double i(
			String term, 
			Map<String, Map<Integer, Long>> postingListTerm, 
			Map<Integer, Map<String, Long>> postingListDoc) {
		
		double idf; // Weight to return
		int N = postingListDoc.size();   // number of documents
		Map<Integer, Long> occDoc = postingListTerm.get(term);   // list of pair of NumOcc,DocId for the term
		int n = occDoc.size(); // number of documents which contain term
		
		idf= Math.log(N/n);
		return idf;
		
	}
	
	
	public static double l(
			String term, 
			Map<String, Map<Integer, Long>> postingListTerm, 
			Map<Integer, Map<String, Long>> postingListDoc) {
		
		double idf;
		int N = postingListDoc.size(); //number of documents in the data
		Map<Integer, Long> occDoc = postingListTerm.get(term);  //Map of docId,occurrence for the term
		int n = occDoc.size(); // count how many documents contain the term
		
		idf= Math.log(1 + N/n);
		return idf;
	}
	
	
	public static double f(
			String term, 
			Map<String, Map<Integer, Long>> postingListTerm, 
			Map<Integer, Map<String, Long>> postingListDoc) {
		
		double idf;
		Map<Integer, Long> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		

		idf=1/n;
		return idf;
	}
	
	
	public static double p(
			String term, 
			Map<String, Map<Integer, Long>> postingListTerm, 
			Map<Integer, Map<String, Long>> postingListDoc) {

		double idf;
		int N = postingListDoc.size();
		Map<Integer, Long> occDoc = postingListTerm.get(term);
		int n = occDoc.size();


		idf= Math.log((N-n)/n);
		return idf;
	}


	public static double P(
			String term, 
			Map<String, Map<Integer, Long>> postingListTerm, 
			Map<Integer, Map<String, Long>> postingListDoc) {
		
		double idf;
		List<Integer> N = new ArrayList<Integer>();  //List of all the df
		Map<Integer, Long> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		int maxN;
		

        for (Map.Entry<String, Map<Integer, Long>> mapentry : postingListTerm.entrySet()) {
			Map<Integer, Long> TermMap = (Map<Integer, Long>) mapentry.getValue();
        	N.add(TermMap.size()); // for all terms, add the number of doc in which they appear
        }
		
        maxN = Collections.max(N);

		idf= Math.log(1+ maxN/n);
		return idf;
	}
	
	
	public static double s(
			String term,
			Map<String, Map<Integer, Long>> postingListTerm, 
			Map<Integer, Map<String, Long>> postingListDoc) {
		
		double idf;
		int N = postingListDoc.size();
		Map<Integer, Long> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		

		idf = Math.log((N+1)/n);
		idf = Math.pow(idf, 2);
		return idf;
	}
	
	
	
}
