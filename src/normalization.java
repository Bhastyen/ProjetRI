import java.util.Map;
import java.util.Map.Entry;



public class normalization {
	
	public static float W(
			String smart,   // format "***" 
			String term,
			int docId,
			Map<Integer, Map<String, Long>> postingListDoc,
			Map<String, Map<Integer, Long>> postingListTerm,
			boolean queryMod) {
		
		if (queryMod == false) {
			switch(Character.toString(smart.charAt(2))) {
			case "n":
				return n(smart, term, docId, postingListDoc, postingListTerm);
			case "c":
				return c(smart, term, docId, postingListDoc, postingListTerm);
			case "s":
				return s(smart, term, docId, postingListDoc, postingListTerm);
			case "2":
				return bm25(smart, term, docId, postingListDoc, postingListTerm);
			default:
				System.out.println("Pas de fonction W definie");
				return 0;
			}
			
		} else {
			return query(smart, term, docId, postingListDoc, postingListTerm);
		}
	
	}


	
	
	private static float query(
			String smart, 
			String term, 
			int docId, Map<Integer, Map<String, Long>> postingListDoc,
			Map<String, Map<Integer, Long>> postingListTerm) 
	{
		String dfMethod = Character.toString(smart.charAt(1));  //function to use for idf
		float idf;
		
		idf = IDF.idf(dfMethod, term, postingListTerm, postingListDoc);
		
		return idf;
	}




	public static float n(
			String smart,   // format "***"
			String term,
			int docId,
			Map<Integer, Map<String, Long>> postingListDoc,
			Map<String, Map<Integer, Long>> postingListTerm)
	{
		
		String tfMethod = Character.toString(smart.charAt(0));  //function to use for tf
		String dfMethod = Character.toString(smart.charAt(1));  //function to use for idf
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
		
		String tfMethod = Character.toString(smart.charAt(0));
		String dfMethod = Character.toString(smart.charAt(1));
		float tf;
		float idf;
		float w;
		float sum = 0;
		float sumElement;
		Map<String,Long> docMap = postingListDoc.get(docId); //Map of term,occ for doc=docId
		
		for(Entry<String, Long> mapentry : docMap.entrySet()) {
			tf = TF.tf(tfMethod, mapentry.getKey(), docId, postingListDoc);
			idf = IDF.idf(dfMethod, mapentry.getKey(), postingListTerm, postingListDoc);
			sumElement = tf*idf;
			sum += Math.pow(sumElement,2); //sum of the square of tf(t',d) for all t' in d
		}
		
		tf = TF.tf(tfMethod, term, docId, postingListDoc);
		idf = IDF.idf(dfMethod, term, postingListTerm, postingListDoc);
		w = (float) (tf*idf/Math.sqrt(sum));
		
		return w;
		
	}
	
	
	public static float s(
			String smart,
			String term,
			int docId,
			Map<Integer, Map<String, Long>> postingListDoc,
			Map<String, Map<Integer, Long>> postingListTerm)
	{
		
		String tfMethod = Character.toString(smart.charAt(0));
		String dfMethod = Character.toString(smart.charAt(1));
		float tf;
		float idf;
		float w;
		float sum = 0;
		float sumElement;
		Map<String,Long> docMap = postingListDoc.get(docId); //Map of term,occ for doc=docId
		
		for(Entry<String, Long> mapentry : docMap.entrySet()) {
			sumElement = TF.tf(tfMethod, mapentry.getKey(), docId, postingListDoc);
			sum += sumElement; //sum of tf(t',d) for all t' in d
		}
		
		tf = TF.tf(tfMethod, term, docId, postingListDoc);
		idf = IDF.idf(dfMethod, term, postingListTerm, postingListDoc);
		w = tf*idf/sum;
		
		return w;
		
	}

	
	public static float bm25(
			String smart,	// format : "bm25, ___.___, ___.___"
			String term,
			int docId,
			Map<Integer, Map<String, Long>> postingListDoc,
			Map<String, Map<Integer, Long>> postingListTerm)
	{
		String tfMethod = "n";
		String dfMethod = "bm25";
		float k=0, b=0;
		float tf, idf, w;
		int ave_len=0, doc_len=0;
		
		
		// convert k and b from string to float :
		String[] splitted = smart.split(",");
		String k_string = splitted[1];
		String b_string = splitted[2];
		
		 try 
		  {
		      k= Float.valueOf(k_string.trim()).floatValue();
		      b = Float.valueOf(b_string.trim()).floatValue();
		  }
		  catch (NumberFormatException nfe)   {
		      System.err.println("NumberFormatException: " + nfe.getMessage());
		  }
	
		 
		// compute average doc length : doc_len = all tokens / numbers of doc
		 Map<String, Long> doc_pairs;
		 for(Entry<Integer, Map<String, Long>> docMap : postingListDoc.entrySet()) { // for each documents
			 doc_pairs = docMap.getValue();
			 for(Entry<String, Long> pair : doc_pairs.entrySet()) { // for each term per documents...
				 ave_len += pair.getValue(); 						// ...add the occurrence of this term
			 }
		 }
		ave_len /= postingListDoc.size(); // divide the total of occurrence by the number of doc
		
		
		// compute length of the document we're working on
		Map<String,Long> docMap = postingListDoc.get(docId);
		for(Entry<String, Long> pair : docMap.entrySet()) { // for each term of the document...
			 doc_len += pair.getValue();					// ... add the occurrence of this term
		 }
		

		tf = TF.tf(tfMethod, term, docId, postingListDoc);
		idf = IDF.idf(dfMethod, term, postingListTerm, postingListDoc);
		w = (tf*(k+1)) / (tf + k *(1-b+b*(doc_len/ave_len))) * idf; // BM25 formula
		
		return w;
	}



	
}
