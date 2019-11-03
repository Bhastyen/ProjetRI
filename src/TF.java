import java.util.*;
import java.util.Map.Entry;

public class TF {
	
	
	public static long tf(String smart, String term, int docId, Map<Integer, Map<String, Long>> postingList) {
		// need postingList with docId as key
		
//		try{
//			Map<String, Long> docMap = postingList.get(docId);
//			long tf = docMap.get(term);
//		} catch (Exception ie) {
//			System.out.println("term not in this document");
//		}
		
		switch(smart) {
		case "b":
			return b(term, docId, postingList);
		case "n":
			return n(term, docId, postingList);
		case "m":
			return m(term, docId, postingList);
		case "a":
			return a(term, docId, postingList);
		case "s":
			return s(term, docId, postingList);
		case "l":
			return l(term, docId, postingList);
		default:
			System.out.println("Pas de fonction definie");
			return 0;
		}
			
	}
	
	
	public static long b(String term, int docId, Map<Integer, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		long tf = docMap.get(term);
		
		return tf;
	}
	
	
	public static long n(String term, int docId, Map<Integer, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		long tf = docMap.get(term);

		return tf;
	}
	
	
	public static long m(String term, int docId, Map<Integer, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		List<Long> occ = new ArrayList<Long>();
		long tf;
		
		for(Entry<String, Long> entry : docMap.entrySet()) {
			occ.add(entry.getValue());
		}
		
		tf= docMap.get(term);
		tf = (long) (tf / (Collections.max(occ)+0.00001));
		
		return tf;
	}
	
	public static long a(String term, int docId, Map<Integer, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		List<Long> occ = new ArrayList<Long>();
		long tf;
		
		for(Entry<String, Long> entry : docMap.entrySet()) {
			occ.add(entry.getValue());
		}
		
		tf= docMap.get(term);
		tf = (long) (0.5 + 0.5*tf/(Collections.max(occ)+0.00001));

		return tf;
	}
	
	
	public static long s(String term, int docId, Map<Integer, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		long tf = docMap.get(term);
		
		tf = (long) Math.pow(tf, 2);

		return tf;
	}
	
	
	public static long l(String term, int docId, Map<Integer, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		long tf = docMap.get(term);

		tf = (long) (1 + Math.log(tf));
			
		return tf;
	}
}
