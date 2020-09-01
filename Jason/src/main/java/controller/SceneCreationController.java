package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import appli.AppliExistingProject;
import exception.NomVideException;
import fichier.DirectoryChooserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SceneCreationController implements Initializable {

	@FXML
	private TextField idTextFieldTraduction;
	
	@FXML
	private TextField idTextFieldPage;
	
	@FXML
	private ComboBox<String> idComboBoxLangue;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@FXML
	public void handleSelectFolderTraduction(MouseEvent event){
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		DirectoryChooser directory = DirectoryChooserManager.getInstance("traduction");
		File dir = directory.showDialog(stage);
		if(dir == null)
			return;
		directory.setInitialDirectory(dir);
		idTextFieldTraduction.setText(dir.getPath().toString());
		ArrayList<String> listeLangues = new ArrayList<>();
		for(File fichier : dir.listFiles()) {
			if(fichier.getName().endsWith(".json")) {
				listeLangues.add(fichier.getName().substring(0, fichier.getName().lastIndexOf(".json")));
			}
		}
		Collections.sort(listeLangues);
		idComboBoxLangue.getItems().removeAll(idComboBoxLangue.getItems());
		for(String str : listeLangues)
			idComboBoxLangue.getItems().add(str);
		idComboBoxLangue.getSelectionModel().select(0);
	}

	@FXML
	public void handleSelectFolderPage(MouseEvent event){
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		DirectoryChooser directory = DirectoryChooserManager.getInstance("page");
		File dir = directory.showDialog(stage);
		if(dir == null)
			return;
		directory.setInitialDirectory(dir);
		idTextFieldPage.setText(dir.getPath().toString());
	}
	
	@FXML
	public void handleCreateProject(ActionEvent event) {
		Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		String[] params = new String[3];
		try {
			params[0] = idTextFieldTraduction.getText();
			params[1] = idTextFieldPage.getText();
			params[2] = idComboBoxLangue.getSelectionModel().getSelectedItem().toString();
		}
		catch(NullPointerException e) {
			return;
		}
		try {
			AppliExistingProject.mainApp(stage, params);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NomVideException e) {
			e.printStackTrace();
		}
	}
}
