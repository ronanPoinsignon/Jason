package fichier;

import java.io.File;

import javax.xml.bind.Unmarshaller.Listener;

/**
 * Classe abstraite permettant la gestion des events d'un fichier.
 * @author ronan
 *
 */
public abstract class FileListener extends Listener {

	private File file;
	private long lastModified;
	private long interval;
	public boolean isStopped = false, exists;
	
	public FileListener(File file, long interval) {
		this.file = file;
		this.interval = interval;
		exists = file.exists();
		if(exists)
			lastModified = file.lastModified();
		this.start();
	}
	
	public void start() {
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!isStopped) {
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
	
	protected abstract void onCreate();
	protected abstract void onChange();
	protected abstract void onDelete();
	
}
