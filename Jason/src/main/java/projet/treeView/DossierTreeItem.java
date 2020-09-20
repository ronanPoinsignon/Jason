package projet.treeView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import exception.NomVideException;
import javafx.scene.control.TreeItem;
import projet.node.Dossier;
import projet.node.DossierReel;
import projet.node.Racine;
import utils.JsonConverter;

/**
 * Classe représentant un dossier pouvant être affiché.
 * @author ronan
 *
 */
public class DossierTreeItem extends TreeItem<Dossier> {

	public DossierTreeItem() {

	}
	
	public DossierTreeItem(Dossier dossier) {
		super(dossier);
	}

	public void add(TreeItem<Dossier> dossier) {
		this.getChildren().add(dossier);
	}
	
	public static void generate(DossierTreeItem tree, Dossier dossier) throws NomVideException {
		DossierTreeItem dossierTemp = null;
		Iterator<Dossier> iterator = dossier.getFiles().iterator();
		while(iterator.hasNext()) {
			Dossier dos = iterator.next();
			dossierTemp = new DossierTreeItem(dos);
			tree.add(dossierTemp);
			tree = dossierTemp;
			generate(tree, dos);
			tree = (DossierTreeItem) dossierTemp.getParent();
		}
	}
	
	public void setTree(Path file) throws IOException, NomVideException {
		byte[] fic = Files.readAllBytes(file);
		JsonObject json = JsonConverter.StringToJsonObject(new String(fic));
		for(Entry<String, JsonElement> set : json.entrySet()) {
			DossierReel.ajouter(set.getKey());
		}
		Dossier.printArborescence(Racine.getRacine());
		System.out.println(this.getChildren());
	}
	
	public static void printArborescence(TreeItem<Dossier> dossier) {
		for(TreeItem<Dossier> dos : dossier.getChildren()) {
			System.out.println(dos.getValue().getName() + " => " + dos.getValue().getPath());
			printArborescence(dos);
		}
	}
}
