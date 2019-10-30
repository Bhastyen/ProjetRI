import java.util.*;

public class tf {
	
	
	public static int b(String t, int docId, Map<Integer, List<Pair>> postingList) {
		ArrayList<Pair> listOfOccTerms = postingList.get(docId);
		ArrayList<String> terms = new ArrayList<String>();
		int tf;
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add(listOfOccTerms[i].getValue());
		}
		
		if (terms.indexOf(t) >=0)	 tf= 1;
		else tf=0;
		return tf;
	}
	
	
	public static int n(String t, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);
		int l = postingList.size();
		List<String> terms = new ArrayList<String>();
		int tf;
		int i = terms.indexOf(t);
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add(listOfOccTerms[i].getValue());
		}
		
		if (i >=0)	 tf= listOfOccTerms[i].getNumOcc();
		else tf=0;
		return tf;
	}
	
	
	public static int m(String t, int docId, Map<Integer, List<Pair>> postingList) {
		ArrayList<Pair> listOfOccTerms = postingList.get(docId);
		int l = postingList.size();
		List<Integer> occ = new ArrayList<Integer>();
		List<String> terms = new ArrayList<String>();
		int tf;
		int i = terms.indexOf(t);
		
		for (int j=0; j<listOfOccTerms.size(); j++) {
			occ.add(listOfOccTerms[i].getNumOcc());
			terms.add(listOfOccTerms[i].getTerm());
		}
		
		if (i >=0)	 tf= listOfOccTerms[i].getNumOcc()/(Collections.max(occ)+0.0001);
		else tf=0;
		return tf;
	}
	
	
	
}
