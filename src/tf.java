import java.util.*;

public class tf {
	
//	public static ArrayList<String> getKeysFromIndex(List<Pair> termPostingList) {
//		ArrayList<String> keys = new ArrayList<String>();
//		for ( String key : termPostingList.keySet() ) {
//		    keys.add(key);
//		}
//		return keys;
//	}
	
	public static int b(String t, int docId, Map<Integer, List<Pair>> postingList) {
		ArrayList<Pair> listOfOccTerms = postingList.get(docId);
		ArrayList<String> terms = new ArrayList<String>();
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add(listOfOccTerms[i].getTerm());
		}
		
		int tf;
		if (terms.indexOf(t) >=0)	 tf= 1;
		else tf=0;
		return tf;
	}
	
	
	public static int n(String t, int docId, Map<Integer, List<Pair>> postingList) {
		ArrayList<Pair> listOfOccTerms = postingList.get(docId);
		int l = postingList.size();
		ArrayList<String> terms = new ArrayList<String>();
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add(listOfOccTerms[i].getTerm());
		}
		
		int tf;
		int i = terms.indexOf(t);
		if (i >=0)	 tf= listOfOccTerms[i].getNumOcc();
		else tf=0;
		return tf;
	}
	
	
	public static int n(String t, int docId, Map<Integer, List<Pair>> postingList) {
		ArrayList<Pair> listOfOccTerms = postingList.get(docId);
		int l = postingList.size();
		ArrayList<Integer> occ = new ArrayList<Integer>();
		ArrayList<String> terms = new ArrayList<String>();
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			occ.add(listOfOccTerms[i].getNumOcc());
			terms.add(listOfOccTerms[i].getTerm());
		}
		
		int tf;
		int i = terms.indexOf(t);
		if (i >=0)	 tf= listOfOccTerms[i].getNumOcc()/(Collection.max(occ)+0.0001);
		else tf=0;
		return tf;
	}
	
	
	
	
	
}
