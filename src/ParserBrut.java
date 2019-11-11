
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ParserBrut {
	private String path;
	
	
	public ParserBrut(String path) {
		this.path = path;
	}
	
	
	public List<Document> parse(){
		List<Document> docs = new ArrayList<Document>();
		File[] fichiers;
		String ligne;
		String contenu = "";
		File d = new File(path);
		int id = 0, j = 0;
		
		if (d.isDirectory()) {

			//System.out.println("C'est ok");
			fichiers = d.listFiles();
			
			for (int i = 0; i < fichiers.length; i++) {
				// ouvre le fichier
				BufferedReader reader;
				try {
					reader = new BufferedReader(new FileReader(fichiers[i]));
				
					// decoupe le fichier en ligne et analyse son contenu
					while ((ligne = reader.readLine()) != null/* && j < 10*/) {
						if (ligne.matches("<doc>.*")) {
							id = searchId(ligne);
							continue;
						}

						if (ligne.matches("</doc>.*")) {
							//System.out.println("Id " + id);
							//System.out.println("Contenu " + contenu);
							docs.add(new Document(id, contenu.replaceAll(" +", " ")));
							j ++;
							contenu = "";
							
							
							continue;
						}

						contenu += ligne.replaceAll("[!,;:^']", " ").replace('\n', ' ') + " ";
					}
							
					// ferme le buffer
					reader.close();
				} catch (FileNotFoundException e) {
					System.out.println("Probleme lors de la lecture du fichier : " + e.getMessage());
				} catch (IOException e) {
					System.out.println("Probleme lors de la lecture du fichier : " + e.getMessage());
				}
			}
		}
		
		return docs;
	}
	
	
	private int searchId(String ligne) {
		int i = 0, l = 0, b = 0;
		
		while (!Character.isDigit(ligne.charAt(i))) {
			i ++;
		}
		
		b = i;
		while (Character.isDigit(ligne.charAt(i))) {
			i ++;
			l += 1;
		}
		
		return Integer.parseInt(ligne.substring(b, b + l));
	}
}
