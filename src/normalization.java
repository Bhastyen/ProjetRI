import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.THashMap;

public class normalization {

	public static float W(String smart, // format "***"
			String term, long docId, long dl, int N, THashMap<String, TLongLongMap> postingList,
			Map<String, Object> otherParameters) {

		switch (Character.toString(smart.charAt(2))) {
		case "n":
			return n(smart, term, docId, N, postingList);
		case "c":
			return c(smart, term, docId, N, postingList);
		case "s":
			return s(smart, term, docId, N, postingList);
		case "u":
			return u(smart, term, docId, dl, N, postingList, otherParameters);
		case "2":
			if (Main.ROBERTSON == true) {
				return bm25f(smart, term, docId, dl, N, postingList, otherParameters);
			} else {
				return bm25(smart, term, docId, dl, N, postingList, otherParameters);
			}

		default:
			System.out.println("Pas de fonction W definie");
			return 0;
		}

	}

	public static float n(String smart, // format "***"
			String term, long docId, int N, THashMap<String, TLongLongMap> postingList) {

		String tfMethod = Character.toString(smart.charAt(0)); // function to use for tf
		String dfMethod = Character.toString(smart.charAt(1)); // function to use for idf
		float tf;
		float idf;
		float w;

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);
		// System.out.println("TF : " + tf + " IDF : " + idf);
		w = tf * idf;

		return w;
	}

	public static float c(String smart, // format "***"
			String term, long docId, int N, THashMap<String, TLongLongMap> postingList) {

		String tfMethod = Character.toString(smart.charAt(0));
		String dfMethod = Character.toString(smart.charAt(1));
		float tf;
		float idf;
		float w;
		float sum = 0;
		float sumElement;

		for (Entry<String, TLongLongMap> entry : postingList.entrySet()) {
			if (entry.getValue().containsKey(docId)) {
				tf = TF.tf(tfMethod, entry.getKey(), docId, postingList);
				idf = IDF.idf(dfMethod, entry.getKey(), N, postingList);
				sumElement = tf * idf;
				sum += Math.pow(sumElement, 2); // sum of the square of tf(t',d) for all t' in d
			}
		}

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);
		w = (float) (tf * idf / Math.sqrt(sum));

		return w;

	}

	public static float s(String smart, // format "***"
			String term, long docId, int N, THashMap<String, TLongLongMap> postingList) {

		String tfMethod = Character.toString(smart.charAt(0));
		String dfMethod = Character.toString(smart.charAt(1));
		float tf;
		float idf;
		float w;
		float sum = 0;
		float sumElement;

		for (Entry<String, TLongLongMap> entry : postingList.entrySet()) {
			if (entry.getValue().containsKey(docId)) {
				tf = TF.tf(tfMethod, entry.getKey(), docId, postingList);
				idf = IDF.idf(dfMethod, entry.getKey(), N, postingList);
				sumElement = tf * idf;
				sum += Math.pow(sumElement, 2); // sum of the square of tf(t',d) for all t' in d
			}
		}

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);
		w = tf * idf / sum;

		return w;

	}

	public static float u(String smart, String term, long docId, long dl, int N,
			THashMap<String, TLongLongMap> postingList, Map<String, Object> otherParameters) {

		String tfMethod = Character.toString(smart.charAt(0));
		String dfMethod = Character.toString(smart.charAt(1));
		float slope = (float) otherParameters.get("slope");
		float pivot = (float) otherParameters.get("pivot");
		float tf, idf, w;
		float ave_dl = (float) otherParameters.get("ave_len");
		float nt = 0;

		// compute nt: distinct terms in the document we're working on
		for (Entry<String, TLongLongMap> entry : postingList.entrySet()) {
			if (entry.getValue().containsKey(docId)) {
				nt++;
			}
		}

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);

		w = (float) ( (tf*idf) / (1+Math.log(dl/ave_dl)) / ((1-slope)*pivot + slope*nt) );
		return w;
	}

	public static float bm25(String smart, // format : "bm25,___.___,___.___"
			String term, long docId, long dl, int N, THashMap<String, TLongLongMap> postingList,
			Map<String, Object> otherParameters) {
		String tfMethod = "n";
		String dfMethod = "bm25";
		float k = (float) otherParameters.get("k");
		float b = (float) otherParameters.get("b");
		float tf, idf, w;
		float ave_dl = (float) otherParameters.get("ave_len");

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);

