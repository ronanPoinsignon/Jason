package exception;

public class NomVideException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String message = "Le nom ne peut être vide";
	
	public NomVideException() {
		super(message);
	}

}
