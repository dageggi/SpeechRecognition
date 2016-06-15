package DTW_SpeechRecognition;

public class SignalPreprocessing {
	
	//Methode zum Einteilen von Abtastwerten in Frames/Fenster
	//Parameter: 1. Array mit Abtastwerten, 2. Anzahl der Werte pro Fenster, 3. Wert mit dem fehlende Werte aufgefüllt werden
	public double[][] getFrames (double[] sampling_values, int frame_length, int fill_value) {
		
		//Prüfen ob range ungerade ist
		if ((frame_length % 2) == 1) {
			throw new IllegalArgumentException("frame_length must be an even number!");
		}
		
		//Array für Fenster
	     double[][] frames = new double[(int) (Math.ceil((double) (sampling_values.length) / ((double) (frame_length) / 2)) - 1)][frame_length];
		//Definitionen: y -> Index des Wertes pro Frame, z -> Index des Frames
		int y = 0;
		int z = 0;
		
		//Schleife für alle übergebenen Abtastwerte
		for (int x = 0; x < sampling_values.length; x++, y++) {
			
			//Prüfung ob ein Frame noch Platz hat 
			if (y < frame_length) {
				//Eintragen des Abtastwertes in Frame-Array
				frames[z][y] = sampling_values[x];
			} else {
				//wenn Frame voll: 
				y = -1; //Frame-Index beginnt neu 
				x = x - (int) (((double) (frame_length) / 2 )+1); //x veringern -> Fenster überlappen sich
				z++; //nächster Frame
			}
			
		}
		
		//wenn alle Abtastwerte eingeordnet, Prüfung ob letztes Frame voll ist
		if (y < frame_length && y != 0) {
			//wenn Platz übrig, mit fill_value auffüllen
			for (int y2 = y; y2 < frame_length; y2++) { 
				frames[z][y2] = fill_value;
			}
		}
		
		//Frame-Arrays in einem Array zurückgeben
		return frames;
	}

	//Methode für Diskrete Fourier Transformation
	public double[][] dft(double[] values, boolean hamming) {
		
		//Array für berechnete Fourier-Koeffizienten (Realteile, Imaginärteile, Betragsquadrate)
		double[][] dft_values = new double[3][values.length];
		double term;
		double imaginary;
		double real;
		double hamming_value;
		
		//Schleife für alle k Fourier-Koeffizienten
		for (int k = 0; k < values.length; k++) {
			
			//Real- und Imaginärteil für neuen Koeffizienten zurücksetzten
			real = 0;
			imaginary = 0;
			
			//Schleife für alle Werte pro Koeffizient
			for (int n = 0; n < values.length; n++) {
				
				if(hamming == true) {
					hamming_value = 0.54 - 0.46 * Math.cos( (double) (2 * Math.PI * n) / (values.length - 1));
				} else {hamming_value = 1;}
				
				//mathematische Berechnung des Koeffizienten, siehe Facharbeit
				term = (2 * Math.PI * k * n) / values.length;
				real += Math.cos(term) * values[n] * hamming_value;
				imaginary += Math.sin(term) * values[n] * hamming_value;
				
				
			}
			
			//Betragsquadrat zur Umwandlung in real Zahl und Mulitpliaktion mit 1/n als Bestandteil der Fourier-Transformation
			dft_values[1][k] = (1/ (double) (values.length)) * imaginary;
			dft_values[0][k] = (1/ (double) (values.length)) * real;
			
			dft_values[2][k] =  Math.pow((1/ (double) (values.length)) * real, 2) + Math.pow((1/ (double) (values.length)) * imaginary, 2);
				
		}
		
		//Rückgabe des Arrays mit allen Fourier-Koeffizienten
		return dft_values;
	}

