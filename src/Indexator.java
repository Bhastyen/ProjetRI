import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Indexator {

	private HashMap<String, Map<Long, Long>> postingList;
	private HashMap<Long, Map<String, Long>> postingListPerDoc;

	public Indexator() {
		postingList = new HashMap<String, Map<Long, Long>>();
		postingListPerDoc = new HashMap<Long, Map<String, Long>>();
	}

	public HashMap<String, Map<Long, Long>> getPostingList() {
		return postingList;
	}

	public HashMap<Long, Map<String, Long>> getPostingListPerDoc() {
		return postingListPerDoc;
	}


	public void createIndex(List<Document> listDoc) {
		List<String> listWords;
		
		long docid = 0;
		long nbOcc = 0;
		
		String stem;
		
		// Pour tous les documents
		for (Document doc : listDoc) {

			// On recupere le doc Id
			docid = doc.getId();

			// On split le string selon les espaces
			String[] splitString = doc.getStringDocument().split(" +");

			listWords = new ArrayList<>(Arrays.asList(splitString));

			//System.out.println(listWords.toString());
			Map<String, Long> frequencyMap =
					listWords.stream().collect(Collectors.groupingBy(Function.identity(),
															Collectors.counting()));
			// enleve le mot vide
			frequencyMap.remove("");
			
			// Put the document and its list of term/occurency in the HashMap
			postingListPerDoc.put(docid, frequencyMap);
			
			// Loop pour faire la posting list
			for (Map.Entry<String, Long> word : frequencyMap.entrySet()) {

					// On récupère le nombre d'occurence du mot
					nbOcc = word.getValue();   // TODO robertson coeff suivant type
					stem = word.getKey(); //Sans stemming
					//
					// Si le mot est dejà dans la posting list alors on recup la liste et on ajoute
					// la nouvelle pair
					
					if (postingList.containsKey(stem)) {
						postingList.get(stem).put(docid, nbOcc);
					}else {
						postingList.put(stem, new HashMap<Long, Long>());
						postingList.get(stem).put(docid, nbOcc);
					}
			}

		}
		
		// remove lonely words
		/*ArrayList<Entry<String, Map<Long, Long>>> posting = new ArrayList<>(postingList.entrySet());
		posting.removeIf(new Predicate<Entry<String, Map<Long, Long>>>() {
			@Override
			public boolean test(Entry<String, Map<Long, Long>> entry) {
				return (entry.getValue().size() <= 1);
			}
		});*/

		
		//System.out.println("Posting List Per Doc : " + postingListPerDoc.size() + "  " + listDoc.size());
		//System.out.println("Posting List : " + postingList.size());
		//System.out.println("Nouvelle Posting List : " + posting.size());
	}
}
