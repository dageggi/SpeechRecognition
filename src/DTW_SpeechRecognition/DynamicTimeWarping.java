package DTW_SpeechRecognition;

public class DynamicTimeWarping {

	private double Pnext = 0;  //Time Distortion Penaltie Variablen
	private double Ploop = 0;
	private double Pskip = 0;
	private int PruningRange = 0; //Pruning Spannweite
	private int array; //aktuell zu beschreibende Differenzen-Matrix
	private double[][] difference_matrixes; //Array für beide Abstands-Matrizen
	private double[] difference_matrix1; //Abstands-Matrizen
	private double[] difference_matrix2;
	private double node_difference; //Abstand der beiden Merkmalvektor
	private double node_min;
	private double min;
	
	
	public DynamicTimeWarping(double pnext, double ploop, double pskip, int pruning_range) { //Kosntruktur der DTW Klasse, wird zur Initialisierung aufgerufen
		this.Pnext = pnext; //schräg
		this.Ploop = ploop; //hoch
		this.Pskip = pskip;	//rechts
		this.PruningRange = pruning_range;
		
		difference_matrix1 = new double[2*PruningRange + 1];
		difference_matrix2 = new double[2*PruningRange + 1];
		
		difference_matrixes = new double[][]{difference_matrix1, difference_matrix2};
		
	}
	
	public void setTimeDistortionPenalties(int pnext, int ploop, int pskip) { //Methode zum Setzen der Time Distortion Penalties
		this.Pnext = pnext;
		this.Ploop = ploop;
		this.Pskip = pskip;			
	}
	
	public double getDifference(double[] feature_vector_one, double[] feature_vector_two) {
		
		array = 1; 
		for (int y = 0; y < 2*PruningRange+1; y++) 
		{ difference_matrix2[y] = 10000; }
		difference_matrix2[PruningRange] = 0 - Pskip;
		
		for (int i = 0, aj = 0 - PruningRange; i < feature_vector_one.length; i++) {
			array = Math.abs(array - 1);
			aj = i - PruningRange;
			if (aj + 2*PruningRange < feature_vector_two.length) {
				
				for (int j = 0; j <= 2*PruningRange ; j++, aj++) {
					
					if (aj >= 0) { node_difference =  Math.abs(feature_vector_one[i]-feature_vector_two[aj]);} 
						else node_difference = 10000;
					
					if (j > 0) { node_min = node_difference + Pskip + difference_matrixes[array][j-1];};
					
					if (j < 2*PruningRange) {
					min = node_difference + Ploop + difference_matrixes[Math.abs(array-1)][j+1];
					if (min < node_min ) {node_min = min;} else if ( j == 0) {node_min = min; };}
					
					if (j > 0 && j < 2*PruningRange  ) {
					min = node_difference + Pnext + difference_matrixes[Math.abs(array-1)][j];
					if (min < node_min) {node_min = min;} }
					
					difference_matrixes[array][j] = node_min;
					
				}
			}
				else {
					aj = feature_vector_two.length - 2*PruningRange -1;
					for (int j = 0; j <= 2*PruningRange ; j++, aj++) {
						
						if (aj >= 0) { node_difference =  Math.abs(feature_vector_one[i]-feature_vector_two[aj]);} 
							else node_difference = 10000;
						
						if (j > 0) { node_min = node_difference + Pskip + difference_matrixes[array][j-1];};
						
						if (j < 2*PruningRange) {
						min = node_difference + Ploop + difference_matrixes[Math.abs(array-1)][j];
						if (min < node_min) {node_min = min;} else if ( j == 0) {node_min = min; };}
						
						if (j > 0 && j < 2*PruningRange +1 ) {
						min = node_difference + Pnext + difference_matrixes[Math.abs(array-1)][j-1];
						if (min < node_min) {node_min = min;}; }
						
						difference_matrixes[array][j] = node_min;
						
					}
					
				}
				
			}
			
		return difference_matrixes[array][2*PruningRange];
		
		
	}
			
}
