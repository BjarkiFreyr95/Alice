package allie;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;



public class GetDestination {
	JFileChooser fileChooser = new JFileChooser();
	boolean hasChosenDestination = false;
	String destinationPath;
	public void selectDestination(Preferences prefs) throws Exception{
		if (Files.exists(Paths.get(prefs.get("destination", "")))) {
			fileChooser.setCurrentDirectory(new File(prefs.get("destination", "")));
		}
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			destinationPath = fileChooser.getSelectedFile().getAbsolutePath();
			hasChosenDestination = true;
			
		}
		else {
			if (Files.exists(Paths.get(prefs.get("destination", "")))) {
				hasChosenDestination = true;
			}
			
		}
	}
	public String getDestination() {
		return destinationPath;
	}
	public void setDestination(String dest) {
		destinationPath = dest;
		hasChosenDestination = true;
	}
}
