import java.util.*;
import java.util.Map.Entry;

public class TF {
	
	
	public static float tf(String smart, String term, long docId, Map<String, Map<Long, Long>> postingList) {
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
//		case "m":
//			return m(term, docId, postingList);
//		case "a":
//			return a(term, docId, postingList);
//		case "s":
//			return s(term, docId, postingList);
		case "l":
			return l(term, docId, postingList);
		default:
			System.out.println("Pas de fonction tf definie");
			return 0;
		}
			
	}
	

	public static float b(String term, long docId, Map<String, Map<Long, Long>> postingList) {

		float tf = 1;
		return tf;
	}


	
	public static float n(String term, long docId, Map<String, Map<Long, Long>> postingList) {
		long tf = postingList.get(term).get(docId);
		return tf;
	}
	
	
	public static float m(String term, long docId, Map<String, Map<Long, Long>> postingList) {
		Map<Long, Long> docMap = postingList.get(term);
		List<Long> occ = new ArrayList<Long>();
		float tf;
		
		for(Entry<Long, Long> entry : docMap.entrySet()) {
			occ.add(entry.getValue());
		}
		
		tf= docMap.get(docId);
		tf = (float) (tf / (Collections.max(occ)+0.00001));
		
		return tf;
	}
	
	public static float a(String term, long docId, Map<String, Map<Long, Long>> postingList) {
		Map<Long, Long> docMap = postingList.get(term);
		List<Long> occ = new ArrayList<Long>();
		float tf;
		
		for(Entry<Long, Long> entry : docMap.entrySet()) {
			occ.add(entry.getValue());
		}
		
		tf= docMap.get(docId);
		tf = (float) (0.5 + 0.5*tf/(Collections.max(occ)+0.00001));

		return tf;
	}
	
	
	public static float s(String term, long docId, Map<String, Map<Long, Long>> postingList) {
		float tf;
		tf = postingList.get(term).get(docId);
		
		tf = (float) Math.pow(tf, 2);

		return tf;
	}
	
	
	public static float l(String term, long docId, Map<String, Map<Long, Long>> postingList) {
		float tf = 0;
		
		tf = postingList.get(term).get(docId);
		
		tf = (float) (1 + Math.log10(tf));
		return tf;
	}
	
	

}
