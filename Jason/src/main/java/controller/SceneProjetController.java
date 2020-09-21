package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import exception.NomVideException;
import fichier.DirectoryListener;
import fichier.FileListener;
import fichier.FileManager;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.SortType;
import javafx.scene.control.TreeTableView;
import projet.Project;
import projet.node.Dossier;
import projet.tradBox.TradBox;
import utils.JsonConverter;

public class SceneProjetController implements Initializable {

	//private static final String pattern1 = "(?<=[^>]*id=\")([^\"*]*)(?=\"[ ]*defaultMessage=\"[^\"]*\"[^>]*>)";
	//private static final String pattern2 = "(?<=[^>]*defaultMessage=\"[^\"]*\"[ ]{0,100}id=\")([^\"*]*)(?=\"[^>]*>)";
	

	@FXML
	TreeTableView<Dossier> idTreeTableViewJson;
	
	@FXML
	ListView<TradBox> idListViewTraduction;
	
	private Map<File, Timer> timerList = new HashMap<>();
	private Map<String, JsonObject> jsonMap = new HashMap<>();
	private Map<String, JsonObject> jsonMapTemp = new HashMap<>();
	private Map<String, TradBox> mapTrad = new HashMap<>();
	private Map<TradBox, ChangeListener<String>> mapListener = new HashMap<>();
	private List<TreeItem<Dossier>> itemList = Collections.synchronizedList(new ArrayList<>());
	private List<String> pathList = Collections.synchronizedList(new ArrayList<>());
	
	private Path traductions, page;
	private String langue;
	private Project projet;
	private String jsonValue;

	private int changeCount = 0;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		idTreeTableViewJson.getSelectionModel().selectedItemProperty()
		.addListener((observable, oldValue, newValue) -> {
			removeListeners();
			String value = projet.modifyJsonValue(mapTrad, jsonMapTemp, newValue);
			if(value != null)
				jsonValue = value;
			addListeners();
		});

