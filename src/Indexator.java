import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;



public class Indexator {

	private HashMap<String, Map<Integer, Long>> postingList;
	private HashMap<Integer, Map<String, Long>> postingListPerDoc;

	public Indexator() {
		postingList = new HashMap<String, Map<Integer, Long>>();
		postingListPerDoc = new HashMap<Integer, Map<String, Long>>();
	}

	public HashMap<String, Map<Integer, Long>> getPostingList() {
		return postingList;
	}

	public HashMap<Integer, Map<String, Long>> getPostingListPerDoc() {
		return postingListPerDoc;
	}


	public void createIndex(List<Document> listDoc) {
		List<String> listWords;

		int docid = 0;
		long nbOcc = 0;

		// Pour tous les documents
		for (Document doc : listDoc) {

			// On récupère le doc Id
			docid = doc.getIdDoc();

			// On split le string selon les espaces
			String[] splitString = doc.getStringDocument().split(" +");

			listWords = new ArrayList<>(Arrays.asList(splitString));

			//System.out.println(listWords.toString());
			Map<String, Long> frequencyMap =
					listWords.stream().collect(Collectors.groupingBy(Function.identity(),
															Collectors.counting()));
			// Put the document and its list of term/occurency in the HashMap
			postingListPerDoc.put(docid, frequencyMap);

			// Loop pour faire la posting list
			for (Map.Entry<String,Long> word : frequencyMap.entrySet()) {

					// On récupère le nombre d'occurence du mot
					nbOcc = word.getValue();

					// Si le mot est dejà dans la posting list alors on recup la liste et on ajoute
					// la nouvelle pair
					if (postingList.containsKey(word.getKey())) {
						postingList.get(word.getKey()).put(docid, nbOcc);
					}else {
						postingList.put(word.getKey(), new HashMap<Integer, Long>());
						postingList.get(word.getKey()).put(docid, nbOcc);
					}
			}

		}

		System.out.println(postingListPerDoc.size() + "  " + listDoc.size());
		System.out.println(postingList.size());
	}
}
