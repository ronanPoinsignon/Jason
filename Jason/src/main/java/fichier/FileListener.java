package fichier;

import java.io.File;
import java.util.TimerTask;

/**
 * Classe abstraite permettant la gestion des events d'un fichier.
 * @author ronan
 *
 */
public abstract class FileListener extends TimerTask {
	
	private File file;
	private long lastModified;
	public boolean exists;
	
	public FileListener(File file) {
		this.file = file;
		exists = file.exists();
		if(exists)
			lastModified = file.lastModified();
	}
	
	public void run() {
		if(!exists) {
			if(file.exists()) {
				exists = true;
				lastModified = file.lastModified();
				FileListener.this.onCreate();
			}
		}
		else {
			if(!file.exists()) {
				exists = false;
				FileListener.this.onDelete();
			}
			else {
				if(lastModified != file.lastModified()){
					lastModified = file.lastModified();
					FileListener.this.onChange();
				}
			}
		}
	}
	
	protected abstract void onCreate();
	protected abstract void onChange();
	protected abstract void onDelete();
	
}