		TreeTableColumn<Dossier, String> colonne = new TreeTableColumn<>("Variables");
		colonne.prefWidthProperty().bind(idTreeTableViewJson.widthProperty());
		colonne.setResizable(false);
		colonne.setCellValueFactory(p -> {
			return new ReadOnlyStringWrapper(p.getValue().getValue().getName());
		});
		idTreeTableViewJson.getColumns().add(colonne);
		idTreeTableViewJson.getSortOrder().add(colonne);
		colonne.setSortType(SortType.DESCENDING);
		idTreeTableViewJson.setShowRoot(false);
	}
	
	public void removeListeners() {
		for(TradBox tr : mapListener.keySet()) {
			tr.getDescriptionLabel().textProperty().removeListener(mapListener.get(tr));
		}
	}

	public void addListeners() {
		for(TradBox tr : mapListener.keySet()) {
			tr.getDescriptionLabel().textProperty().addListener(mapListener.get(tr));
		}
	}
	
	public void initialize(Path traductions, Path page, String defaultLanguage) throws IOException, NomVideException {
		this.traductions = traductions;
		this.page = page;
		this.langue = defaultLanguage;
		//readFiles(page.toFile(), new JsonObject());
		projet = new Project(langue);
		addFileEvent();
		projet.setJsonList(jsonMap, pathList, traductions);
		jsonMapTemp = jsonMap;
		projet.setArborescence(jsonMap, itemList, idTreeTableViewJson);
		setLanguageList();
		setFileListener();
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
				//Matcher matcherVariable = Pattern.compile(pattern1 + "|" + pattern2).matcher(fic);
				while (matcher.find()) {
					String m = matcher.group();
					allMatches.add(m);
				}
				/*while (matcherVariable.find()) {
					String m = matcherVariable.group();
					System.out.println(m);
				}*/
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
	
	public void addFileEvent() {
		DirectoryListener dir = new DirectoryListener(traductions.toFile()) {
			
			@Override
			protected void onDeleteDirectory() {
				
			}
			
			@Override
			protected void onCreateDirectory() {
				
			}
			
			@Override
			protected void onCreateFile(List<File> liste) {
				for(File fichier : liste) {
					if(!pathList.contains(fichier.getPath())) {
						String key = fichier.getName().substring(0, fichier.getName().lastIndexOf(".json"));
						pathList.add(fichier.getPath());
						addTradBox(fichier, key);
						FileListener listener;
						listener = new FileListener(fichier) {
							
							@Override
							protected void onDelete() {
								SceneProjetController.this.onDelete(fichier);
							}
							
							@Override
							protected void onCreate() {
								SceneProjetController.this.addTradBox(fichier, key);
							}
							
							@Override
							protected void onChange() {
								SceneProjetController.this.onChange(fichier, key);
							}
						};
						Timer timer = new Timer(true);
						timer.schedule(listener, new Date(), 1000);
						timerList.put(fichier, timer);
					}
				}
			}
		};
		Timer timer = new Timer(true);
		timer.schedule(dir, new Date(), 1000);
		timerList.put(traductions.toFile(), timer);
	}
	
	public void handleModification(TradBox tradbox, String key) {
		ChangeListener<String> listener = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> obs, String oldV, String newV) {
				try {
					if(jsonValue == null)
						return;
					JsonElement je = new Gson().fromJson(new Gson().toJson(newV), JsonElement.class);
					jsonMapTemp.get(key).add(jsonValue, je);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		mapListener.put(tradbox, listener);
		tradbox.getDescriptionLabel().textProperty().addListener(listener);
	}
	
	public void handleModif() {
		try {
			projet.modifFiles(jsonMapTemp, traductions);
			jsonMap = jsonMapTemp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public void handleModif(TradBox tradbox, String key){
		boolean hasPb = false;
		try {
			//projet.modifFile(jsonMap, traductions, tradbox, jsonValue, key);
			for(String keySet : jsonMap.keySet()) {
				JsonObject json = jsonMap.get(keySet);
				if(jsonValue == null)
					return;
				JsonElement je = new Gson().fromJson(new Gson().toJson(tradbox.getDescription()), JsonElement.class);
				jsonMap.get(key).add(jsonValue, je);
				File file = new File(traductions.toFile().getPath() + "\\" + keySet + ".json");
				projet.modifFile(json, file, timerList.get(file));
			}
			//projet.modifFiles(jsonMap, traductions);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			jsonMap.remove(key);
			mapTrad.remove(key);
			idListViewTraduction.getItems().remove(tradbox);
		} catch(NullPointerException e) {
			e.printStackTrace();
			hasPb = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		tradbox.setEditable(!hasPb);
	}*/
	
	public void addTradBox(File fichier, String key) {
		JsonObject json = null;
		String value = "";
		boolean hasPb = false;
		try {
			json = JsonConverter.getJsonFromFile(fichier.toPath());
			value = json.get(jsonValue).getAsString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(JsonSyntaxException e) {
			hasPb = true;
		} catch(NullPointerException e) {
			
		}
		TradBox tr = new TradBox(key, value);
		tr.setEditable(!hasPb);
		handleModification(tr, key);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				idListViewTraduction.getItems().add(tr);
				sortTradView();
			}
		});
		try {
			json = JsonConverter.sortJson(json);
		} catch(NullPointerException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		jsonMap.put(key, json);
		mapTrad.put(key, tr);
	}

	public void setLanguageList() {
		for(File fichier : traductions.toFile().listFiles()) {
			if(fichier.getName().endsWith(".json")) {
				String name = fichier.getName().substring(0, fichier.getName().lastIndexOf(".json"));
				addTradBox(fichier, name);
			}
		}
	}
	
	public void setFileListener() {
		for(File file : traductions.toFile().listFiles()) {
			FileListener listener;
			listener = new FileListener(file) {
				
				@Override
				public void onDelete() {
					SceneProjetController.this.onDelete(file);
				}
				
				@Override
				public void onCreate() {
					SceneProjetController.this.addTradBox(file, file.getName().substring(0, file.getName().lastIndexOf(".json")));
				}
				
				@Override
				public void onChange() {
					String key = file.getName().substring(0, file.getName().lastIndexOf(".json"));
					SceneProjetController.this.onChange(file, key);
				}
			};
			Timer timer = new Timer(true);
			timer.schedule(listener, new Date(), 1000);
			timerList.put(file, timer);
		}
	}
	
	public void sortTradView() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Collections.sort(idListViewTraduction.getItems(), new java.util.Comparator<TradBox>() {
				    @Override
				    public int compare(TradBox o1, TradBox o2) {
				        return o1.getTitre().compareTo(o2.getTitre());
				    }
				});
			}
		});
	}
	
	public void onDelete(File file) {
		String key = file.getName().substring(0, file.getName().lastIndexOf(".json"));
		mapTrad.remove(key);
		jsonMap.remove(key);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				TradBox tr = mapTrad.get(key);
				idListViewTraduction.getItems().remove(tr);
			}
		});
	}
	
	public void onChange(File fichier, String key) {
		changeCount++;
		JsonObject json = projet.onChange(mapTrad, jsonMap, fichier);
		idListViewTraduction.getItems().forEach(item -> {
			if(item.getTitre().startsWith(key)) {
				try {
					item.setDescription(json.get(jsonValue).getAsString());									
				}
				catch(NullPointerException e) {
					item.setDescription("");
				}
			}
		});
		if(changeCount == 1) {
			try {
				projet.setArborescence(jsonMap, itemList, idTreeTableViewJson);
			} catch (NomVideException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sortTradView();
		}
		changeCount--;
	}
}
