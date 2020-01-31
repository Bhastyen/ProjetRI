import java.util.*;
import java.util.Map.Entry;

import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.THashMap;

public class TF {

	public static float tf(String smart, String term, long docId, THashMap<String, TLongLongMap> postingList) { 
		switch (smart) { 	// select the correct tf function depending of the SMART specification
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

	public static float b(String term, long docId, THashMap<String, TLongLongMap> postingList) {

		float tf = 1;
		return tf;
	}

	public static float n(String term, long docId, THashMap<String, TLongLongMap> postingList) {
		long tf = postingList.get(term).get(docId);
		return tf;
	}

	public static float m(String term, long docId, THashMap<String, TLongLongMap> postingList) {
		TLongLongMap docMap = postingList.get(term);
		TLongLongIterator ite = docMap.iterator();
		List<Long> occ = new ArrayList<Long>();
		float tf;

		while (ite.hasNext()) {
			ite.advance();
			occ.add(ite.value());
		}

		tf = docMap.get(docId);
		tf = (float) (tf / (Collections.max(occ) + 0.00001));

		return tf;
	}

	public static float a(String term, long docId, THashMap<String, TLongLongMap> postingList) {
		TLongLongMap docMap = postingList.get(term);
		TLongLongIterator ite = docMap.iterator();
		List<Long> occ = new ArrayList<Long>();
		float tf;

		while (ite.hasNext()) {
			ite.advance();
			occ.add(ite.value());
		}

		/*
		 * for(Entry<Long, Long> entry : docMap.entrySet()) { occ.add(entry.getValue());
		 * }
		 */

		tf = docMap.get(docId);
		tf = (float) (0.5 + 0.5 * tf / (Collections.max(occ) + 0.00001));

		return tf;
	}

	public static float s(String term, long docId, THashMap<String, TLongLongMap> postingList) {
		float tf;
		tf = postingList.get(term).get(docId);

		tf = (float) Math.pow(tf, 2);

		return tf;
	}

	public static float l(String term, long docId, THashMap<String, TLongLongMap> postingList) {
		float tf = 0;

		tf = postingList.get(term).get(docId);

		tf = (float) (1 + Math.log10(tf));
		return tf;
	}

	
	public static float f(String term, long docId, THashMap<String, TLongLongMap> postingList,	// function tf for BM25f : take alphas into account
			HashMap<Long, Document> docsMap, HashMap<Document.Type_Element, Float> alphaType) { 
		
		Document doc = docsMap.get(docId);


		float tf = 0F;
		float tfTemp = 0F;
		if(doc.getIdFils().size() == 0){
			tfTemp = postingList.get(term).get(docId);
			tf += tfTemp * alphaType.get(doc.getType());
		}else {
			for(int i =0; i<doc.getIdFils().size(); i++){
				Document child = docsMap.get(doc.getIdFils().get(i));
				tf += (f(term, child.getId(), postingList, docsMap, alphaType)) * alphaType.get(doc.getType());
			}
		}
		return tf;
	}
}
