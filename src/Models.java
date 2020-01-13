import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TLongLongProcedure;

public class Models {

	public static List<Entry<Document, Float>> CosineScore (
			String query,
			THashMap<String, TLongLongMap> postingList,
			List<Document> documents,
			String param) 
	{
		float score; long doc_id; long dl; int N;
		query= query.toLowerCase();
		String[] arrQuery = query.split(" ");

		List<Entry<Document, Float>> results;   // document, score  resultats du cosine score
		List<Entry<Long, Long>> docs;  // <id du document , tf>
		List<Long> revelantId;
		HashMap<Long, Float> docIdScore = new HashMap<>();  // DocId , score par document
		HashMap<Long, Document> docsMap = new HashMap<>();  // DocId , document  sert a cherhcer efficacement un document par rapport a l'id
		Map<String, Object> otherParameters = new HashMap<String, Object>();
		// recupere un index des documents par id
		docsMap = Document.getDocumentsHashMap(documents);
		N = docsMap.size();
		
		if (Character.toString(param.charAt(2)).equals("2")){
			otherParameters.put("k",normalization.k(param));
			otherParameters.put("b",normalization.b(param));
			if (Main.ROBERTSON == true) {
				otherParameters.put("ave_len",normalization.ave_len_f(docsMap, N, normalization.alphas(param)));
			} else {
				otherParameters.put("ave_len",normalization.ave_len(docsMap, N));
			}
			otherParameters.put("docMap", docsMap);
			otherParameters.put("alphas", normalization.alphas(param));
		}
		
		if (Character.toString(param.charAt(2)).equals("u")){
			otherParameters.put("slope",normalization.slope(param));
			otherParameters.put("pivot",normalization.pivot(postingList, N));
			otherParameters.put("ave_len",normalization.ave_len(docsMap, N));
		}
		
		// Pour chaque mot de la requete
		for(String wordQuery : arrQuery) {

			if(Main.STEMMING) {
				wordQuery = Stemming.stemTerm(wordQuery);
			}

			// Pour chaque pair : nombre occurence / IdDocument du terme wordQuery
			if (postingList.contains(wordQuery)) {

				//docs = new ArrayList<>(postingList.get(wordQuery).entrySet());
				TLongLongIterator ite = postingList.get(wordQuery).iterator();
				while(ite.hasNext()) {
					ite.advance();
					
					doc_id = ite.key();
					
					if (docsMap.get(doc_id).getType() == Main.GRANULARITE && docsMap.get(doc_id).getCheminDocument().equals("article[1]/")) {						
						if (!docIdScore.containsKey(doc_id))   // ajout de l entree dans le dico s il n existe pas
							docIdScore.put(doc_id, 0f);
						
						dl = docsMap.get(doc_id).get_length();
						score = docIdScore.get(doc_id);
						score += normalization.W(param, wordQuery, doc_id, dl, N, postingList, otherParameters);
	
						docIdScore.put(doc_id, score);
					}
				}
				
				/*for (Entry<Long, Long> pair : docs) {   // <id du document , tf>
					doc_id = pair.getKey();

					if (docIdScore.get(doc_id) == null)   // ajout de l entree dans le dico s il n existe pas
						docIdScore.put(doc_id, 0f);
					
					dl = docsMap.get(doc_id).get_length();
					score = docIdScore.get(doc_id);
					score += normalization.W(param, wordQuery, doc_id, dl, N, postingList, otherParameters);

					docIdScore.put(pair.getKey(), score);
				}*/
			}
		}

		// gerer le recouvrement
		
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
		
		/*int i = 0;
		while (i < results.size()) {
			if (results.get(i).getKey().getIdFils().size() > 0) {
				results.remove(i);
			} else {
				i ++;
			}
		}*/
		
		List<Entry<Document, Float>> resultGrouped = new ArrayList<>();
		while (results.size() != 0) {
			Long docid = results.get(0).getKey().getIdDoc();
			int c = Main.MAX_ELEMENT;
			int i = 0;
			
			//List<Entry<Document, Float>> res = new ArrayList<Entry<Document, Float>>(results);
			while (i < results.size()) {
				if (results.get(i).getKey().getIdDoc() == docid && c > 0) {
					resultGrouped.add(results.get(i));
					c--;
					results.remove(i);
				} else if (results.get(i).getKey().getIdDoc() == docid && c == 0) {
					results.remove(i);
				} else {
					i++;
				}

				// System.out.println("Size result: " + results.size());
			}
		}
		
		return resultGrouped;   // renvoie la liste des resultats tries
	}