//		System.out.println("TF : " + tf + "  IDF : " + idf + " doc " + docId);

		w = (tf * (k + 1)) / (tf + k * (1 - b + b * (dl / ave_dl))) * idf; // BM25 formula
		return w;
	}

	public static float bm25f(String smart, // format : "bm25,___.___,___.___"
			String term, long docId, long dl, int N, THashMap<String, TLongLongMap> postingList,
			Map<String, Object> otherParameters) {

		String dfMethod = "bm25";
		float k = (float) otherParameters.get("k");
		float b = (float) otherParameters.get("b");

		float tf, idf, w;
		float ave_dl = (float) otherParameters.get("ave_len");

		HashMap<Long, Document> docsMap = (HashMap<Long, Document>) otherParameters.get("docMap");
		List<Float> alphas = (List<Float>) otherParameters.get("alphas");
		HashMap<Document.Type_Element, Float> alphaType = new HashMap<Document.Type_Element, Float>();
		alphaType.put(Document.Type_Element.TITLE, alphas.get(0));
		alphaType.put(Document.Type_Element.BODY, alphas.get(1));
		alphaType.put(Document.Type_Element.SECTION, alphas.get(2));
		alphaType.put(Document.Type_Element.VIDE, 1F);
		alphaType.put(Document.Type_Element.ARTICLE, 1F);
		alphaType.put(Document.Type_Element.ELEMENT, 1F);
		alphaType.put(Document.Type_Element.DOCUMENT, 1F);

		tf = TF.f(term, docId, postingList, docsMap, alphaType);

		idf = IDF.idf(dfMethod, term, N, postingList);

		dl = dlBM25f(docId, docsMap, alphaType);
		
//		ave_dl = 23;

		
		w = (tf * (k + 1)) / (tf + k * (1 - b + b * (dl / ave_dl))) * idf; // BM25 formula
//		System.out.println("ave "  + ave_dl);
		return w;
	}

	public static long dlBM25f(long docId, HashMap<Long, Document> docsMap, HashMap<Document.Type_Element, Float> alphaType) {
		Document doc = docsMap.get(docId);

		long dl = 0L;
		long dlTemp = 0L;
		if (doc.getIdFils().size() == 0) {
			dlTemp = doc.get_length();
			dl += dlTemp * alphaType.get(doc.getType());
		} else {
			for (int i = 0; i < doc.getIdFils().size(); i++) {
				Document child = docsMap.get(doc.getIdFils().get(i));
				dl += (dlBM25f(child.getId(), docsMap, alphaType)) * alphaType.get(doc.getType());
			}
		}

		return dl;
	}

	/// Other functions useful for avoiding repeating operations each time
	public static float pivot(THashMap<String, TLongLongMap> postingList, int N) {
		float pivot = 0;

		for (Entry<String, TLongLongMap> entry : postingList.entrySet()) { // for each documents
			pivot += entry.getValue().size(); // add the distinct terms to the average
		}

		pivot /= N; // divide the total of distinct terms by the number of doc

		return pivot;
	}

	public static float ave_len(HashMap<Long, Document> docsMap, int N) {
		float ave_len = 0;
		Long docId;

		for (Entry<Long, Document> entry : docsMap.entrySet()) { // for each documents
			docId = entry.getKey();
			ave_len += entry.getValue().get_length(); // ...add the length of this document

		}

		ave_len /= N; // divide the total of occurrence by the number of doc
		return ave_len;
	}
	
	public static float ave_len_f(HashMap<Long, Document> docsMap, int N, List<Float> alphas) {
		float ave_len = 0;
		Long docId;

		HashMap<Document.Type_Element, Float> alphaType = new HashMap<Document.Type_Element, Float>();
		alphaType.put(Document.Type_Element.TITLE, alphas.get(0));
		alphaType.put(Document.Type_Element.BODY, alphas.get(1));
		alphaType.put(Document.Type_Element.SECTION, alphas.get(2));
		alphaType.put(Document.Type_Element.VIDE, 1F);
		alphaType.put(Document.Type_Element.ARTICLE, 1F);
			
		for (Entry<Long, Document> entry : docsMap.entrySet()) { // for each documents
			docId = entry.getKey();
			ave_len += dlBM25f(docId, docsMap, alphaType); // ...add the length of this document

		}

		ave_len /= N; // divide the total of occurrence by the number of doc
		return ave_len;
	}

	public static float ave_len_doc(HashMap<Long, Document> docsMap, int N) {
		float ave_len = 0;
		Long docId;
		
		for(Entry<Long, Document> entry : docsMap.entrySet()) { // for each documents
			docId = entry.getKey();
			
			if (entry.getValue().getType() == Document.Type_Element.ARTICLE && entry.getValue().getCheminDocument().equals("article[1]/")) {
				ave_len += entry.getValue().get_length(); 						// ...add the length of this document
			}
			
		}
		
		ave_len /= N; // divide the total of occurrence by the number of doc
		return ave_len;
	}
	
	public static float k(String smart) {
		// System.out.println("Smart : " + smart);
		String[] splitted = smart.split(",");
		String k_string = splitted[1].split("=")[1];

		float k = Float.valueOf(k_string.trim()).floatValue();
		return k;
	}

	public static List<Float> alphas(String smart) {

		List<Float> alphas = new ArrayList<>();

		String[] splitted = smart.split(",");
		String a_title_str = splitted[3].split("=")[1];
		String a_body_str = splitted[4].split("=")[1];
		String a_sec_str = splitted[5].split("=")[1];

		float a_title = Float.valueOf(a_title_str.trim()).floatValue();
		float a_body = Float.valueOf(a_body_str.trim()).floatValue();
		float a_sec = Float.valueOf(a_sec_str.trim()).floatValue();

		alphas.add(a_title);
		alphas.add(a_body);
		alphas.add(a_sec);
		return alphas;
	}

	public static float b(String smart) {
		String[] splitted = smart.split(",");
		String b_string = splitted[2].split("=")[1];

		float b = Float.valueOf(b_string.trim()).floatValue();
		return b;
	}

	public static float slope(String smart) {
		String slope_string = smart.split(",")[1];

		float slope = Float.valueOf(slope_string.trim()).floatValue();
		return slope;
	}
}
