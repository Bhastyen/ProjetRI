import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;



public class Models {
	
	
	public static List<Entry<Integer, Float>> CosineScore (String query, HashMap<String, List<Pair>> postingList, List<Document> documents) {
		float weight = 0; float score;
		int key;
		String[] arrQuery = query.split(" ");
		
		HashMap<Integer, Float> docIdScore = new HashMap<>();
		ArrayList<Entry<Integer, Float>> list;
		
		//Pour chaque mot de la requete
		for(String wordQuery : arrQuery) {
			weight = 0; //TODO F(tf(t,q) x G(df(t)) selon les fonctions SMART
			
			//Pour chaque pair : nombre occurence / IdDocument du terme wordQuery
			for (Pair<Integer, Integer> pair : postingList.get(wordQuery)) {
				if (docIdScore.get(pair.getValue()) == null)   // ajout de l entree dans le dico s il n existe pas
					docIdScore.put(pair.getValue(), 0f);
				score = docIdScore.get(pair.getValue());   // pair.getvalue() = idDoc
				score += 0f;    //TODO F(tf(t,d) x G(df(t)) selon les fonctions SMART
				docIdScore.put(pair.getValue(), score);
			}
		}
		
		for (Document doc : documents) {  // normalisation du score de chaque document
			key = doc.getIdDoc();
			score = docIdScore.get(key);
			
			score = score / doc.getLength();  // Scores[d] by Lengths[d]
			docIdScore.put(key, score);
		}
		
		// trie les documents par rapport a leur score calcule pour la requete
		list = new ArrayList<>(docIdScore.entrySet());
		Collections.sort(list, new Comparator<Entry<Integer, Float>>() {

			@Override
			public int compare(Entry<Integer, Float> e1, Entry<Integer, Float> e2) {
				if (e1.getValue() > e2.getValue())
					return 1;
				
				if (e1.getValue() < e2.getValue())
					return -1;
				
				return 0;
			}
			
		});
		
		return list;   // renvoie la liste des resultats tries
	}
}
