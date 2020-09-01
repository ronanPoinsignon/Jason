package projet.treeView;

import javafx.scene.control.TreeCell;
import projet.node.Dossier;

/**
 * Classe permettant l'affichage des items composant {@link DossierTreeItem}
 * @author ronan
 *
 */
public class NodeTreeCell extends TreeCell<Dossier> {
    @Override 
    protected void updateItem(Dossier item, boolean empty) { 
        super.updateItem(item, empty);
        setText(null); 
        if (!empty && item != null) { 
        	String text = item.getName();
        	setText(text);
        }
    } 
}
