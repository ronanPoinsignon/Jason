package appli;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.Utils;

public class Appli extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent sceneSelection = FXMLLoader.load(getClass().getResource("/resources/fxml/sceneSelectionProjet.fxml"));
		Scene scene = new Scene(sceneSelection);
		stage.setTitle("Séléction d'un projet");
		stage.getIcons().add(new Image(Utils.class.getResourceAsStream("pogger.png")));
		stage.setScene(scene);
		stage.setMinHeight(300);
		stage.setMinWidth(500);
		stage.show();
		/*stage.setTitle("Tree Table View Samples");
        final Scene scene = new Scene(new Group(), 200, 400);
        Group sceneRoot = (Group)scene.getRoot();  

        //Creating tree items
        final TreeItem<String> childNode2 = new TreeItem<>("Child Node 2");
        final TreeItem<String> childNode3 = new TreeItem<>("Child Node 3");
        final TreeItem<String> childNode1 = new TreeItem<>("Child Node 1");

        //Creating the root element
        final TreeItem<String> root = new TreeItem<>("Root node");
        root.setExpanded(true);   

        //Adding tree items to the root
        root.getChildren().setAll(childNode1, childNode2, childNode3);        

        //Creating a column
        TreeTableColumn<String,String> column = new TreeTableColumn<>("Column");
        
        column.setPrefWidth(150);   

        //Defining cell content
        column.setCellValueFactory((CellDataFeatures<String, String> p) -> 
            new ReadOnlyStringWrapper(p.getValue().getValue()));

        //Creating a tree table view
        final TreeTableView<String> treeTableView = new TreeTableView<>(root);
        treeTableView.getColumns().add(column);
        treeTableView.setPrefWidth(152);
        treeTableView.setShowRoot(true);             
        sceneRoot.getChildren().add(treeTableView);
        stage.setScene(scene);
        column.setComparator(Comparator.comparing(String::length));
        stage.show();*/
	}
}