	//Methode für Fast Fourier Transformation
	public double[][] fft(double[] values, boolean hamming) {

		//Prüfen ob die übergebene Array-Länge eine Potenz von 2 ist, wenn nicht: Fehler
		double is_power = Math.log(values.length) / Math.log(2);
		if (((is_power - (int) is_power) != 0) && (values.length != 2)) {
			throw new IllegalArgumentException("Window-Length is not a power of 2!");
		} 
		
		//Arrays für Werte der FFT 
		double[][] fft_values = new double[3][values.length];
		double[][] fft_values2 = new double[3][values.length];
		double[][][] fft_arrays = {fft_values, fft_values2};

		//Eintragen der übergebenen Werte in Reihenfolge der Bit-Umkehr
		int size = (int) (Math.log((double)values.length)/ Math.log((double)2) + 1);
		double hamming_value;
		for(int x = 0; x < values.length; x++) {
			
			if(hamming == true) {
				hamming_value = 0.54 - 0.46 * Math.cos( (double) (2 * Math.PI * x) / (values.length - 1));
			} else {hamming_value = 1;}
			
			fft_arrays[0][0][x] = hamming_value * values[binaryToInt(changeBits(intToBinary(x, size)))];
			fft_arrays[0][1][x] = 0.0;
		}
		
		//Schleife für alle einzelnen Stufen der FFT
		int array = 1;
		for (int x = 1, k = 0; k < is_power; k++, x = x * 2) {
			int z = 1;
			//Wechsel des zu beschreibenden Arrays
			array = Math.abs(array-1);
			//Schleife für alle Koeffizienten
			for (int y = 0; y < values.length-1; z++) { 
				//Berechnung der Fourier-Koeffizienten
				if (z <= x) {
					 fft_arrays[Math.abs(array-1)][0][y] = fft_combine_real(fft_arrays[array][0][y], fft_arrays[array][1][y], 
							 	fft_arrays[array][0][y+x], fft_arrays[array][1][y+x], y, 2*x, false);
					 fft_arrays[Math.abs(array-1)][1][y] = fft_combine_imag(fft_arrays[array][0][y], fft_arrays[array][1][y], 
							 	fft_arrays[array][0][y+x], fft_arrays[array][1][y+x], y, 2*x, false);
					
					 fft_arrays[Math.abs(array-1)][0][y+x] = fft_combine_real(fft_arrays[array][0][y], fft_arrays[array][1][y], 
							 	fft_arrays[array][0][y+x], fft_arrays[array][1][y+x], y, 2*x, true);
					 fft_arrays[Math.abs(array-1)][1][y+x] = fft_combine_imag(fft_arrays[array][0][y], fft_arrays[array][1][y], 
							 	fft_arrays[array][0][y+x], fft_arrays[array][1][y+x], y, 2*x, true);
					 
				} 

				//Erhöhung y, bzw. Überspringen einzelner y zur korrekten Zuordnung der Werte bei der FFT
				if (z == x) {
					z = 0;
					y += 2 + k;
				} else { y++; }
				
			}
		}		
			
		//Multiplikation aller Werte mit 1/n und Berechnung der Betragsquadrate
		for (int x = 0; x < values.length; x++) {
 			fft_arrays[Math.abs(array-1)][0][x] = 1 / (double) (values.length) * fft_arrays[Math.abs(array-1)][0][x];
 			fft_arrays[Math.abs(array-1)][1][x] = 1 / (double) (values.length) * fft_arrays[Math.abs(array-1)][1][x];
			fft_arrays[Math.abs(array-1)][2][x] = Math.pow(fft_arrays[array][0][x],2) + Math.pow(fft_arrays[array][1][x],2);
			
		}
		
		//Rückgabe des Arrays mit Fourier-Koeffizienten und Betragsquadraten
		return fft_arrays[Math.abs(array-1)];
	}
	
	//Berechnung der Real-Teile entsprechend der Formeln der FFT
	private double fft_combine_real(double a1, double a2, double b1, double b2, double y, double n, boolean is_up) {
		double real;
		double term = -2 * Math.PI * y / n;
		
		if (is_up == false ) {
			real = a1 + b1*Math.cos(term) - b2*Math.sin(term);} else
		{real = a1 - (b1*Math.cos(term) - b2*Math.sin(term)); }
		
		return real;
	}

	//Berechnung der Imaginär-Teile entsprechend der Formeln der FFT
	private double fft_combine_imag(double a1, double a2, double b1, double b2, double y, double n, boolean is_up) {
		double imag;
		double term = -2 * Math.PI * y / n;
		
		if(is_up == true) {
			imag = a2 - b1 * Math.sin(term) - b2*Math.cos(term); } else {
				imag = a2 + b1*Math.sin(term) + b2*Math.cos(term);
			}
		
		return imag;																																	
	}
	
	//Umwandlung eines Integers in Dualzahl-Array
	private int[] intToBinary(int x, int size) {

		int[] binary = new int[size-1];
		int number = x;
		
		for (int i = 1; i <= size-1; i++) {
			if ((number % 2) == 1) {
				number = (number -1) / 2;
				binary[size-i-1] = 1;
			} else {
				number = number / 2;
				binary[size-i-1] = 0;
			}
			
		}
		
		return binary;
	}

	//Umkehr der Bits in einem Bit-Array
	private int[] changeBits(int[] bit_array) {

		int bitBuffer[] = new int[bit_array.length];
		for (int x = 1; x <= bit_array.length; x++) {
			bitBuffer[bit_array.length - x] = bit_array[x-1];
		}
		
		return bitBuffer;
	}
	
	//Umwandlung eines Arrays aus Integers (0 und 1) zu Integer
	private int binaryToInt(int[] bit_array) {
		int number = 0;
		
		for (int x = 1; x <= bit_array.length; x++) {
			number += Math.pow(2, x-1) * bit_array[bit_array.length - x];
		}
		
		return number; 
	}
	
