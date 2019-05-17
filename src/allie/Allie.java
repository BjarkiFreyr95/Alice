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
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import java.awt.Label;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;



public class Allie {

	private JFrame frmAlicehz;
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
				    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				        if ("Nimbus".equals(info.getName())) {
				            UIManager.setLookAndFeel(info.getClassName());
				            break;
				        }
				    }
				} catch (Exception e) {
				    // If Nimbus is not available, you can set the GUI to another look and feel.
				}
				try {
					Allie window = new Allie();
					window.frmAlicehz.setVisible(true);
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
		
		
		
		frmAlicehz = new JFrame();
		frmAlicehz.setIconImage(Toolkit.getDefaultToolkit().getImage(Allie.class.getResource("/allie/AllieIcon80x80.png")));
		frmAlicehz.getContentPane().setBackground(UIManager.getColor("Button.background"));
		frmAlicehz.setBounds(100, 100, 554, 492);
		frmAlicehz.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frmAlicehz.setTitle("Alice - 40Hz isochronic mp3 converter");
		

		Label convertLabel = new Label("");
		convertLabel.setForeground(Color.BLACK);
		convertLabel.setBounds(301, 178, 226, 31);
		frmAlicehz.getContentPane().add(convertLabel);
		
		
		DefaultListModel<String> model = new DefaultListModel<String>();
		frmAlicehz.getContentPane().setLayout(null);
		JList<String> list = new JList<String>(model);
		list.setBackground(new Color(245, 245, 245));
		list.setBounds(10, 127, 260, 300);
		frmAlicehz.getContentPane().add(list);
		
		Label currentSongStatusLabel = new Label("");
		currentSongStatusLabel.setForeground(Color.BLACK);
		currentSongStatusLabel.setBounds(301, 269, 226, 24);
		frmAlicehz.getContentPane().add(currentSongStatusLabel);
		
		Label estimatedTimeLabel = new Label("Estimated Time: 00:00:00");
		estimatedTimeLabel.setForeground(Color.BLACK);
		estimatedTimeLabel.setBounds(301, 343, 183, 24);
		frmAlicehz.getContentPane().add(estimatedTimeLabel);
		
		JLabel fileSelectorLabel = new JLabel("No files selected");
		fileSelectorLabel.setForeground(Color.BLACK);
		fileSelectorLabel.setBounds(147, 61, 158, 24);
		frmAlicehz.getContentPane().add(fileSelectorLabel);
		
		JLabel destinationSelectorLabel = new JLabel("No destination selected");
		destinationSelectorLabel.setForeground(Color.BLACK);
		destinationSelectorLabel.setBounds(147, 92, 165, 24);
		frmAlicehz.getContentPane().add(destinationSelectorLabel);
		
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
		btnSelectFiles.setForeground(new Color(0, 0, 0));
		btnSelectFiles.setBackground(new Color(204, 204, 204));
		btnSelectFiles.setBounds(10, 62, 127, 23);
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
		frmAlicehz.getContentPane().add(btnSelectFiles);
		
		JButton btnChangeDestination = new JButton("Destination");
		btnChangeDestination.setForeground(new Color(0, 0, 0));
		btnChangeDestination.setBackground(new Color(204, 204, 204));
		btnChangeDestination.setBounds(10, 93, 127, 23);
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
		frmAlicehz.getContentPane().add(btnChangeDestination);
		
		
		UIManager.getLookAndFeelDefaults().put("nimbusOrange", UIManager.getLookAndFeelDefaults().get("nimbusFocus"));
		
		DefaultBoundedRangeModel currentSongModel = new DefaultBoundedRangeModel();
		JProgressBar currSongProgress = new JProgressBar(currentSongModel);
		currSongProgress.setForeground(new Color(51, 255, 51));
		currSongProgress.setBackground(new Color(255, 255, 255));
		currSongProgress.setBounds(301, 225, 183, 38);
		currentSongModel.setMinimum(0);
		currentSongModel.setMaximum(100);
		frmAlicehz.getContentPane().add(currSongProgress);
		
		DefaultBoundedRangeModel totalProgressModel = new DefaultBoundedRangeModel();
		JProgressBar totalProgress = new JProgressBar(totalProgressModel);
		totalProgress.setBounds(301, 299, 183, 38);
		totalProgress.setMinimum(0);
		totalProgress.setMaximum(100);
		frmAlicehz.getContentPane().add(totalProgress);
		
		

		Label currSongPercentage = new Label("");
		currSongPercentage.setForeground(Color.BLACK);
		currSongPercentage.setBounds(490, 225, 47, 38);
		frmAlicehz.getContentPane().add(currSongPercentage);
		
		Label totalSongPercentage = new Label("");
		totalSongPercentage.setForeground(Color.BLACK);
		totalSongPercentage.setBounds(490, 300, 47, 38);
		frmAlicehz.getContentPane().add(totalSongPercentage);
		
		
		JButton btnConvert = new JButton("Convert!");
		btnConvert.setForeground(new Color(0, 0, 0));
		btnConvert.setBackground(new Color(204, 204, 204));
		btnConvert.setBounds(301, 131, 183, 35);
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
		frmAlicehz.getContentPane().add(btnConvert);
		lblNewLabel_1.setEnabled(false);
		lblNewLabel_1.setBounds(272, 485, 359, 31);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		frmAlicehz.getContentPane().add(lblNewLabel_1);
		
		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(new ImageIcon(this.getClass().getResource("AllieIcon80x80.png")));
		imageLabel.setBounds(353, 11, 90, 105);
		frmAlicehz.getContentPane().add(imageLabel);
		
	}
}
