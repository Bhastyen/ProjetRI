import java.util.ArrayList;
import java.util.HashMap;
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
			Map<String, Float> otherParameters) {


		switch(Character.toString(smart.charAt(2))) {
		case "n":
			return n(smart, term, docId, N, postingList);
		case "c":
			return c(smart, term, docId, N, postingList);
		case "s":
			return s(smart, term, docId, N, postingList);
		case "u":
			return u(smart, term, docId, dl, N, postingList, otherParameters);
		case "2":
			return bm25(smart, term, docId, dl, N, postingList, otherParameters);
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
			THashMap<String, TLongLongMap> postingList)
	{
		
		String tfMethod = Character.toString(smart.charAt(0));  //function to use for tf
		String dfMethod = Character.toString(smart.charAt(1));  //function to use for idf
		float tf;
		float idf;
		float w;
		
		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);
		//System.out.println("TF : " + tf + "  IDF : " + idf);
		w = tf*idf;
		
		return w;
	}
	
	
	public static float c(
			String smart,   // format "***"
			String term,
			long docId,
			int N,
			THashMap<String, TLongLongMap> postingList)
	{
		
		String tfMethod = Character.toString(smart.charAt(0));
		String dfMethod = Character.toString(smart.charAt(1));
		float tf;
		float idf;
		float w;
		float sum = 0;
		float sumElement;
		
		for(Entry<String, TLongLongMap> entry : postingList.entrySet()) {
			if (entry.getValue().containsKey(docId)){
				tf = TF.tf(tfMethod, entry.getKey(), docId, postingList);
				idf = IDF.idf(dfMethod, entry.getKey(), N, postingList);
				sumElement = tf*idf;
				sum += Math.pow(sumElement,2); //sum of the square of tf(t',d) for all t' in d
			}
		}
		
		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);
		w = (float) (tf*idf/Math.sqrt(sum));
		
		return w;
		
	}
	
	
	public static float s(
			String smart,   // format "***"
			String term,
			long docId,
			int N,
			THashMap<String, TLongLongMap> postingList)
	{
		
		String tfMethod = Character.toString(smart.charAt(0));
		String dfMethod = Character.toString(smart.charAt(1));
		float tf;
		float idf;
		float w;
		float sum = 0;
		float sumElement;
		
		for(Entry<String, TLongLongMap> entry : postingList.entrySet()) {
			if (entry.getValue().containsKey(docId)){
				tf = TF.tf(tfMethod, entry.getKey(), docId, postingList);
				idf = IDF.idf(dfMethod, entry.getKey(), N, postingList);
				sumElement = tf*idf;
				sum += Math.pow(sumElement,2); //sum of the square of tf(t',d) for all t' in d
			}
		}
		
		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);
		w = tf*idf/sum;
		
		return w;
		
	}

	
	
	public static float u(		
			String smart,
			String term,
			long docId,
			long dl,
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<String, Float> otherParameters)	{


		String tfMethod = Character.toString(smart.charAt(0));
		String dfMethod = Character.toString(smart.charAt(1));
		float slope=otherParameters.get("slope");
		float pivot=otherParameters.get("pivot");
		float tf, idf, w;
		float ave_dl=otherParameters.get("ave_len");
		float nt=0;

		// compute nt: distinct terms in the document we're working on
		for(Entry<String, TLongLongMap> entry : postingList.entrySet()) {
			if (entry.getValue().containsKey(docId)){
				nt++;
			}
		}
		
		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);
		w = (float) (   (tf*idf)/(1+Math.log(dl/ave_dl)) / ((1-slope)*pivot + slope*nt)   );
		return w;
	}
	
	public static float bm25(
			String smart,	// format : "bm25,___.___,___.___"
			String term,
			long docId,
			long dl,
			int N,
			THashMap<String, TLongLongMap> postingList,
			Map<String, Float> otherParameters)
	{
		String tfMethod = "n";
		String dfMethod = "bm25";
		float k = otherParameters.get("k");
		float b = otherParameters.get("b");
		float tf, idf, w;
		float ave_dl = otherParameters.get("ave_len");

		tf = TF.tf(tfMethod, term, docId, postingList);
		idf = IDF.idf(dfMethod, term, N, postingList);
		
//		System.out.println("TF : " + tf + "  IDF : " + idf + " doc " + docId);
		
		ave_dl = 20;
		
		
		w = (tf * (k + 1)) / (tf + k * (1 - b + b * (dl / ave_dl))) * idf; // BM25 formula
		return w;
	}
	
	
	
	/// Other functions useful for avoiding repeating operations each time
	public static float pivot(THashMap<String, TLongLongMap> postingList, int N) {
		float  pivot = 0;
		
		for(Entry<String, TLongLongMap> entry : postingList.entrySet()) { // for each documents
			pivot += entry.getValue().size();		// add the distinct terms to the average
		}
		
		pivot /= N; // divide the total of distinct terms by the number of doc
		
		return pivot;
	}
	
	public static float ave_len(HashMap<Long, Document> docsMap, int N) {
		float ave_len = 0;
		Long docId;
		
		for(Entry<Long, Document> entry : docsMap.entrySet()) { // for each documents
			docId = entry.getKey();
			ave_len += entry.getValue().get_length(); 						// ...add the length of this document
			
		}
		
		ave_len /= N; // divide the total of occurrence by the number of doc
		return ave_len;
	}
	
	public static float k(String smart) {
		//System.out.println("Smart : " + smart);
		String[] splitted = smart.split(",");
		String k_string = splitted[1].split("=")[1];
		
		float k = Float.valueOf(k_string.trim()).floatValue();
		return k;
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
