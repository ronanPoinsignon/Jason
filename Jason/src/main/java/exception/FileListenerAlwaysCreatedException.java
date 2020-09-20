package exception;

public class FileListenerAlwaysCreatedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String message = "Le fichier a déjà un listener";
	
	public FileListenerAlwaysCreatedException() {
		super(message);
	}

}
