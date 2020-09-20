package projet.node;

import exception.NomVideException;

/**
 * Classe racine de l'arborescence.
 * @author ronan
 *
 */
public class Racine extends DossierReel {

	public static String NAME = "root";
	
	private static Racine racine = null;
	
	public static Racine getRacine() throws NomVideException {
		if(racine == null)
			racine = new Racine(NAME);
		return racine;
	}
	
	public static Racine resetRacine() throws NomVideException {
		racine = new Racine(NAME);
		return racine;
	}

	private Racine(String name) throws NomVideException {
		super(name);
	}
	
}
