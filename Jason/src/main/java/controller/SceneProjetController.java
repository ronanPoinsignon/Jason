package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import exception.NomVideException;
import fichier.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeTableView;
import projet.Project;
import projet.node.Dossier;
import projet.tradBox.TradBox;
import utils.JsonConverter;

public class SceneProjetController implements Initializable {

	private static final String pattern1 = "(?<=[^>]*id=\")([^\"*]*)(?=\"[ ]*defaultMessage=\"[^\"]*\"[^>]*>)";
	private static final String pattern2 = "(?<=[^>]*defaultMessage=\"[^\"]*\"[ ]{0,100}id=\")([^\"*]*)(?=\"[^>]*>)";
	
	
	@FXML
	TreeTableView<Dossier> idTreeTableViewJson;
	
	@FXML
	ListView<TradBox> idListViewTraduction;
	
	private Path traductions, page;
	private String langue;
	@SuppressWarnings("unused")
	private Project projet;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@FXML
	public void handleMessageExtraction(){
		try {
			JsonConverter.saveJson(JsonConverter.sortJson(readFiles(page.toFile(), new JsonObject())), new File(traductions.toFile().getPath() + "\\" + "df.json").toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		//readFiles(page.toFile(), new JsonObject());
		projet = new Project(traductions, page, langue, idTreeTableViewJson, idListViewTraduction);
	}
	
	public JsonObject readFiles(File file, JsonObject json) throws IOException {
		List<String> allMatches = new ArrayList<>();
		for(File fichier : file.listFiles()) {
			allMatches = new ArrayList<String>();
			if(fichier.isDirectory()) {
				readFiles(fichier, json);				
			}
			else {
				System.out.println("NAME : " + fichier.getPath());
				String fic = FileManager.readFile(fichier);
				Matcher matcher = Pattern.compile("<[^>]*(/)?>").matcher(fic);
				Matcher matcherVariable = Pattern.compile(pattern1 + "|" + pattern2).matcher(fic);
				while (matcher.find()) {
					String m = matcher.group();
					allMatches.add(m);
				}
				while (matcherVariable.find()) {
					String m = matcherVariable.group();
					System.out.println(m);
				}
			}
			for(String m : allMatches) {
				Matcher matcherVariable = Pattern.compile("(?<=id=\")([^\"*]*)(?=\")").matcher(m);
				Matcher matcherValue = Pattern.compile("(?<=defaultMessage=\")([^\"*]*)(?=\")").matcher(m);
				while (matcherValue.find() && matcherVariable.find()) {
					String matchesVar = matcherVariable.group();
					String matchesVal = matcherValue.group();
					JsonElement element = new Gson().toJsonTree(matchesVal);
					json.add(matchesVar, element);
				}
			}
		}
		return json;
	}

}
