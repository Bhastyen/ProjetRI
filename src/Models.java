import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



public class Models {

	public static List<Entry<Integer, Float>> CosineScore (
			String query,
			HashMap<String, Map<Integer, Long>> postingList,
			HashMap<Integer, Map<String, Long>> postingListPerDoc,
			List<Document> documents,
			String param) 
	{
		float weight = 0; float score; int key;
		query= query.toLowerCase();
		String[] arrQuery = query.split(" ");

		List<Entry<Integer, Long>> docs;  // <id du document , tf>
		HashMap<Integer, Float> docIdScore = new HashMap<>();  // DocId , score par document
		ArrayList<Entry<Integer, Float>> list; // DocId , score par document (pour recup plus tard)

		//Pour chaque mot de la requete
		for(String wordQuery : arrQuery) {
			
			if(Main.STEMMING) {
				wordQuery = Stemming.stemTerm(wordQuery);  // Pour le stemming TODO si on veut mettre optionnel variable Globale , if 1
			}
			
			// Pour chaque pair : nombre occurence / IdDocument du terme wordQuery
			if (postingList.get(wordQuery) != null) {
				
				weight = normalization.W(param, wordQuery, 0, postingListPerDoc, postingList, true);
				
				docs = new ArrayList<>(postingList.get(wordQuery).entrySet());
				for (Entry<Integer, Long> pair : docs) {   // <id du document , tf>
					
					if (docIdScore.get(pair.getKey()) == null)   // ajout de l entree dans le dico s il n existe pas
						docIdScore.put(pair.getKey(), 0f);
					
					score = docIdScore.get(pair.getKey());
					score += normalization.W(param, wordQuery, pair.getKey(), postingListPerDoc, postingList, false) * weight;
					docIdScore.put(pair.getKey(), score);
					
				}
			}
		}

		// trie les documents par rapport a leur score calcule pour la requete
		list = new ArrayList<>(docIdScore.entrySet());
		Collections.sort(list, new Comparator<Entry<Integer, Float>>() {

			@Override
			public int compare(Entry<Integer, Float> e1, Entry<Integer, Float> e2) {
				if (e1.getValue() > e2.getValue())
					return -1;

				if (e1.getValue() < e2.getValue())
					return 1;

				return 0;
			}

		});

		return list;   // renvoie la liste des resultats tries
	}
}
