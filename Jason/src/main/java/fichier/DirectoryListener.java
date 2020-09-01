package fichier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe abstraite permettant la gestion des event d'un dossier.
 * @author ronan
 *
 */
public abstract class DirectoryListener {

	private File[] fileList;

	private File folder;
	private long interval;
	
	private boolean isStopped = false, exists;
	
	public DirectoryListener(File folder, long interval) {
		this.folder = folder;
		this.interval = interval;
		this.start();
	}
	
	public void start() {
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!isStopped) {
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
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		th.setDaemon(true);
		th.start();
	}

	protected abstract void onCreateFile(List<File> liste);
	protected abstract void onDeleteDirectory();
	protected abstract void onCreateDirectory();
	
}
