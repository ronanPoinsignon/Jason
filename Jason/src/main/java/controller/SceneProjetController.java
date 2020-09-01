package controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import exception.NomVideException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import projet.Project;
import projet.node.Dossier;
import projet.tradBox.TradBox;

public class SceneProjetController implements Initializable {

	@FXML
	TreeView<Dossier> idTreeViewJson;
	
	@FXML
	ListView<TradBox> idListViewTraduction;
	
	private Path traductions, page;
	private String langue;
	private Project projet;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@FXML
	public void handleMessageExtraction(){
		System.out.println("message");
	}

	public Path getTraductions() {
		return traductions;
	}

	public Path getPage() {
		return page;
	}

	public String getLangue() {
		return langue;
	}
	
	public void initialize(Path traductions, Path page, String defaultLanguage) throws IOException, NomVideException {
		this.traductions = traductions;
		this.page = page;
		this.langue = defaultLanguage;
		projet = new Project(traductions, page, langue, idTreeViewJson, idListViewTraduction);
	}

}
