package appli;

import java.io.File;
import java.io.IOException;

import controller.SceneProjetController;
import exception.NomVideException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import projet.Project;
import utils.Utils;

public class AppliExistingProject extends Application{
	
	public static void main(String[] args) throws IOException, NomVideException {
		if(args == null || Utils.getRealArrayLength(args) != 3) {
			Appli.main(args);
			return;
		}
		System.out.println(args[0]);
		System.out.println(args[1]);
		System.out.println(args[2]);
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent sceneSelection = FXMLLoader.load(getClass().getResource("/resources/fxml/sceneProjet.fxml"));
		Scene scene = new Scene(sceneSelection);
		stage.setTitle("Séléction d'un projet");
		stage.getIcons().add(new Image(Utils.class.getResourceAsStream("pogger.png")));
		stage.setScene(scene);
		stage.setMinHeight(300);
		stage.setMinWidth(500);
		stage.show();
	}
	
	public static void mainApp(Stage stage, String[] params) throws IOException, NomVideException {
		if(params == null || Utils.getRealArrayLength(params) != 3) {
			Appli.main(params);
			return;
		}
		stage.setTitle("Modification d'un projet");
		FXMLLoader loader = new FXMLLoader(Project.class.getResource("/resources/fxml/sceneProjet.fxml"));
		Parent sceneProjet = loader.load();
		SceneProjetController controller = loader.getController();
		controller.initialize(new File(params[0]).toPath(), new File(params[1]).toPath(), params[2]);
		stage.setScene(new Scene(sceneProjet));
	}
	
}
