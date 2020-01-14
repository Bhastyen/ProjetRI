import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.THashMap;



public class IDF {
	public static int df = 0;
	public static String prev_term = "";
	
	public static float idf(
			String smart, 
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap) {
		
		if (!term.equals(prev_term)) {
			
			if (Main.GRANULARITE == Document.Type_Element.DOCUMENT)   // si par Document on prends en compte que les articles
				df = df(postingList.get(term), docsMap);
			else df = postingList.get(term).size();
			prev_term = term;
		
		}
		
		switch(smart) {
		case "n":
			return  n();
		case "i":
			return  i(term, N, postingList, docsMap);
		case "t":
			return  i(term, N, postingList, docsMap);
		case "l":
			return  l(term, N, postingList, docsMap);
		case "f":
			return  f(term, N, postingList, docsMap);
		case "p":
			return  P(term, postingList, docsMap);
		case "s":
			return  s(term, N, postingList, docsMap);
		case "bm25":
			return  bm25(term, N, postingList, docsMap);
		default:
			System.out.println("Pas de fonction idf definie");
			return 0;
		}
			
	}
	
	public static float n() {
		
		float idf = (float) 1;
		
		return idf;
	}
	
	public static float i(
			String term, 
			int N, 	// number of documents
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap) {
		
		float idf; // Weight to return
		int n = df;

		/*if (Main.GRANULARITE == Document.Type_Element.DOCUMENT) {
			n = df(postingList.get(term), docsMap);
		} else {
			n = postingList.get(term).size();
		}*/
		
//		N=1000;
//		if (term.equals("a")) {
//			n=10;
//		}
//		if (term.equals("b")) {
//			n=25;
//		}
//		if (term.equals("c")) {
//			n=10;
//		}
//		if (term.equals("d")) {
//			n=24;
//		}
//		if (term.equals("e")) {
//			n=250;
//		}
//		System.out.println(n);
		
		idf = (float) Math.log10((float) N / n);
		
		return idf;
	}
	
	
	public static float l(
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap) {
		
		float idf;
		float n = df;
		
		idf = (float) Math.log10(1 + (float) N / n);
		return idf;
	}
	
	
	public static float f(
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap) {
		
		float idf;
		float n = df;
		
		idf = (float) (1.0/n);
		return idf;
	}
	
	
	public static float p(
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap) {

		float idf;
		float n = df;
		
		idf= (float) Math.log10((N-n)/(float) n);
		return idf;
	}


	public static float P(
			String term, 
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap) {
		
		float idf;
		List<Integer> N = new ArrayList<Integer>();  //List of all the df
		float n = df;
		int maxN;
		
        for (Map.Entry<String, TLongLongMap> mapentry : postingList.entrySet()) {
        	N.add(mapentry.getValue().size()); // for all terms, add the number of doc in which they appear
        }
		
        maxN = Collections.max(N);
		idf= (float) Math.log10(1+ (float) maxN/n);

		return idf;
	}
	
	
	public static float s(
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap) {
		
		float idf;
		float n = df;
		
		idf = (float) Math.log10((N + 1f)/n);
		idf = (float) Math.pow(idf, 2);
		
		return idf;
	}
	
	
	private static float bm25(
			String term, 
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap) {
		float idf;
		float n = df;

		/*if (Main.GRANULARITE == Document.Type_Element.DOCUMENT) {
			n = df(postingList.get(term), docsMap);
		} else {
			n = postingList.get(term).size();
		}*/

		// for test
		/*
		N = 1000;
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
		}*/
		
		//
		idf = (float) Math.log10((N - n + 0.5) / (n + 0.5));
//		System.out.println("idf :"+N + " "+ n);
		return idf;
	}
	
	// calul le bon df
	private static int df(TLongLongMap docs, Map<Long, Document> docsMap) {
		int compteur = 0;
		long[] keys = docs.keys();
		
		for (int i = 0; i < keys.length; i ++) {
			if (docsMap.get(keys[i]).getType() == Document.Type_Element.ARTICLE 
					&& docsMap.get(keys[i]).getCheminDocument().equals("article[1]/")) {
				compteur += 1;
			}
		}
		
		return compteur;
	}
	
}

