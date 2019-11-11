
public class Document {
	public enum Type {XML, BRUT} ;
	
	private int idDoc;
	private String stringDocument;
	
	
	public Document(int idDoc, String stringDocument) {
		super();
		this.idDoc = idDoc;
		this.stringDocument = stringDocument.toLowerCase();
	}
	
	
	
	public int getIdDoc() {
		return idDoc;
	}
	
	public void setIdDoc(int idDoc) {
		this.idDoc = idDoc;
	}
	
	public String getStringDocument() {
		return stringDocument;
	}
	
	public void setStringDocument(String stringDocument) {
		this.stringDocument = stringDocument;
	}
	
	public int getLength() {
		int c = 0;
		String[] arrString = stringDocument.split(" ");
		
		/*for (int i = 0; i < arrString.length; i++) {
			if (arrString[i] == "" || arrString[i] == " ") {
				c += 1;
			}
		}
		
		System.out.println("Ligne vide doc " + idDoc + " : " + c);*/
		
		return arrString.length;
		
	}
}
