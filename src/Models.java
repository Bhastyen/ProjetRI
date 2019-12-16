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
		List<Long> revelantId;
		HashMap<Long, Float> docIdScore = new HashMap<>();  // DocId , score par document
		HashMap<Long, Document> docsMap = new HashMap<>();  // DocId , document  sert a cherhcer efficacement un document par rapport a l'id
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

				// weight = normalization.W(param, wordQuery, 0, postingListPerDoc, postingList, true);

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
		// recupere un index des documents par id
		docsMap = Document.getDocumentsHashMap(documents);
		
		// Parcourir l'arbre des documents et ressortir tous les elements les plus pertinents
		revelantId = removeAllCover(documents, docIdScore, docsMap);
		
		// construit une liste de paire document et score
		results = getDocumentAndScore(revelantId, docsMap, docIdScore);
		
		// trie les documents/elements par rapport a leur score calcule pour la requete
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
	
	// associe a chaque document pertinent son score
	private static List<Entry<Document, Float>> getDocumentAndScore(List<Long> revelantDocId, Map<Long, Document> docsMap, Map<Long, Float> docIdScore){
		List<Entry<Document, Float>> results;
		long id; float score;

		results = new ArrayList<>();
		for (int i = 0; i < revelantDocId.size(); i ++) {
			id = revelantDocId.get(i);
			score = docIdScore.get(id) == null? 0f : docIdScore.get(id);   // verifie si le score existe et mets 0 sinon (document et query sans point commun)
			
			results.add(new AbstractMap.SimpleEntry<Document, Float>(docsMap.get(id), score));
		}
		
		return results;
	}
	
	// gere le recouvrement de chaque document et renvoie tous les elements pertinents
	private static List<Long> removeAllCover(List<Document> docs, Map<Long, Float> idDocScore, Map<Long, Document> idDocDoc){
		List<Long> results = new ArrayList<Long>();
		List<Long> resultsFils = new ArrayList<Long>();
		
		for (int i = 0; i < docs.size(); i++) {
			//System.err.println("ID  fils  " + Long.toString(docs.get(i).getId()) + "  doc + 1  " + Long.toString(docs.get(i).getIdDoc()) + "1");
			
			if (Long.toString(docs.get(i).getId()).equals(Long.toString(docs.get(i).getIdDoc()) + "1")) {  // si il s'agit de l'element racine "article"
				System.out.println("Document : " + docs.get(i).getIdDoc());
				resultsFils = removeCover(docs.get(i), idDocScore, idDocDoc);
				results.addAll(resultsFils);   // recupere les elements interessants du document
				
				//System.err.println("Nombre resultat : " + resultsFils.size());
			}
		}
		
		return results;
	}

	// gere le recouvrement pour un document et selectionne les elements avec le meilleur score
	private static List<Long> removeCover(Document doc, Map<Long, Float> idDocScore, Map<Long, Document> idDocDoc){
		List<Long> revelantIds = new ArrayList<Long>();
		List<Long> revelantIdFils;
		float scoreParent = 0, scoreFils = 0;
		boolean father = true;
		float difference = 0;
		
		
		if (doc.getIdFils().size() > 0) {
			for (int i = 0; i < doc.getIdFils().size(); i++) {
				revelantIdFils = removeCover(idDocDoc.get(doc.getIdFils().get(i)), idDocScore, idDocDoc);
				for (int j = 0; j < revelantIdFils.size(); j++) {
					scoreParent = idDocScore.get(doc.getId()) == null? 0f : idDocScore.get(doc.getId());
					scoreFils = idDocScore.get(doc.getId()) == null? 0f : idDocScore.get(doc.getId());
					
					difference = ((scoreFils + 0.00001f) / (scoreParent + 0.00001f) * 100f) - 100f;
					
					//System.err.println("Difference : " + scoreFils + "   " + scoreParent + "   " + difference);
					if (difference < -10) {     // si le fils est 10% plus bas que le pere
						//System.out.println("Ok inf");
						revelantIds.add(revelantIdFils.get(j));
						father = false;
					} else if (difference > 10) {    // si le fils est 10% plus haut que le pere
						//System.out.println("Ok sup");
						revelantIds.add(revelantIdFils.get(j));
						father = false;
					} else {      // si le fils est entre [-10%, 10%]
						//System.out.println("Ok equal");
					}
				}
			}
		}
		
		
		if (father) {
			revelantIds.add(doc.getId());
		}
		
		return revelantIds;
	}
	
}
