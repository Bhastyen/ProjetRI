import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



public class Models {

	public static List<Entry<Integer, Float>> CosineScore (String query, HashMap<String, Map<Integer, Long>> postingList, HashMap<Integer, Map<String, Long>> postingListPerDoc, List<Document> documents, String param) {
		float weight = 0, idf = 0; float score; int key;
		String[] arrQuery = query.split(" ");

		List<Entry<Integer, Long>> docs;
		HashMap<Integer, Float> docIdScore = new HashMap<>();
		ArrayList<Entry<Integer, Float>> list;

		//Pour chaque mot de la requete
		for(String wordQuery : arrQuery) {
			
			//Pour chaque pair : nombre occurence / IdDocument du terme wordQuery
			if (postingList.get(wordQuery) != null) {
				idf = (float) Math.log10(documents.size() / (float) postingList.get(wordQuery).size());
				weight = idf;    //TODO F(tf(t,q) x G(df(t)) selon les fonctions SMART  normalization(param)
				docs = new ArrayList<>(postingList.get(wordQuery).entrySet());
			
				for (Entry<Integer, Long> pair : docs) {
					//System.out.println("idf : " + idf + " tf : " + postingListPerDoc.get(pair.getKey()).size());
					if (docIdScore.get(pair.getKey()) == null)   // ajout de l entree dans le dico s il n existe pas
						docIdScore.put(pair.getKey(), 0f);
					score = docIdScore.get(pair.getKey());
					score += postingListPerDoc.get(pair.getKey()).size() * idf;    //TODO F(tf(t,d) x G(df(t)) selon les fonctions SMART  normalization(param)
					docIdScore.put(pair.getKey(), score);
				}
			}
		}

		for (Document doc : documents) {  // normalisation du score de chaque document
			key = doc.getIdDoc();
			
			if (docIdScore.containsKey(key)) {
				score = docIdScore.get(key);

				score = score / doc.getLength();  // Scores[d] by Lengths[d]
				docIdScore.put(key, score);
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
