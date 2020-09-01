package projet.node;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import exception.NomVideException;

/**
 * Classe abstraite représentant un dossier virtuel.
 * @author ronan
 *
 */
public abstract class Dossier {

	private static final String delimitation = ".";
		
	private List<Dossier> files = new ArrayList<>();

	private Dossier dossierParent;
	
	private String name;
	
	public Dossier(String name) throws NomVideException {
		if(name.isEmpty())
			throw new NomVideException();
		this.name = name;
	}
	
	protected Dossier() {
		this.name = "";
	}

	public Dossier getDossierParent() {
		return this.dossierParent;
	}
	
	public void setDossierParent(Dossier dossierParent) {
		this.dossierParent = dossierParent;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Dossier> getFiles() {
		return files;
	}
	
	public String getPath() {
		if(dossierParent != null) {
			return dossierParent.getPath() + delimitation + this.name;
		}
		else
			return this.name;
	}
	
	public String getPathWithoutRoot() {
		String path;
		if(dossierParent != null) {
			path = dossierParent.getPath() + delimitation + this.name;
		}
		else
			path = this.name;
		return path.substring((Racine.NAME + ".").length());
	}
	
	public Dossier get(String name) throws NomVideException {
		Dossier dossier = null;
		try{
			dossier = this.files.stream().filter(dos -> dos.getName().equals(name)).findFirst().get();
		}
		catch(NoSuchElementException e) {

		}
		return dossier;
	}
	
	public static void printArborescence(Dossier dossier) {
		for(Dossier dos : dossier.getFiles()) {
			System.out.println(dos.getName() + " => " + dos.getPath());
			printArborescence(dos);
		}
	}
		
	@Override
	public String toString() {
		return this.name;
	}
}
