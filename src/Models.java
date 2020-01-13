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
		List<Long> revelantId, roots = new ArrayList<>();
		HashMap<Long, Float> docIdScore = new HashMap<>();  // DocId , score par document
		HashMap<Long, Document> docsMap = new HashMap<>();  // DocId , document  sert a cherhcer efficacement un document par rapport a l'id
		Map<String, Float> otherParameters = new HashMap<String, Float>();
		
		// recupere un index des documents par id
		docsMap = Document.getDocumentsHashMap(documents);
		
		if (Main.GRANULARITE == Document.Type_Element.DOCUMENT)
			N = GetNumberDoc(documents);
		else N = docsMap.size();
		
		if (Character.toString(param.charAt(2)).equals("2")){
			otherParameters.put("k",normalization.k(param));
			otherParameters.put("b",normalization.b(param));
			
			if (Main.GRANULARITE == Document.Type_Element.DOCUMENT)
				otherParameters.put("ave_len", normalization.ave_len_doc(docsMap, N));
			else otherParameters.put("ave_len", normalization.ave_len(docsMap, N));
		}
		
		if (Character.toString(param.charAt(2)).equals("u")){
			otherParameters.put("slope",normalization.slope(param));
			otherParameters.put("pivot",normalization.pivot(postingList, N));
			
			if (Main.GRANULARITE == Document.Type_Element.DOCUMENT)
				otherParameters.put("ave_len", normalization.ave_len_doc(docsMap, N));
			else otherParameters.put("ave_len", normalization.ave_len(docsMap, N));
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
					
					// recupere les racines des documents traitees pour gerer ensuite le reouvrement
					if (docsMap.get(doc_id).getType() == Document.Type_Element.ARTICLE 
							&& docsMap.get(doc_id).getCheminDocument().equals("article[1]/") 
							&& !roots.contains(doc_id)) {
						roots.add(doc_id);
					}
					
					// ne calcul un score que si l'element est d'un certain type
					if (docsMap.get(doc_id).getType() == Main.GRANULARITE || Main.GRANULARITE == Document.Type_Element.ELEMENT) {						
						if (!docIdScore.containsKey(doc_id))   // ajout de l entree dans le dico s il n existe pas
							docIdScore.put(doc_id, 0f);
						
						dl = docsMap.get(doc_id).get_length();
						score = docIdScore.get(doc_id);
						score += normalization.W(param, wordQuery, doc_id, dl, N, postingList, otherParameters);
	
						docIdScore.put(doc_id, score);
					}
				}
			}
		}

		// gerer le recouvrement
		
		// Parcourir l'arbre des documents et ressortir tous les elements les plus pertinents
		revelantId = removeAllCover(roots, docIdScore, docsMap);
		
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
		
		List<Entry<Document, Float>> resultGrouped = new ArrayList<>();
		while (results.size() != 0) {
			Long docid = results.get(0).getKey().getIdDoc();
			int c = Main.MAX_ELEMENT;
			int i = 0;
			
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
	private static List<Long> removeAllCover(List<Long> roots, Map<Long, Float> idDocScore, Map<Long, Document> idDocDoc) {
		List<Entry<Long, Float>> idEntries = new ArrayList<Entry<Long, Float>>(idDocScore.entrySet());
		List<Long> results = new ArrayList<Long>();
		List<Long> finalResults = new ArrayList<Long>();
		List<Long> resultsFils = new ArrayList<Long>();
		Document doc;

		for (int i = 0; i < roots.size(); i++) {
			doc = idDocDoc.get(roots.get(i));
			
			resultsFils = removeCover(doc, idDocScore, idDocDoc);
			results.addAll(resultsFils);   // recupere les elements interessants du document
		}
		
		// supprime le chevauchement restant s'il existe
		for (int i = 0; i < results.size(); i ++) {
			doc = idDocDoc.get(results.get(i));
			
			for (int j = 0; j < doc.getIdFils().size(); j ++) {
				if (results.contains(doc.getIdFils().get(j))) {
					System.out.println("Overlap : " + doc.getId() + "  " + doc.getIdFils().get(j));
				}
			}
		}
		
		System.out.println("Combien de resultat : " + finalResults.size() + 
				"  combien de resultat non finaux : " + results.size() + 
				"  combien d'article : " + roots.size() + 
				"  combien de score : " + idDocScore.size());

		return results;
	}

	// gere le recouvrement pour un document et selectionne les elements avec le meilleur score
	private static List<Long> removeCover(Document doc, Map<Long, Float> idDocScore, Map<Long, Document> idDocDoc) {
		List<Long> revelantIds = new ArrayList<Long>();
		List<Long> revelantIdFils;
		float scoreParent = 0, scoreFils = 0, moyScoreFils = 0;
		boolean father = true;
		boolean fils = true;
		float difference = 0;

		if (doc.getIdFils().size() > 0) {
			
			for (int i = 0; i < doc.getIdFils().size(); i++) {
				fils = true;
				moyScoreFils = 0;
				
				revelantIdFils = removeCover(idDocDoc.get(doc.getIdFils().get(i)), idDocScore, idDocDoc);
				
				for (int j = 0; j < revelantIdFils.size(); j++) {
					if (!idDocScore.containsKey(revelantIdFils.get(j)))
						scoreFils = 0;
					else scoreFils = idDocScore.get(revelantIdFils.get(j));

					moyScoreFils += scoreFils / revelantIdFils.size();
				}
								
				if (!idDocScore.containsKey(doc.getIdFils().get(i)))
					scoreParent = 0;
				else scoreParent = idDocScore.get(doc.getIdFils().get(i));
				
				difference = ((moyScoreFils + 0.00001f) / (scoreParent + 0.00001f) * 100f) - 100f;

				if (difference > 10) { // si le fils est plus de 10% plus haut que le pere
					for (int j = 0; j < revelantIdFils.size(); j ++) {
						if (!idDocScore.containsKey(revelantIdFils.get(j)))
							scoreFils = 0;
						else scoreFils = idDocScore.get(revelantIdFils.get(j));
						
						if ((idDocDoc.get(revelantIdFils.get(j)).getType() == Main.GRANULARITE || Main.GRANULARITE == Document.Type_Element.ELEMENT)
								&& scoreFils != 0) {
							revelantIds.add(revelantIdFils.get(j));
						}
					}
					
					fils = false;
				}

				if (fils) {
					if (!idDocScore.containsKey(doc.getIdFils().get(i)))
						scoreParent = 0;
					else scoreParent = idDocScore.get(doc.getIdFils().get(i));
	
					if ((idDocDoc.get(doc.getIdFils().get(i)).getType() == Main.GRANULARITE || Main.GRANULARITE == Document.Type_Element.ELEMENT)
							&& scoreParent != 0) {
						revelantIds.add(doc.getIdFils().get(i));
					}
				}
			}
			
			// calcul moyenne pour comparaison entre revelant id et le parent
			for (int i = 0; i < revelantIds.size(); i++) {
				moyScoreFils = 0;
				if (!idDocScore.containsKey(revelantIds.get(i)))
					scoreFils = 0;
				else scoreFils = idDocScore.get(revelantIds.get(i));

				moyScoreFils += scoreFils / revelantIds.size();
			}
			
			// calcul le pourcentage de difference entre les fils pertinant et le pere : resultat entre [-100, 100]
			if (!idDocScore.containsKey(doc.getId()))
				scoreParent = 0;
			else scoreParent = idDocScore.get(doc.getId());
			
			difference = ((moyScoreFils + 0.00001f) / (scoreParent + 0.00001f) * 100f) - 100f;
			
			// choisi entre les fils ou le parent
			if (difference > 10) { // si le fils est plus de 10% plus haut que le pere
				father = false;
			}else {
				revelantIds = new ArrayList<>();
				father = true;
			}
		}

		if (father) {
			if (!idDocScore.containsKey(doc.getId()))
				scoreParent = 0;
			else scoreParent = idDocScore.get(doc.getId());
			
			if ((idDocDoc.get(doc.getId()).getType() == Main.GRANULARITE || Main.GRANULARITE == Document.Type_Element.ELEMENT) && scoreParent != 0)
				revelantIds.add(doc.getId());
		}

		return revelantIds;
	}

	// calcul nombre Document
	public static int GetNumberDoc(List<Document> docs) {
		int compteur = 0;
		
		for (int i = 0; i < docs.size(); i++) {
			if (docs.get(i).getType() == Document.Type_Element.ARTICLE && docs.get(i).getCheminDocument().equals("article[1]/")) {
				compteur += 1;
			}
		}
		
		return compteur;
	}
}
