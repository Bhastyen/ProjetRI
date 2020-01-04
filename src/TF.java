import java.util.*;
import java.util.Map.Entry;

public class TF {
	
	
	public static float tf(String smart, String term, long docId, Map<Long, Map<String, Long>> postingList) {
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
			System.out.println("Pas de fonction tf definie");
			return 0;
		}
			
	}
	

	public static float b(String term, long docId, Map<Long, Map<String, Long>> postingList) {
		try {
			Map<String, Long> docMap = postingList.get(docId);
			long tf = docMap.get(term);
			tf = 1;
			return tf;
		} catch (Exception ie) {return 0;} 
	}



	
	public static float n(String term, long docId, Map<Long, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		System.out.println(term);
		long tf = docMap.get(term);

		return tf;
	}
	
	
	public static float m(String term, long docId, Map<Long, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		List<Long> occ = new ArrayList<Long>();
		float tf;
		
		for(Entry<String, Long> entry : docMap.entrySet()) {
			occ.add(entry.getValue());
		}
		
		tf= docMap.get(term);
		tf = (float) (tf / (Collections.max(occ)+0.00001));
		
		return tf;
	}
	
	public static float a(String term, long docId, Map<Long, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		List<Long> occ = new ArrayList<Long>();
		float tf;
		
		for(Entry<String, Long> entry : docMap.entrySet()) {
			occ.add(entry.getValue());
		}
		
		tf= docMap.get(term);
		tf = (float) (0.5 + 0.5*tf/(Collections.max(occ)+0.00001));

		return tf;
	}
	
	
	public static float s(String term, long docId, Map<Long, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		float tf = docMap.get(term);
		
		tf = (float) Math.pow(tf, 2);

		return tf;
	}
	
	
	public static float l(String term, long docId, Map<Long, Map<String, Long>> postingListDoc) {
		Map<String, Long> docMap = postingListDoc.get(docId);
		float tf = 0;
		
		if (docMap.containsKey(term))
			tf = docMap.get(term);

		tf = (float) (1 + Math.log10(tf));
		
		return (float) (1f + Math.log10(tf));
	}
	
	
	public static float L(String term, long docId, Map<Long, Map<String, Long>> postingList) {
		Map<String, Long> docMap = postingList.get(docId);
		long occ = 0;
		long positiveocc = 0;
		float tf;
		
		for(Entry<String, Long> entry : docMap.entrySet()) {
			occ += entry.getValue();
			positiveocc += 1;
		}
		
		tf= docMap.get(term);
		tf = (float) ((1+ Math.log10(tf)) / (1 + Math.log10(occ/positiveocc)));

		return tf;
	}
}
