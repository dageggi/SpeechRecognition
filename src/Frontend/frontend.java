package Frontend;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import DTW_SpeechRecognition.DynamicTimeWarping;
import DTW_SpeechRecognition.SignalPreprocessing;

public class frontend {

	   	//Initialisierung einer HashMap -> einem Wort (String) wird ein Merkmalvektor zugeordnet (double[])
		private HashMap<String, double[]> wordBank = new HashMap<String, double[]>();
		GUI gui = new GUI();
		SignalPreprocessing sig_pre = new SignalPreprocessing();
		DynamicTimeWarping dtw = new DynamicTimeWarping(3,3,3,2);
		
		public byte[] microphone() {
		
		//Festlegen der Paramater für die Sprachaufnahme (wichtig: 8000.0F bedeutet dass das Signal
		//mit 8kHz abgetastet wird
		AudioFormat audioFormat = new AudioFormat(8000.0F, 16, 1, true, false);
		TargetDataLine targetDataLine = null;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
		
		try { 
			targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
			targetDataLine.open(audioFormat);
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}
		
		ByteArrayOutputStream out  = new ByteArrayOutputStream();
		int numBytesRead;
		byte[] data = new byte[targetDataLine.getBufferSize() / 5];
		targetDataLine.start();

		//Aufnahme solange bis stopped false -> bedeutet Aufnahmeende
		while (gui.recordStopped) {
		   numBytesRead =  targetDataLine.read(data, 0, data.length);
		   out.write(data, 0, numBytesRead);
		
		}    
		
		
		return out.toByteArray();
		
	}

		public double[][] getFeatureVector(byte[] values) {
			
			double[][] frames = sig_pre.getFrames(double values, 256, 0);
			double[][] feature_vectors = new double[frames.length][24];
			int frame = 0;
			
			for (double[] x : frames) {
				double[] fft = sig_pre.fft(x, true)[2];
				double[] features = new double[128];
				for(int y = 127; y <= 0; y--) {
					features[y] = fft[y];
				}
				features = sig_pre.melFilterBank(8000, features, 15, 200, 3700);
				features = sig_pre.log(features, 1);
				features = sig_pre.dct(features);
				
				double[] features2;
				for(int y = 1; y < 13; y++) {
					features2[y-1] = features[y];
				}
				features2 = sig_pre.channelEqualization(features2, 2);
				double[] deltafeatures = sig_pre.deltaFeatures(features2, 2);
				
				
				for (int x = 0; x <= )
				
				frame++;;
				
			}
			
			return byte
		}
		
}
