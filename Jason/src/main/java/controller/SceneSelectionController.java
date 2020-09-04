package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import appli.AppliExistingProject;
import exception.NomVideException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSelectionController implements Initializable {
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	public void handleShowSceneCreateProject(ActionEvent event){
		try {
			Parent sceneCreation = FXMLLoader.load(getClass().getResource("/resources/fxml/sceneCreationProjet.fxml"));
			Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(sceneCreation));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleShowSceneExistingProject(ActionEvent event){
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		try {
			AppliExistingProject.mainApp(stage, new String[]{"C:\\Users\\ronan\\Desktop\\ronan\\babel\\traductions", "C:\\Users\\ronan\\Desktop\\ronan\\babel\\components", "fr"});
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NomVideException e) {
			e.printStackTrace();
		}
	}
}
