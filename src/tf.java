import java.util.*;

public class tf {
	
	
	public static int b(String term, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);
		List<String> terms = new ArrayList<String>();
		int tf;
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add(listOfOccTerms[i].getValue());
		}
		
		if (terms.indexOf(term) >=0)	 tf= 1;
		else tf=0;
		return tf;
	}
	
	
	public static int n(String term, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);
		int l = postingList.size();
		List<String> terms = new ArrayList<String>();
		int tf;
		int i = terms.indexOf(term);
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add(listOfOccTerms[i].getValue());
		}
		
		if (i >=0)	 tf= listOfOccTerms[i].getNumOcc();
		else tf=0;
		return tf;
	}
	
	
	public static int m(String term, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);
		int l = postingList.size();
		List<Integer> occ = new ArrayList<Integer>();
		List<String> terms = new ArrayList<String>();
		int tf;
		int i = terms.indexOf(term);
		
		for (int j=0; j<listOfOccTerms.size(); j++) {
			occ.add(listOfOccTerms[i].getNumOcc());
			terms.add(listOfOccTerms[i].getTerm());
		}
		
		if (i >=0)	 tf= listOfOccTerms[i].getNumOcc()/(Collections.max(occ)+0.0001);
		else tf=0;
		return tf;
	}
	
	
	
}
