import java.util.Map;
import java.util.Map.Entry;

public class normalization {
	
	public static float W(
			int docId,
			String term,
			String smart,   // format "***" 
			Map<Integer, Map<String, Long>> postingListDoc,
			Map<String, Map<Integer, Long>> postingListTerm) {
		
		switch(Character.toString(smart.charAt(3))) {
		case "n":
			return n(smart, term, docId, postingListDoc, postingListTerm);
		case "c":
			return c(smart, term, docId, postingListDoc, postingListTerm);
		case "s":
			return s(smart, term, docId, postingListDoc, postingListTerm);
		default:
			System.out.println("Pas de fonction definie");
			return 0;
		}

	}


	
	
	public static float n(
			String smart,   // format "***"
			String term,
			int docId,
			Map<Integer, Map<String, Long>> postingListDoc,
			Map<String, Map<Integer, Long>> postingListTerm)
	{
		
		String tfMethod = Character.toString(smart.charAt(1));  //function to use for tf
		String dfMethod = Character.toString(smart.charAt(2));  //function to use for idf
		float tf;
		float idf;
		float w;
		
		tf = TF.tf(tfMethod, term, docId, postingListDoc);
		idf = IDF.idf(dfMethod, term, postingListTerm, postingListDoc);
		w = tf*idf;
		
		return w;
		
	}
	
	
	public static float c(
			String smart,
			String term,
			int docId,
			Map<Integer, Map<String, Long>> postingListDoc,
			Map<String, Map<Integer, Long>> postingListTerm)
	{
		
		String tfMethod = Character.toString(smart.charAt(1));
		String dfMethod = Character.toString(smart.charAt(2));
		float tf;
		float idf;
		float w;
		float sum = 0;
		float sumElement;
		Map<String,Long> docMap = postingListDoc.get(docId); //Map of term,occ for doc=docId
		
		for(Entry<String, Long> mapentry : docMap.entrySet()) {
			sumElement = TF.tf(Character.toString(smart.charAt(1)), mapentry.getKey(), docId, postingListDoc);
			sum += Math.pow(sumElement,2); //sum of the square of tf(t',d) for all t' in d
		}
		
		tf = TF.tf(tfMethod, term, docId, postingListDoc);
		idf = IDF.idf(dfMethod, term, postingListTerm, postingListDoc);
		w = tf*idf/sum;
		
		return w;
		
	}
	
	
	public static float s(
			String smart,
			String term,
			int docId,
			Map<Integer, Map<String, Long>> postingListDoc,
			Map<String, Map<Integer, Long>> postingListTerm)
	{
		
		String tfMethod = Character.toString(smart.charAt(1));
		String dfMethod = Character.toString(smart.charAt(2));
		float tf;
		float idf;
		float w;
		float sum = 0;
		float sumElement;
		Map<String,Long> docMap = postingListDoc.get(docId); //Map of term,occ for doc=docId
		
		for(Entry<String, Long> mapentry : docMap.entrySet()) {
			sumElement = TF.tf(Character.toString(smart.charAt(1)), mapentry.getKey(), docId, postingListDoc);
			sum += sumElement; //sum of tf(t',d) for all t' in d
		}
		
		tf = TF.tf(tfMethod, term, docId, postingListDoc);
		idf = IDF.idf(dfMethod, term, postingListTerm, postingListDoc);
		w = tf*idf/sum;
		
		return w;
		
	}
//	
	
	
}
