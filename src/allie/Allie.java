package allie;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

import java.awt.Label;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;



/*
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JSlider;
*/

public class Allie {

	private JFrame frame;
	private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
	private final JLabel lblNewLabel_1 = new JLabel("Unverified Experimental Software");
	//private int increasingVolumeInt;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Allie window = new Allie();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Allie() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		GetFiles gf = new GetFiles();
		GetDestination gd = new GetDestination();
		//increasingVolumeInt = 0;
		
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 667, 516);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setTitle("Alice");
		

		Label convertLabel = new Label("");
		convertLabel.setBounds(31, 183, 226, 31);
		frame.getContentPane().add(convertLabel);
		
		
		
		/*
		 * 
		JLabel discouragedLabel = new JLabel("");
		discouragedLabel.setBounds(32, 159, 226, 35);
		frame.getContentPane().add(discouragedLabel);
		JLabel extraVolumeStrength = new JLabel("0%");
		extraVolumeStrength.setEnabled(false);
		extraVolumeStrength.setBounds(184, 114, 74, 37);
		frame.getContentPane().add(extraVolumeStrength);
		JSlider volumeSlider = new JSlider();
		volumeSlider.setEnabled(false);
		volumeSlider.setValue(0);
		volumeSlider.setBounds(32, 122, 136, 26);
		volumeSlider.setMinimum(100);
		volumeSlider.setMaximum(200);
		
		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				increasingVolumeInt = volumeSlider.getValue();
				extraVolumeStrength.setText(increasingVolumeInt + "%");
				if (increasingVolumeInt > 0) {
					//discouragedLabel.setText("May need 2 tries for each song");
				}
				else {
					discouragedLabel.setText("");
				}
			}
		});
		
		frame.getContentPane().add(volumeSlider);
		*/
		
		DefaultListModel<String> model = new DefaultListModel<String>();
		frame.getContentPane().setLayout(null);
		JList<String> list = new JList<String>(model);
		list.setBounds(290, 159, 260, 300);
		frame.getContentPane().add(list);
		
		Label currentSongStatusLabel = new Label("");
		currentSongStatusLabel.setBounds(20, 269, 226, 24);
		frame.getContentPane().add(currentSongStatusLabel);
		
		Label estimatedTimeLabel = new Label("Estimated Time: 00:00:00");
		estimatedTimeLabel.setBounds(20, 354, 183, 24);
		frame.getContentPane().add(estimatedTimeLabel);
		
		JLabel fileSelectorLabel = new JLabel("No files selected");
		fileSelectorLabel.setBounds(428, 124, 158, 24);
		frame.getContentPane().add(fileSelectorLabel);
		
		JLabel destinationSelectorLabel = new JLabel("No destination selected");
		destinationSelectorLabel.setBounds(428, 91, 165, 24);
		frame.getContentPane().add(destinationSelectorLabel);
		
		if (prefs.get("destination", "") != "") {
			if (Files.exists(Paths.get(prefs.get("destination", "")))) {
				gd.setDestination(prefs.get("destination", ""));
				int lIndex = gd.getDestination().lastIndexOf('\\');
				if (lIndex < 0) {
					lIndex = gd.getDestination().lastIndexOf('/');
				}
				if (lIndex > gd.getDestination().length() - 1) {
					destinationSelectorLabel.setText(gd.getDestination());
				}
				else if (lIndex > -1) {
					destinationSelectorLabel.setText("Folder: " + gd.getDestination().substring(lIndex + 1, gd.getDestination().length()));
				}
				else {
					destinationSelectorLabel.setText(gd.getDestination());
				}
			}
			else {
				System.out.println("path does not exist");
			}
		}
		
		
		JButton btnSelectFiles = new JButton("Select Files");
		btnSelectFiles.setBounds(291, 125, 127, 23);
		btnSelectFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileSelectorLabel.setText("No files selected");
				//OpenFile of = new OpenFile()
				try {
					gf.selectFilePicked(prefs);
					String[] temp = gf.getMusicNames();
					model.clear();
					if (temp != null) {
						fileSelectorLabel.setText(temp.length + " files selected");
						for (int i = 0; i < temp.length; i++) {
							model.addElement(temp[i]);
						}
					}
					else {
						fileSelectorLabel.setText("No files selected");
					}
					
				} catch (Exception e) {
					System.out.println("caught " + e);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(btnSelectFiles);
		
		JButton btnChangeDestination = new JButton("Destination");
		btnChangeDestination.setBounds(291, 92, 127, 23);
		btnChangeDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					gd.selectDestination(prefs);
					if (gd.hasChosenDestination) {
						prefs.put("destination", gd.getDestination());
						int lIndex = gd.getDestination().lastIndexOf('\\');
						if (lIndex < 0) {
							lIndex = gd.getDestination().lastIndexOf('/');
						}
						if (lIndex > gd.getDestination().length() - 1) {
							destinationSelectorLabel.setText(gd.getDestination());
						}
						else if (lIndex > -1) {
							destinationSelectorLabel.setText("Folder: " + gd.getDestination().substring(lIndex + 1, gd.getDestination().length()));
						}
						else {
							destinationSelectorLabel.setText(gd.getDestination());
						}
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(btnChangeDestination);
		
		
		DefaultBoundedRangeModel currentSongModel = new DefaultBoundedRangeModel();
		JProgressBar currSongProgress = new JProgressBar(currentSongModel);
		currSongProgress.setBounds(20, 225, 183, 38);
		currentSongModel.setMinimum(0);
		currentSongModel.setMaximum(100);
		frame.getContentPane().add(currSongProgress);
		
		DefaultBoundedRangeModel totalProgressModel = new DefaultBoundedRangeModel();
		JProgressBar totalProgress = new JProgressBar(totalProgressModel);
		totalProgress.setBounds(20, 310, 183, 38);
		totalProgress.setMinimum(0);
		totalProgress.setMaximum(100);
		frame.getContentPane().add(totalProgress);
		

		Label currSongPercentage = new Label("");
		currSongPercentage.setBounds(210, 225, 47, 38);
		frame.getContentPane().add(currSongPercentage);
		
		Label totalSongPercentage = new Label("");
		totalSongPercentage.setBounds(210, 310, 47, 38);
		frame.getContentPane().add(totalSongPercentage);
		
		
		JButton btnConvert = new JButton("Convert!");
		btnConvert.setBounds(24, 137, 165, 35);
		btnConvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				convertLabel.setText("");
				System.out.println("gd.hasChosenDestination && gf.hasChosenFiles = " + gd.hasChosenDestination + " " + gf.hasChosenFiles);
				if (gd.hasChosenDestination && gf.hasChosenFiles) {
					
					fileSelectorLabel.setText("No files selected");
					Converter converter = new Converter(gd.getDestination(), gf.musicFiles, currentSongModel, totalProgressModel, currSongPercentage, totalSongPercentage, gf, 
							currentSongStatusLabel, estimatedTimeLabel, model, 100, convertLabel);
					converter.convertFiles();
					System.out.println("finished converting");
				}
				else if (!gd.hasChosenDestination) {
					convertLabel.setText("You need to select destination");
					System.out.println("No destination chosen");
				}
				else {
					convertLabel.setText("You need to select files");
					System.out.println("Has not chosen any files");
				}
				
			}
		});
		frame.getContentPane().add(btnConvert);		
		
		JLabel lblNewLabel = new JLabel("Isochronic mp3 converter (40hz) - Adaptive Gain");
		lblNewLabel.setBounds(199, 20, 497, 48);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		frame.getContentPane().add(lblNewLabel);
		lblNewLabel_1.setEnabled(false);
		lblNewLabel_1.setBounds(272, 485, 359, 31);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		frame.getContentPane().add(lblNewLabel_1);
		
		/*
		JTextArea tDisclaimer = new JTextArea();
		JScrollPane sp = new JScrollPane(tDisclaimer);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setSize(254, 300);
		sp.setLocation(594, 156);
		tDisclaimer.setBounds(546, 56, 209, 180);
		tDisclaimer.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tDisclaimer.setText("Disclaimer  \n==========\n\nAllie \nVersion 0.9.2 Beta\n\nLast updated: August 28, 2018\n\nAllie is a software for converting audio files to be Isochronic at 40Hz.  This type of modulation may have different effects on people that listen to the output file. The use of the files is at the listener's own risk. No claims are made for any particular effect from listening to the converted sounds, benefits or risks.\n\nIt is also the responsibility of the user to respect any applicable copyrights for the music being converted.\n\nIn no event shall the author of this software be liable for any special, direct, indirect, consequential, incidental damages or any damages whatsoever, whether in an action of contract, negligence or other tort, arising out of or in connection with the use of the software or its output. The author reserves the right to make additions, deletions, or modification to the software at any time without prior notice. \n\n\n");
		tDisclaimer.setLineWrap(true);
		tDisclaimer.setWrapStyleWord(true);
		tDisclaimer.setCaretPosition(0);
		frame.getContentPane().add(sp); 
		*/
		
		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(new ImageIcon(this.getClass().getResource("AllieIcon80x80.png")));
		imageLabel.setBounds(42, 21, 115, 105);
		frame.getContentPane().add(imageLabel);
		
		
		/*
		
		JLabel lblVolumeIncreaser = new JLabel("Volume Increaser");
		lblVolumeIncreaser.setEnabled(false);
		lblVolumeIncreaser.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblVolumeIncreaser.setBounds(32, 80, 158, 38);
		frame.getContentPane().add(lblVolumeIncreaser);
		
		
		*/
		
		
		
		
		
		
		
	}
}
