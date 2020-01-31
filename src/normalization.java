import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.THashMap;


public class normalization {
	
	public static float W(
			String smart,   // format "***" 
			String term,
			long docId,
			long dl,
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<String, Object> otherParameters,
			Map<Long, Document> docsMap) {

		switch (Character.toString(smart.charAt(2))) { // select the good normalization (SMART or BM25 or BM25f) depending of the specifications of Main.PARAMETERS
		case "n":
			return n(smart, term, docId, N, postingList, docsMap);
		case "c":
			return c(smart, term, docId, N, postingList, docsMap);
		case "s":
			return s(smart, term, docId, N, postingList, docsMap);
		case "u":
			return u(smart, term, docId, dl, N, postingList, otherParameters, docsMap);
		case "2":
			if (Main.ROBERTSON == true) {
				return bm25f(smart, term, docId, dl, N, postingList, otherParameters);
			} else {
				return bm25(smart, term, docId, dl, N, postingList, otherParameters, docsMap);
			}
		default:
			System.out.println("Pas de fonction W definie");
			return 0;
		}

	}

	public static float n(
			String smart,   // format "***"
			String term,
			long docId,
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap)
	{
		
		String tfMethod = Character.toString(smart.charAt(0));  //function to use for tf
		String dfMethod = Character.toString(smart.charAt(1));  //function to use for idf
		float tf;
		float idf;
		float w;

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList, docsMap);
		//System.out.println("TF : " + tf + "  IDF : " + idf);
		w = tf * idf;
		
