package projet.node;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import exception.NomVideException;

/**
 * Classe représentant un dossier virtuel.
 * @author ronan
 *
 */
public class DossierReel extends Dossier {
	
	
	public DossierReel(String name) throws NomVideException {
		super(name);
	}

	/**
	 * Ajoute un dossier à l'arborescence.
	 * @param dossier
	 */
	public void ajouter(Dossier dossier) {
		this.getFiles().add(dossier);
		dossier.setDossierParent(this);
	}
	
	/**
	 * Supprime tous les sous-dossiers / fichiers du dossier donné.
	 * @param dossier
	 */
	public static void removeAll(Dossier dossier) {
		List<Dossier> liste = dossier.getFiles();
		for(int i = 0; i < liste.size(); i++) {
			removeAll(liste.get(i));
			dossier.getFiles().removeAll(dossier.getFiles());
		}
	}
	
	/**
	 * Ajoute un dossier ou son arborescence en partant de son path. 
	 * @param path
	 * @throws NomVideException
	 */
	public static void ajouter(String path) throws NomVideException {
		DossierReel dossier = Racine.getRacine();
		DossierReel dossierTemp = null;
		String[] dossiers = path.split("\\.");
		Iterator<String> iterator = Arrays.stream(dossiers).iterator();
	    while(iterator.hasNext()) {
	    	String dos = iterator.next();
	    	dossierTemp = (DossierReel) dossier.get(dos);
			if(dossierTemp == null) {
				dossierTemp = new DossierReel(dos);
				dossier.ajouter(dossierTemp);
			}
			dossier = dossierTemp;
	    }
	}
}