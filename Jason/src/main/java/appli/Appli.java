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
	}
}
