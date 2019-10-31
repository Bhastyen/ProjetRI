import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class idf {
	
	public static float tf(
			String smart, 
			String term, 
			Map<String, List<Pair<Integer,Integer>>> postingListTerm, 
			Map<Integer, List<Pair<Integer,Integer>>> postingListDoc) {
		
		switch(smart) {
		case "n":
			return n(term, postingListTerm, postingListDoc);
		case "i":
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
			System.out.println("Pas de fonction definie");
			return 0;
		}
			
	}
	
	public static int n(
			String term, 
			Map<String, List<Pair<Integer,Integer>>> postingListTerm, 
			Map<Integer, List<Pair<Integer,Integer>>> postingListDoc) {
		
		int idf;
		
		idf= 1;
		
		return idf;
	}
	
	public static double i(
			String term, 
			Map<String, List<Pair<Integer,Integer>>> postingListTerm, 
			Map<Integer, List<Pair<Integer,Integer>>> postingListDoc) {
		
		double idf; // Weight to return
		int N = postingListDoc.size();   // number of documents
		List<Pair<Integer, Integer>> occDoc = postingListTerm.get(term);   // list of pair of NumOcc,DocId for the term
		int n = occDoc.size(); // number of documents which contain term
		
		if (n != 0) {
			idf= Math.log(N/n);
			return idf;
		}
		else {
			System.out.print("0 occurence of this term in the documents");
			return 0;
		}
		
	}
	
	
	public static double l(
			String term, 
			Map<String, List<Pair<Integer,Integer>>> postingListTerm, 
			Map<Integer, List<Pair<Integer,Integer>>> postingListDoc) {
		
		double idf;
		int N = postingListDoc.size();
		List<Pair<Integer, Integer>> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		
		if (n != 0) {
			idf= Math.log(1 + N/n);
			return idf;
		}
		else {
			System.out.print("0 occurence of this term in the documents");
			return 0;
		}
		
	}
	
	public static double f(
			String term, 
			Map<String, List<Pair<Integer,Integer>>> postingListTerm, 
			Map<Integer, List<Pair<Integer,Integer>>> postingListDoc) {
		
		double idf;
		List<Pair<Integer, Integer>> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		
		if (n != 0) {
			idf=1/n;
			return idf;
		}
		else {
			System.out.print("0 occurence of this term in the documents");
			return 0;
		}
		
	}
	
	
	public static double p(
			String term, 
			Map<String, List<Pair<Integer,Integer>>> postingListTerm, 
			Map<Integer, List<Pair<Integer,Integer>>> postingListDoc) {
		
		double idf;
		int N = postingListDoc.size();
		List<Pair<Integer, Integer>> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		
		if (n != 0) {
			idf= Math.log((N-n)/n);
			return idf;
		}
		else {
			System.out.print("0 occurence of this term in the documents");
			return 0;
		}
		
	}
	
	
	public static double P(
			String term, 
			Map<String, List<Pair<Integer,Integer>>> postingListTerm, 
			Map<Integer, List<Pair<Integer,Integer>>> postingListDoc) {
		
		double idf;
		List<Integer> N = new ArrayList<Integer>();
		List<Pair<Integer, Integer>> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		int maxN;
		
		//Boucle for
        for (Map.Entry mapentry : postingListTerm.entrySet()) {
           N.add( ((List<Pair>) mapentry.getValue()).size() );
        }
		
        maxN = Collections.max(N);
		if (n != 0) {
			idf= Math.log(1+ maxN/n);
			return idf;
		}
		else {
			System.out.print("0 occurence of this term in the documents");
			return 0;
		}
		
	}
	
	public static double s(
			String term,
			Map<String, List<Pair<Integer,Integer>>> postingListTerm, 
			Map<Integer, List<Pair<Integer,Integer>>> postingListDoc) {
		
		double idf;
		int N = postingListDoc.size();
		List<Pair<Integer, Integer>> occDoc = postingListTerm.get(term);
		int n = occDoc.size();
		
		if (n != 0) {
			idf = Math.log((N+1)/n);
			idf = Math.pow(idf, 2);
			return idf;
		}
		else {
			System.out.print("0 occurence of this term in the documents");
			return 0;
		}
		
	}
	
}