		return w;
	}
	
	
	public static float c(
			String smart,   // format "***"
			String term,
			long docId,
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap)
	{
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
				idf = IDF.idf(dfMethod, entry.getKey(), N, postingList, docsMap);
				sumElement = tf * idf;
				sum += Math.pow(sumElement, 2); // sum of the square of tf(t',d) for all t' in d
			}
		}

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList, docsMap);
		w = (float) (tf * idf / Math.sqrt(sum));

		return w;
	}

	public static float s(
			String smart,   // format "***"
			String term,
			long docId,
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<Long, Document> docsMap)
	{
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
				idf = IDF.idf(dfMethod, entry.getKey(), N, postingList, docsMap);
				sumElement = tf * idf;
				sum += Math.pow(sumElement, 2); //sum of the square of tf(t',d) for all t' in d
			}
		}

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList, docsMap);
		w = tf * idf / sum;

		return w;
	}

	public static float u(		
			String smart,
			String term,
			long docId,
			long dl,
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<String, Object> otherParameters,
			Map<Long, Document> docsMap)	{

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
		idf = IDF.idf(dfMethod, term, N, postingList, docsMap);
		w = (float) ( (tf*idf) / (1+Math.log(dl/ave_dl)) / ((1-slope)*pivot + slope*nt) );
		
		return w;
	}
	
	public static float bm25(
			String smart,	// format : "bm25,___.___,___.___"
			String term,
			long docId,
			long dl,
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<String, Object> otherParameters,
			Map<Long, Document> docsMap)
	{
		String tfMethod = "n";
		String dfMethod = "bm25";
		float k = (float) otherParameters.get("k");
		float b = (float) otherParameters.get("b");
		float tf, idf, w;
		float ave_dl = (float) otherParameters.get("ave_len");

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList, docsMap);

		w = (tf * (k + 1)) / (tf + k * (1 - b + b * (dl / ave_dl))) * idf;  // BM25 formula

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
		// get the correct alpha weights to the associated element
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
		idf = IDF.idf(dfMethod, term, N, postingList, docsMap);

		dl = dlBM25f(docId, docsMap, alphaType);
		
		w = (tf * (k + 1)) / (tf + k * (1 - b + b * (dl / ave_dl))) * idf; // BM25 formula
		return w;
	}

	// Other functions useful for avoiding repeating operations each time
	public static long dlBM25f(long docId,
			HashMap<Long, Document> docsMap,
			HashMap<Document.Type_Element, Float> alphaType) { // recursive function for computing the document length for bm25f
		
		Document doc = docsMap.get(docId);
		long dl = 0L;
		long dlTemp = 0L;

		if (doc.getIdFils().size() == 0) {
			dlTemp = doc.get_length();
			if (alphaType.containsKey(doc.getType()))
				dl += dlTemp * alphaType.get(doc.getType());
			else dl += dlTemp * alphaType.get(Document.Type_Element.VIDE);   // si coef element non determine on multiplit par le coeff d'un element non connu  
		} else {
			for (int i = 0; i < doc.getIdFils().size(); i++) {
				Document child = docsMap.get(doc.getIdFils().get(i));
				long result = dlBM25f(child.getId(), docsMap, alphaType);   // crée une erreur si MIN_LENGTH_AUTHORIZED est different de 0
				
				if (alphaType.containsKey(doc.getType()))
					dl += result * alphaType.get(doc.getType());
				else dl += result * alphaType.get(Document.Type_Element.VIDE);  // si coef element non determine on multiplit par le coeff d'un element non connu
			}
		}

		return dl;
	}


	public static float ave_len(HashMap<Long, Document> docsMap, int N) {
		float ave_len = 0;
		Long docId = 0L;

		for (Entry<Long, Document> entry : docsMap.entrySet()) { // for each documents
			docId = entry.getKey();
			ave_len += entry.getValue().get_length(); // ...add the length of this document
		}

		ave_len /= N; // divide the total of occurrence by the number of doc
		
		return ave_len;
	}
	
	public static float ave_len_f(HashMap<Long, Document> docsMap, int N, List<Float> alphas) { // same as ave_len but taking the modified doc length into account
		float ave_len = 0;
		Long docId;

		HashMap<Document.Type_Element, Float> alphaType = new HashMap<Document.Type_Element, Float>();
		alphaType.put(Document.Type_Element.TITLE, alphas.get(0));
		alphaType.put(Document.Type_Element.BODY, alphas.get(1));
		alphaType.put(Document.Type_Element.SECTION, alphas.get(2));
		alphaType.put(Document.Type_Element.VIDE, 1F);
		alphaType.put(Document.Type_Element.ARTICLE, 1F);
			
		for (Entry<Long, Document> entry : docsMap.entrySet()) { 
			docId = entry.getKey();
			ave_len += dlBM25f(docId, docsMap, alphaType);
		}

		ave_len /= N;
		
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
	
	public static float k(String smart) {	// parse the k parameter from Main.PARAMETERS
		String[] splitted = smart.split(",");
		String k_string = splitted[1].split("=")[1];

		float k = Float.valueOf(k_string.trim()).floatValue();
		return k;
	}

	public static float b(String smart) { 	// parse the b parameter from Main.PARAMETERS
		String[] splitted = smart.split(",");
		String b_string = splitted[2].split("=")[1];

		float b = Float.valueOf(b_string.trim()).floatValue();
		return b;
	}

	public static List<Float> alphas(String smart) {

		List<Float> alphas = new ArrayList<>();

		String[] splitted = smart.split(",");
		String a_1_str = splitted[3].split("=")[1];
		String a_2_str = splitted[4].split("=")[1];
		String a_3_str = splitted[5].split("=")[1];

		float a_1 = Float.valueOf(a_1_str.trim()).floatValue();
		float a_2 = Float.valueOf(a_2_str.trim()).floatValue();
		float a_3 = Float.valueOf(a_3_str.trim()).floatValue();

		alphas.add(a_1);
		alphas.add(a_2);
		alphas.add(a_3);
		
		return alphas;
	}

	public static float slope(String smart) { // parse the slope parameter from Main.PARAMETERS
		String slope_string = smart.split(",")[1];

		float slope = Float.valueOf(slope_string.trim()).floatValue();
		return slope;
	}
	
	public static float pivot(THashMap<String, TLongLongMap> postingList, int N) { // compute the pivot for **u normalization 
		float pivot = 0;

		for (Entry<String, TLongLongMap> entry : postingList.entrySet()) { // for each documents
			pivot += entry.getValue().size(); // add the distinct terms to the average
		}

		pivot /= N; // divide the total of distinct terms by the number of doc

		return pivot;
	}
}