	// associe a chaque document pertinent son score
	private static List<Entry<Document, Float>> getDocumentAndScore(List<Long> revelantDocId,
			Map<Long, Document> docsMap, Map<Long, Float> docIdScore) {
		List<Entry<Document, Float>> results;
		long id;
		float score;

		results = new ArrayList<>();
		for (int i = 0; i < revelantDocId.size(); i++) {
			id = revelantDocId.get(i);
			score = docIdScore.get(id) == null ? 0f : docIdScore.get(id); // verifie si le score existe et mets 0 sinon
																			// (document et query sans point commun)

			results.add(new AbstractMap.SimpleEntry<Document, Float>(docsMap.get(id), score));
		}

		return results;
	}

	// gere le recouvrement de chaque document et renvoie tous les elements
	// pertinents
	private static List<Long> removeAllCover(List<Document> docs, Map<Long, Float> idDocScore, Map<Long, Document> idDocDoc) {
		List<Entry<Long, Float>> idEntries = new ArrayList<Entry<Long, Float>>(idDocScore.entrySet());
		List<Long> results = new ArrayList<Long>();
		List<Long> resultsFils = new ArrayList<Long>();

		for (int i = 0; i < idEntries.size(); i++) {
			
			// System.err.println("ID fils " + Long.toString(docs.get(i).getId()) + " doc + 1 " + Long.toString(docs.get(i).getIdDoc()) + "1");
			
//			 if (docs.get(i).getType() == Document.Type_Element.ARTICLE && !docs.get(i).getCheminDocument().matches("/*/*/*")) { // si il s'agit
				 // de l'element racine "article"
				 
//				 System.out.println("Type " + docs.get(i).getType() + "  chemin  " + docs.get(i).getCheminDocument());
			
//				 resultsFils = removeCover(docs.get(i), idDocScore, idDocDoc); 
//				 results.addAll(resultsFils); // recupere les elements interessants du document
			 
//			 	System.err.println("Nombre resultat : " + resultsFils.size());
//			 }
			 

			if (!Float.isNaN(idEntries.get(i).getValue())) {
				results.add(idEntries.get(i).getKey()); // recupere les elements interessants du document
			}
		}

		return results;
	}

	// gere le recouvrement pour un document et selectionne les elements avec le meilleur score
	private static List<Long> removeCover(Document doc, Map<Long, Float> idDocScore, Map<Long, Document> idDocDoc) {
		List<Long> revelantIds = new ArrayList<Long>();
		List<Long> revelantIdFils;
		float scoreParent = 0, scoreFils = 0;
		boolean father = true;
		float difference = 0;

		//System.out.println("Fils  " + doc.getIdFils());
		if (doc.getIdFils().size() > 0) {
			for (int i = 0; i < doc.getIdFils().size(); i++) {
				revelantIdFils = removeCover(idDocDoc.get(doc.getIdFils().get(i)), idDocScore, idDocDoc);
				for (int j = 0; j < revelantIdFils.size(); j++) {
					scoreParent = !idDocScore.containsKey(doc.getId()) ? 0f : idDocScore.get(doc.getId());
					scoreFils = !idDocScore.containsKey(revelantIdFils.get(j)) ? 0f : idDocScore.get(revelantIdFils.get(j));

					difference = ((scoreFils + 0.00001f) / (scoreParent + 0.00001f) * 100f) - 100f;

					// System.err.println("Difference : " + scoreFils + " " + scoreParent + " " + difference);
					if (difference < -10) { // si le fils est 10% plus bas que le pere
						//System.out.println("Ok inf");
						revelantIds.add(revelantIdFils.get(j));
						father = false;
					} else if (difference > 10) { // si le fils est 10% plus haut que le pere
						//System.out.println("Ok sup");
						revelantIds.add(revelantIdFils.get(j));
						father = false;
					} else { // si le fils est entre [-10%, 10%]
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
