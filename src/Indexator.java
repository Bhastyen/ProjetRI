import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongLongHashMap;



public class Indexator {
	private THashMap<String, TLongLongMap> postingList;

	public Indexator() {
		postingList = new THashMap<String, TLongLongMap>();
	}

	public THashMap<String, TLongLongMap> getPostingList() {
		return postingList;
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

			Map<String, Long> frequencyMap =
					listWords.stream().collect(Collectors.groupingBy(Function.identity(),
															Collectors.counting()));
			// enleve le mot vide
			frequencyMap.remove("");
			
			// Loop pour faire la posting list
			for (Map.Entry<String, Long> word : frequencyMap.entrySet()) {
				// On recupere le nombre d'occurence du mot
				nbOcc = word.getValue();   // TODO robertson coeff suivant type
				stem = word.getKey(); // Sans stemming

				// Si le mot est deja dans la posting list alors on recup la liste et on ajoute
				// la nouvelle paire doc_id / tf
				
				if (postingList.containsKey(stem)) {
					postingList.get(stem).put(docid, nbOcc);
				}else {
					postingList.put(stem, new TLongLongHashMap());
					postingList.get(stem).put(docid, nbOcc);
				}
			}

		}
		
		// remove lonely words
		/*ArrayList<Entry<String, TLongLongMap>> posting = new ArrayList<>(postingList.entrySet());
		posting.removeIf(new Predicate<Entry<String, TLongLongMap>>() {
			@Override
			public boolean test(Entry<String, TLongLongMap> entry) {
				return (entry.getValue().size() <= 1);
			}
		});

		System.out.println("Posting List : " + postingList.size());
		System.out.println("Nouvelle Posting List : " + posting.size());*/
	}
}
