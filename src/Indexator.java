import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Indexator {
	private static String test1 = "Toto va manger le chat de le Toto";
	private static String test2 = "le chat veut être un chat mais il a un probleme";
	private static String test3 = "Chante Toto et voit comme le ciel est beau";

	private static List<String> listDoc;

	public static void main(String args[]) {

		List<Pair> listPairOccDocid;
		Boolean isInDico = false;
		Integer indexInDico = 0;
		listDoc = new ArrayList<String>();
		listDoc.add(test1);
		listDoc.add(test2);
		listDoc.add(test3);

		listPairOccDocid = new ArrayList<>();

		int docid = 100;
		HashMap<String, List<Pair>> postingList = new HashMap<String, List<Pair>>();
		HashMap<Integer, List<Pair>> postingListPerDoc = new HashMap<Integer, List<Pair>>();
		List<Pair> dicoPerDoc = new ArrayList<>();
		List<Pair> postingListPairs = new ArrayList<>();
		List<String> dico = new ArrayList<>();
		List<String> wordInPostingListPerDoc = new ArrayList<>();
		int nbOcc = 0;

		// Pour tous les documents
		for (String doc : listDoc) {
			// On split le string selon les espaces
			String[] splitString = doc.split(" ");
			// On récupère le doc Id
			// docid = doc.getdocid();

			// Pour chque mot
			for (String word : splitString) {
				isInDico = false;
				// A enlever
				if (word == splitString[0]) {
					docid = docid + 1;
					//
				}
				// Si le dico est vide, c'est le premier mot on l'ajoute direct
				if (dicoPerDoc.size() == 0) {
					dicoPerDoc.add(new Pair(1, word));
				} else {
					// Look if the word is already in the dico
					for (int i = 0; i < dicoPerDoc.size(); i++) {
						// Si on trouve le mot dans le dico
						if (dicoPerDoc.get(i).getValue().equals(word)) {
							// On set le bool a true et on note son index
							isInDico = true;
							indexInDico = i;
						}
					}
					// If the term is not in the dico
					if (!isInDico) {
						// Add the term to the dico
						dicoPerDoc.add(new Pair(1, word));
					} else {
						// On augmente le nombre d'occurence du terme
						dicoPerDoc.get(indexInDico).setNumOcc((Integer) dicoPerDoc.get(indexInDico).getNumOcc() + 1);
					}
				}

			}
			// Put the document and its list of term/occurency in the HashMap
			List<Pair> clone = new ArrayList<Pair>(dicoPerDoc);
			postingListPerDoc.put(docid, clone);

			// Loop pour faire la posting liste
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
					for (int i = 0; i < dicoPerDoc.size(); i++) {
						// Si on trouve le mot dans le dico
						if (dicoPerDoc.get(i).getValue().equals(word)) {
							nbOcc = (Integer) dicoPerDoc.get(i).getNumOcc();
							break;
						}
					}

					// Si le mot est dejà dans la posting liste alors on recup la liste et on ajoute
					// la nouvelle pair
					if (postingList.containsKey(word)) {
						postingListPairs = postingList.get(word);
					}
					postingListPairs.add(new Pair(nbOcc, docid));
					// On ajoute la list updated ou nouvelle
					List<Pair> clone2 = new ArrayList<Pair>(postingListPairs);
					postingList.put(word, clone2);

					// On vide la liste des pairs
					postingListPairs.clear();
				}
			}
			// On vide tout hop hop hop
			wordInPostingListPerDoc.clear();
			dicoPerDoc.clear();
		}
		System.out.println(postingListPerDoc);
		System.out.println(postingList);
	}
}
