package Frontend;

import java.awt.EventQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JSeparator;
import javax.swing.JList;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

import javax.swing.JSpinner;
import javax.swing.JTextField;

public class GUI {

	public static frontend front;
	private JFrame frmSpracherkennung;
	
	private JTextField textfield_word;
	public boolean recordStopped = true;
		
	//Main-Methode: Einstiegspunkt des Programms
	public static void main(String[] args) {
		
		front = new frontend();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//GUI generieren
					GUI window = new GUI();
					window.frmSpracherkennung.setVisible(true);
				} catch (Exception e) {
					//mögliche anfallende Fehler abfangen (try-catch), Fehler ausgeben (printStackTrace())
					e.printStackTrace();
				}
			}
		});
	}

	//GUI initialiseren
	public GUI() {
		initialize();
	}

	//schriftliche Programmierung der festgelegten GUI aus dem Designer
	private void initialize() {
		
		frmSpracherkennung = new JFrame();
		frmSpracherkennung.setTitle("Spracherkennung");
		frmSpracherkennung.setBounds(100, 100, 546, 397);
		frmSpracherkennung.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSpracherkennung.getContentPane().setLayout(null);
		
		final JButton btnMicRecognition = new JButton("Mikrofonaufnahme starten");
		//wird aufgerufen wenn der Mikrofonaufnahme-Button gedrückt wird
		btnMicRecognition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				btnMicRecognition.setText("TEST!!!!");
				if (recordStopped == true) {
					btnMicRecognition.setText("Aufnahme stoppen");
					recordStopped = false;
					
				} else {
					btnMicRecognition.setText("Mikrofonaufnahme starten");
					recordStopped = true;
				}
				
			}
		});
		btnMicRecognition.setBounds(119, 15, 188, 23);
		frmSpracherkennung.getContentPane().add(btnMicRecognition);
		
		
		JLabel lblErkennung = new JLabel("Erkennung");
		lblErkennung.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblErkennung.setBounds(22, 15, 97, 19);
		frmSpracherkennung.getContentPane().add(lblErkennung);
		
		final JButton btnWAVRecognition = new JButton("Sprachaufnahme einf\u00FCgen");
		btnWAVRecognition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnWAVRecognition.setText("TEST");
			}
		});
		btnWAVRecognition.setBounds(331, 15, 188, 23);
		frmSpracherkennung.getContentPane().add(btnWAVRecognition);
		
		JLabel lblstatusspeech = new JLabel("kein Sprachsignal");
		lblstatusspeech.setFont(new Font("Tahoma", Font.ITALIC, 11));
		lblstatusspeech.setBounds(151, 53, 97, 14);
		frmSpracherkennung.getContentPane().add(lblstatusspeech);
		
		JButton btnStart = new JButton("Erkennung starten");
	
		btnStart.setBounds(331, 49, 188, 23);
		frmSpracherkennung.getContentPane().add(btnStart);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 112, 572, 2);
		frmSpracherkennung.getContentPane().add(separator);
		
		JLabel lblwordtext = new JLabel("Erkanntes Wort:");
		lblwordtext.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblwordtext.setBounds(119, 87, 142, 14);
		frmSpracherkennung.getContentPane().add(lblwordtext);
		
		JLabel lblword = new JLabel("New label");
		lblword.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblword.setBounds(227, 88, 132, 14);
		frmSpracherkennung.getContentPane().add(lblword);
		
		JLabel lblZeit = new JLabel("Zeit:");
		lblZeit.setBounds(377, 87, 33, 14);
		frmSpracherkennung.getContentPane().add(lblZeit);
		
		JLabel lbltime = new JLabel("New label");
		lbltime.setBounds(420, 87, 46, 14);
		frmSpracherkennung.getContentPane().add(lbltime);
		
		JLabel lblWordbank = new JLabel("Wortschatz");
		lblWordbank.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblWordbank.setBounds(22, 125, 108, 23);
		frmSpracherkennung.getContentPane().add(lblWordbank);
		
		JButton btnMicWordbank = new JButton("Mikrofonaufnahme starten");
		btnMicWordbank.setVerticalAlignment(SwingConstants.TOP);
		btnMicWordbank.setHorizontalAlignment(SwingConstants.TRAILING);
		btnMicWordbank.setBounds(119, 127, 188, 23);
		frmSpracherkennung.getContentPane().add(btnMicWordbank);
		
		JButton btnWAVWordbank = new JButton("Sprachaufnahme einf\u00FCgen");
		btnWAVWordbank.setVerticalAlignment(SwingConstants.TOP);
		btnWAVWordbank.setHorizontalAlignment(SwingConstants.TRAILING);
		btnWAVWordbank.setBounds(331, 125, 188, 23);
		frmSpracherkennung.getContentPane().add(btnWAVWordbank);
		
		JButton btnAddWordbank = new JButton("Zum Wortschatz hinzuf\u00FCgen");
		btnAddWordbank.setVerticalAlignment(SwingConstants.TOP);
		btnAddWordbank.setHorizontalAlignment(SwingConstants.TRAILING);
		btnAddWordbank.setBounds(181, 190, 178, 23);
		frmSpracherkennung.getContentPane().add(btnAddWordbank);
		
		JLabel lblStatusSoundWordbank = new JLabel("kein Sprachsignal");
		lblStatusSoundWordbank.setFont(new Font("Tahoma", Font.ITALIC, 11));
		lblStatusSoundWordbank.setBounds(129, 165, 132, 14);
		frmSpracherkennung.getContentPane().add(lblStatusSoundWordbank);
		
		JSpinner wordbank_list = new JSpinner();
		wordbank_list.setBounds(22, 225, 444, 30);
		frmSpracherkennung.getContentPane().add(wordbank_list);
		
		textfield_word = new JTextField();
		textfield_word.setBounds(331, 161, 132, 20);
		frmSpracherkennung.getContentPane().add(textfield_word);
		textfield_word.setColumns(10);
		
		JLabel lblWord = new JLabel("Wort: ");
		lblWord.setBounds(288, 165, 46, 14);
		frmSpracherkennung.getContentPane().add(lblWord);
		
		JButton btnWordDelete = new JButton("L\u00F6schen");
		btnWordDelete.setBounds(193, 266, 134, 23);
		frmSpracherkennung.getContentPane().add(btnWordDelete);
		
		JButton btnLoadWordbank = new JButton("Wortschatz laden");
		btnLoadWordbank.setBounds(10, 310, 150, 23);
		frmSpracherkennung.getContentPane().add(btnLoadWordbank);
		
		JButton btnDeleteWordbank = new JButton("Wortschatz l\u00F6schen");
		
		btnDeleteWordbank.setBounds(369, 310, 150, 23);
		frmSpracherkennung.getContentPane().add(btnDeleteWordbank);
		
		JButton btnSaveWordbank = new JButton("Wortschatz speichern");
		
		btnSaveWordbank.setBounds(181, 310, 164, 23);
		frmSpracherkennung.getContentPane().add(btnSaveWordbank);
		
		
		
		
		
	}
	}
