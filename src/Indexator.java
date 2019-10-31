import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Indexator {

	private static HashMap<String, Map<Integer, Long>> postingList = new HashMap<String, Map<Integer, Long>>();
	private static HashMap<Integer, Map<String, Long>> postingListPerDoc = new HashMap<Integer, Map<String, Long>>();

	public Indexator() {
	}

	public static HashMap<String, Map<Integer, Long>> getPostingList() {
		return postingList;
	}

	public static HashMap<Integer, Map<String, Long>> getPostingListPerDoc() {
		return postingListPerDoc;
	}

	public void createIndex(List<Document> listDoc) {

		listDoc = new ArrayList<Document>(listDoc);

		Map<Integer, Long> postingListPairs = new HashMap<>();

		List<String> dico = new ArrayList<>();
		List<String> wordInPostingListPerDoc = new ArrayList<>();
		List<String> listWords;

		int docid = 0;
		long nbOcc = 0;

		// Pour tous les documents
		for (Document doc : listDoc) {
			// On récupère le doc Id
			docid = doc.getIdDoc();
			
			// On split le string selon les espaces
			String[] splitString = doc.getStringDocument().split(" ");
			
			listWords = new ArrayList<>(Arrays.asList(splitString));
			
			Map<String, Long> frequencyMap =
					listWords.stream().collect(Collectors.groupingBy(Function.identity(), 
															Collectors.counting()));
			// Put the document and its list of term/occurency in the HashMap
			Map<String, Long> clone = new HashMap<>(frequencyMap);
			postingListPerDoc.put(docid, clone);

			// Loop pour faire la posting list
			for (String word : splitString) {
				// On met le mot dans le dico pour garder trace entre les documents
				if (!dico.contains(word)) {
					dico.add(word);
				}
				// La liste wordInPostingListPerDoc permet de ne pas faire de doublons pour un
				// document
				if (wordInPostingListPerDoc.contains(word)) {
					break;
				} else {
					wordInPostingListPerDoc.add(word);

					// On récupère le nombre d'occurence du mot
					nbOcc = frequencyMap.get(word);
					
					// Si le mot est dejà dans la posting list alors on recup la liste et on ajoute
					// la nouvelle pair
					if (postingList.containsKey(word)) {
						postingListPairs = postingList.get(word);
					}
					postingListPairs.put(docid,nbOcc);
					// On ajoute la list updated ou nouvelle
					Map<Integer, Long> clone2 = new HashMap<>(postingListPairs);
					postingList.put(word, clone2);
					System.out.println(postingList);
				}
				// On vide la liste des pairs
				postingListPairs.clear();
			}
			// On vide tout hop hop hop
			wordInPostingListPerDoc.clear();
		}
		System.out.println(postingListPerDoc);
		System.out.println(postingList);
	}
}
