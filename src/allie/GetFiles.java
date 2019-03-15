package allie;

import javax.swing.JFileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GetFiles {
	File[] musicFiles;
	JFileChooser fileChooser = new JFileChooser();
	FileFilter filter = new FileNameExtensionFilter("MP3 File", "mp3");
	boolean hasChosenFiles = false;
	
	public void selectFilePicked(Preferences prefs) throws Exception{
		if (Files.exists(Paths.get(prefs.get("destination", "")))) {
			fileChooser.setCurrentDirectory(new File(prefs.get("fileDirectory", "")));
		}
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileFilter(filter);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			prefs.put("fileDirectory", fileChooser.getSelectedFile().getAbsolutePath());
			musicFiles = fileChooser.getSelectedFiles();
			hasChosenFiles = true;
			
		}
		else {
			hasChosenFiles = false;
			if (musicFiles != null) {
				if (musicFiles.length != 0) {
					hasChosenFiles = true;
				}
			}
			
		}
	}
	public File[] getMusicFilesArray() {
		return musicFiles;
	}
	public boolean hasSelectedMusicFiles() {
		return hasChosenFiles;
	}
	public String[] getMusicNames() {
		
		if (musicFiles != null) {
			String[] nameList = new String[musicFiles.length];
			for (int i = 0; i < musicFiles.length; i++) {
				nameList[i] = musicFiles[i].getName();
			}
			return nameList;
		}
		return null;
	}
	
	public void clearMusicFilesArray() {
		musicFiles = null;
		hasChosenFiles = false;
		System.out.println("cleared music file array");
		
	}
	
}
