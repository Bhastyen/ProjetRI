import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Models {

	public static List<Entry<Document, Float>> CosineScore (
			String query,
			HashMap<String, Map<Long, Long>> postingList,
			HashMap<Long, Map<String, Long>> postingListPerDoc,
			List<Document> documents,
			String param) 
	{
		float weight = 0; float score; int key;
		query= query.toLowerCase();
		String[] arrQuery = query.split(" ");

		List<Entry<Document, Float>> results;   // document, score  resultats du cosine score
		List<Entry<Long, Long>> docs;  // <id du document , tf>
		HashMap<Long, Float> docIdScore = new HashMap<>();  // DocId , score par document
		Map<String, Float> otherParameters = new HashMap<String, Float>();
		
		if (Character.toString(param.charAt(2)).equals("2")){
			otherParameters.put("k",normalization.k(param));
			otherParameters.put("b",normalization.b(param));
			otherParameters.put("ave_len",normalization.ave_len(postingListPerDoc));
		}
		
		if (Character.toString(param.charAt(2)).equals("u")){
			otherParameters.put("slope",normalization.slope(param));
			otherParameters.put("pivot",normalization.pivot(postingListPerDoc));
			otherParameters.put("ave_len",normalization.ave_len(postingListPerDoc));		
		}
		
		//Pour chaque mot de la requete
		for(String wordQuery : arrQuery) {

			if(Main.STEMMING) {
				wordQuery = Stemming.stemTerm(wordQuery);
			}

			// Pour chaque pair : nombre occurence / IdDocument du terme wordQuery
			if (postingList.get(wordQuery) != null) {

				//				weight = normalization.W(param, wordQuery, 0, postingListPerDoc, postingList, true);

				docs = new ArrayList<>(postingList.get(wordQuery).entrySet());
				for (Entry<Long, Long> pair : docs) {   // <id du document , tf>

					if (docIdScore.get(pair.getKey()) == null)   // ajout de l entree dans le dico s il n existe pas
						docIdScore.put(pair.getKey(), 0f);

					score = docIdScore.get(pair.getKey());
					score += normalization.W(param, wordQuery, pair.getKey(), postingListPerDoc, postingList, otherParameters, false);
					//					score *= weight;

					docIdScore.put(pair.getKey(), score);
//									break;
				}
//							break;
			}
		}

		// gerer le recouvrement
		results = getDocumentAndScore(docIdScore, documents);
		
		// TODO Ajouter les fils d'un element
		// TODO Parcourir l'arbre du document et ressortir les elements les plus pertinents
		
		
		// trie les documents par rapport a leur score calcule pour la requete
		Collections.sort(results, new Comparator<Entry<Document, Float>>() {

			@Override
			public int compare(Entry<Document, Float> e1, Entry<Document, Float> e2) {
				if (e1.getValue() > e2.getValue())
					return -1;

				if (e1.getValue() < e2.getValue())
					return 1;

				return 0;
			}

		});
		

		return results;   // renvoie la liste des resultats tries
	}
	
	private static List<Entry<Document, Float>> getDocumentAndScore(Map<Long, Float> docIdScore, List<Document> docs){
		List<Entry<Document, Float>> results;
		ArrayList<Entry<Long, Float>> list; // DocId , score par document
		HashMap<Long, Document> docsMap = new HashMap<>();  // DocId , document  sert a cherhcer efficacement un document par rapport a l'id
		
		// TODO chercher le doc de maniere efficace a partir de l'id
		docsMap = Document.getDocumentsHashMap(docs); 

		// TODO construire la liste List<Entry<Document, Float>>
		list = new ArrayList<>(docIdScore.entrySet());   // on recupere le score et l'id du document
		results = new ArrayList<>();
		for (int i = 0; i < list.size(); i ++) {
			results.add(new AbstractMap.SimpleEntry<Document, Float>(docsMap.get(list.get(i).getKey()), list.get(i).getValue()));
		}
		
		return results;
	}
	
	private static List<Entry<Document, Float>> RemoveCover(List<Entry<Document, Float>> docs){
		
		
		return docs;
	}
	
}