	//Methode zur Berechnung der Diskreten Cosinus Transformation
	public double[] dct(double[] values) {
		
		double[] dct_values = new double[values.length];
		double value = 0;
		
		for (int n = 0; n < values.length; n++) {
			value = 0;
			for (int k = 0; k < values.length; k++) {
				value += values[k] * Math.cos((Math.PI*n*(k+0.5)) / ((double) (values.length)));
			}
			dct_values[n] = value;
		}
		return dct_values;
	}

	//Methode zur Berechnung des Logarithmus
	public double[] log(double[] values, double c) {
		double[] log_values = new double[values.length];
		
		for (int x = 0; x < values.length; x++) {
			log_values[x]= Math.log(values[x] + c);
		}
		
		return log_values;
	}
	
	//Mel zu Hertz
	private double melToHertz(double mel) {
		
		double hertz = 700 * (Math.pow(10, (mel / 2595)) - 1);
		return hertz;
	}
	
	//Hertz zu Mel
	private double hertzToMel(double hertz) {
		
		double mel = 2595 * Math.log10(((hertz / 700) + 1));
		return mel;
		
	}
	
	//Erstellen einer Mel-Filterbank (Anzahl der Dreiecks-Filter, minimale und maximale Frequenz
	public double[] melFilterBank(double samplerate, double[] values, int n, double min_hertz, double max_hertz) {
				
		double[] fo_co = new double[n+2];
		double[] weights = new double[n];
		double weight = 0;
		
		double triangle_size = ((hertzToMel(max_hertz) - hertzToMel(min_hertz)) / (double) (n)) / 2;
		double last_mel = hertzToMel(min_hertz);
		fo_co[0] = Math.floor(((double) (values.length)+ 1) * (min_hertz / samplerate));
		for (int x = 1; x < n+2 ;x++) {
			last_mel += triangle_size;		
			fo_co[x] = Math.floor(((double) (values.length)+ 1) * (melToHertz(last_mel) / samplerate));
		}
		
		double height = 0;
		double slope = 0;
		for (int x = 0, z = 0; x < n; x++) {
			
			weight = 0;
			height = 2 / (fo_co[z+2]  - fo_co[z]);
			slope = height / (fo_co[z+1] - fo_co[z]);
			for (int y = 0; y <= (fo_co[z+1] - fo_co[z]); y++) {
				weight += values[z+y+x] * slope;
			}
			
			z++;
			slope = height / (fo_co[z+1] - fo_co[z]);
			for (int y = 1; y <= (fo_co[z+1] - fo_co[z]); y++) {
				weight += values[z+y+1+x] * slope;
			}
			
			weights[x] = weight;
			
		}
		
		return weights;
	}
	
	//Methode zur Langzeit-Kanalnormierung
	public double[] channelEqualization(double[] values, int range) {
		
		//Prüfen ob range ungerade ist
		if ((range % 2) == 0) {
			throw new IllegalArgumentException("Range must be an odd number!");
		}
		
		//Array für errechnete Werte
		double[] ce_values = new double[values.length];
		
		//setzen der ersten und letzten Werte, die von der Berechnung ausgeschlossen sind
		for (int x = 0; x < ((range-1)/2 ); x++) {
			ce_values[x] = values[x];
			ce_values[values.length - x - 1] = values[values.length - x - 1];
		}
		
		//Berechnung des Faktors und Subtraktion vom gegebenen Wert
		double factor;
		for (int x = (range - 1) /2; x < values.length - (range-1) / 2; x++) {
			factor = 0;
			for (int y = 0; y < range; y++) {
				factor += values[(x-(range-1)/2)+y];
			}
			ce_values[x] = values[x] - (factor * (1 / (double) (range)));
		}
		
		//Rückgabe der berechneten Werte
		return ce_values;
	}

	//Berechnung der zeitlichen Steigung
	public double[] deltaFeatures(double[] values, int range) {
		
		//Prüfen ob range ungerade ist
		if ((range % 2) == 1) {
			throw new IllegalArgumentException("Range must be an even number!");
		}
		
		//Array für berechnete Werte
		double[] deltaFeatures = new double[values.length];
		
		//Ergänzen der nicht von der BErechnung betroffenen Werte
		for (int x = 0; x < range/2; x++) {
			deltaFeatures[x] = 0;
			deltaFeatures[values.length-x] = 0;
		}
		
		//Berechnung der zeitlichen Ableitungen
		double delta;
		for (int x = range/2; x < values.length - range/2; x++) {
			delta = 0;
			for (int y = 0; y < range/2; y++) {
				delta += -x * values[x-y];
				delta += x * values[x+y];
			}
			deltaFeatures[x] = delta;
			
			
		}
		
		//Rückgabe des Ergebnis-Arrays
		return deltaFeatures;
	}
	
}
