import java.util.*;

public class TF {
	
	
	public static float tf(String smart, String term, int docId, Map<Integer, List<Pair>> postingList) {
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
	
	
	public static int b(String term, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);
		List<String> terms = new ArrayList<String>();
		int tf;
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add((String) listOfOccTerms.get(i).getValue());
		}
		
		if (terms.indexOf(term) >=0)	 tf= 1;
		else tf=0;
		return tf;
	}
	
	
	public static int n(String term, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);
		List<String> terms = new ArrayList<String>();
		int tf;
		int index = terms.indexOf(term);
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add((String) listOfOccTerms.get(i).getValue());
		}
		
		if (index >=0)	 tf= (int) (listOfOccTerms.get(index)).getNumOcc();
		else tf=0;
		return tf;
	}
	
	
	public static float m(String term, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);

		List<Integer> occ = new ArrayList<Integer>();
		List<String> terms = new ArrayList<String>();
		float tf;
		int index = terms.indexOf(term);
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			occ.add((Integer) listOfOccTerms.get(i).getNumOcc());
			terms.add((String) listOfOccTerms.get(i).getValue());
		}
		
		if (index >=0) {
			tf= (float) (listOfOccTerms.get(index)).getNumOcc();
			tf = (float) (tf / (Collections.max(occ)+0.00001));
		}
		else tf=0;
		return tf;
	}
	
	public static float a(String term, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);
		List<Integer> occ = new ArrayList<Integer>();
		List<String> terms = new ArrayList<String>();
		float tf;
		int index = terms.indexOf(term);
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			occ.add((Integer) listOfOccTerms.get(i).getNumOcc());
			terms.add((String) listOfOccTerms.get(i).getValue());
		}
		
		if (index >=0) {
			tf= (float) (listOfOccTerms.get(index)).getNumOcc();
			tf = (float) (0.5 + 0.5*tf/(Collections.max(occ)+0.00001));
		}
		else tf=0;
		return tf;
	}
	
	
	public static int s(String term, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);
		List<String> terms = new ArrayList<String>();
		int tf;
		int index = terms.indexOf(term);
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add((String) listOfOccTerms.get(i).getValue());
		}
		
		if (index >=0)	 {
			tf = (int) listOfOccTerms.get(index).getNumOcc();
			tf = (int) Math.pow(tf, 2);
		}
		else tf=0;
		return tf;
	}
	
	
	public static float l(String term, int docId, Map<Integer, List<Pair>> postingList) {
		List<Pair> listOfOccTerms = postingList.get(docId);
		List<String> terms = new ArrayList<String>();
		float tf;
		int index = terms.indexOf(term);
		
		for (int i=0; i<listOfOccTerms.size(); i++) {
			terms.add((String) listOfOccTerms.get(i).getValue());
		}
		
		if (index >=0)	 {
			tf= (int) (listOfOccTerms.get(index)).getNumOcc();
			tf = (float) (1 + Math.log(tf));
			
		}
		else tf=0;
		return tf;
	}
}
