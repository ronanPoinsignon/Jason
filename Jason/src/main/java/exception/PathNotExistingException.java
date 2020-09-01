package exception;

public class PathNotExistingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String message = "Le path donné n'existe pas ou a été supprimé";
	
	public PathNotExistingException() {
		super(message);
	}
}
