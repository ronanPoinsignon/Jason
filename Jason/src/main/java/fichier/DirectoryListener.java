package fichier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

/**
 * Classe abstraite permettant la gestion des event d'un dossier.
 * @author ronan
 *
 */
public abstract class DirectoryListener extends TimerTask {

	private File[] fileList;

	private File folder;
	
	private boolean exists;
	
	public DirectoryListener(File folder) {
		this.folder = folder;
	}
	
	public void run() {
		if(!exists) {
			if(folder.exists()) {
				exists = true;
				fileList = folder.listFiles();
				DirectoryListener.this.onCreateDirectory();
			}
		}
		else {
			if(!folder.exists()) {
				exists = false;
				DirectoryListener.this.onDeleteDirectory();
			}
			else {
				if(!Arrays.equals(fileList,folder.listFiles())){
					List<File> listeFichiers = new ArrayList<>(Arrays.asList(folder.listFiles()));
					List<File> liste = new ArrayList<>(listeFichiers);
					liste.removeAll(Arrays.asList(fileList));
					fileList = folder.listFiles();
					DirectoryListener.this.onCreateFile(liste);
				}
			}
		}
	}

	protected abstract void onCreateFile(List<File> liste);
	protected abstract void onDeleteDirectory();
	protected abstract void onCreateDirectory();
	
}
