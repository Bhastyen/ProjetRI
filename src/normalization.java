
public class normalization {
	
	public static double n(int tf, double df, int docLength) {
		double weigth = 0;
		
		weigth = tf*df/docLength;
		
		return weigth;
		
	}
	
//	public static double c(int tf, double df, int docLength) {
//		double w = 0;
//		
//		w = tf*df/docLength;
//		w = w/
//		
//		
//		return w;
//		
//	}
//	
//	
//	public static double s(int tf, double df, int docLength) {
//		double weigth = 0;
//		
//		weigth = tf*df/docLength;
//		
//		return weigth;
//		
//	}
//	
//	
//	public static double u(int tf, double df, int docLength) {
//		double weigth = 0;
//		
//		weigth = tf*df/docLength;
//		
//		return weigth;
//		
//	}
//	
	
	
}
