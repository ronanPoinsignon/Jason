package projet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.SortType;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import projet.node.Dossier;
import projet.node.DossierReel;
import projet.node.Racine;
import projet.tradBox.TradBox;
import projet.treeView.DossierTreeItem;
import utils.JsonConverter;

/**
 * Classe représentant un projet.
 * @author ronan
 *
 */
public class Project {
	
	private Map<String, JsonObject> jsonMap = new HashMap<>();
	private Map<String, TradBox> mapTrad = new HashMap<>();
	private List<String> pathList = new ArrayList<>();
	private List<TreeItem<Dossier>> itemList = new ArrayList<>();
	
	private Path traductions, page;
	private String defaultLanguage;
	private TreeTableView<Dossier> view;
	private ListView<TradBox> tradView;
	private String jsonValue, jsonValueTemp;
	
	public Project(Path traduction, Path page, String defaultLanguage, TreeTableView<Dossier> view, ListView<TradBox> tradView) throws IOException, NomVideException {
		this.traductions = traduction;
		this.page = page;
		this.defaultLanguage = defaultLanguage;
		this.view = view;
		this.tradView = tradView;
		this.setJsonList(traduction);
		this.setArborescence();
		this.setLanguageList();
		this.setFileListener();
		TreeTableColumn<Dossier, String> colonne = new TreeTableColumn<>("Variables");
		colonne.prefWidthProperty().bind(view.widthProperty());
		colonne.setResizable(false);
		colonne.setCellValueFactory(p -> {
			return new ReadOnlyStringWrapper(p.getValue().getValue().getName());
		});
		view.getColumns().add(colonne);
		view.getSortOrder().add(colonne);
		colonne.setSortType(SortType.DESCENDING);
		view.setShowRoot(false);
		view.getSelectionModel().selectedItemProperty()
		.addListener((observable, oldValue, newValue) -> {
			modifyJson(newValue);
		});
		new DirectoryListener(traduction.toFile(), 1000) {
			
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
						new FileListener(fichier, 1000) {
							
							@Override
							protected void onDelete() {
								Project.this.onDelete(fichier);
							}
							
							@Override
							protected void onCreate() {
								addTradBox(fichier, key);
							}
							
							@Override
							protected void onChange() {
								Project.this.onChange(fichier);
							}
						};
					}
				}
			}
		};
	}
	
	/**
	 * Crée la map de fichier json à partir du dossier de traduction.
	 * @param dossierTrad
	 * @throws IOException
	 */
	private void setJsonList(Path dossierTrad) throws IOException {
		for(File fichier : dossierTrad.toFile().listFiles()) {
			if(fichier.getName().endsWith(".json")) {
				String path = dossierTrad.toAbsolutePath() + "\\" + fichier.getName();
				String fic = FileManager.readFile(new File(path));
				pathList.add(path);
				JsonObject json = null;
				String key = fichier.getName().substring(0, fichier.getName().lastIndexOf(".json"));
				try {
					json = JsonConverter.StringToJsonObject(fic);
					try {
						json = JsonConverter.sortJson(json);
					}
					catch(IOException e) {
						
					}
					jsonMap.put(key, json);
				}
				catch(IllegalStateException e) {
					e.printStackTrace();
				}
				catch(JsonSyntaxException e) {
					jsonMap.put(key, null);
				}
			}
		}
	}
	
	public void setFileListener() {
		for(File file : traductions.toFile().listFiles())
			new FileListener(file, 1000) {
				
				@Override
				public void onDelete() {
					Project.this.onDelete(file);
				}
				
				@Override
				public void onCreate() {
					addTradBox(file, file.getName().substring(0, file.getName().lastIndexOf(".json")));
				}
				
				@Override
				public void onChange() {
					Project.this.onChange(file);
				}
			};
	}
	
	/**
	 * Crée l'arborescence du projet.
	 * @throws NomVideException
	 */
	public void setArborescence() throws NomVideException {
		DossierReel.removeAll(Racine.getRacine());
		JsonObject json = getDefaultJson();
		for(String key : json.keySet()) {
			DossierReel.ajouter(key);
		}
		DossierTreeItem dos = new DossierTreeItem(Racine.getRacine());
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Project.this.view.setRoot(dos);
				Project.this.addItemEvent(dos);
				try {
					DossierTreeItem.generate(dos, Racine.getRacine());
					addItemEvent(dos);

					List<String> str = itemList.stream().map(TreeItem<Dossier>::getValue).map(Dossier::getPath).collect(Collectors.toList());
					for(TreeItem<Dossier> dossier : dos.getChildren()) {
						if(str.contains(dossier.getValue().getPath())) {
							dossier.setExpanded(true);
						}
					}
				} catch (NomVideException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void setLanguageList() {
		for(File fichier : traductions.toFile().listFiles()) {
			if(fichier.getName().endsWith(".json")) {
				String name = fichier.getName().substring(0, fichier.getName().lastIndexOf(".json"));
				addTradBox(fichier, name);
			}
		}
	}
	
	public void handleModification(TradBox tradbox, String key) {
		tradbox.setOnKeyPressed(kp -> {
			if(kp.getCode().equals(KeyCode.ENTER) && jsonValue != null && !jsonValue.isEmpty()) {
				modifFile(tradbox, key);
			}
		});
		tradbox.getDescriptionLabel().focusedProperty().addListener((ob, oldV, newV) -> {
			if(oldV) {
				modifFile(tradbox, key);
			}
		});
	}
	
	public void modifFile(TradBox tradbox, String key) {
		if(jsonValue == null)
			return;
		boolean hasPb = false;
		try {
			JsonElement je = new Gson().fromJson(new Gson().toJson(tradbox.getDescription()), JsonElement.class);
			jsonMap.get(key).add(jsonValue, je);
			JsonConverter.saveJson(jsonMap.get(key), new File(traductions.toFile().getPath() + "\\" + key + ".json").toPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			jsonMap.remove(key);
			mapTrad.remove(key);
			tradView.getItems().remove(tradbox);
		} catch(NullPointerException e) {
			e.printStackTrace();
			hasPb = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		tradbox.setEditable(!hasPb);
	}
	
	public void onDelete(File fichier) {
		String key = fichier.getName().substring(0, fichier.getName().lastIndexOf(".json"));
		TradBox tr = mapTrad.get(key);
		mapTrad.remove(key);
		jsonMap.remove(key);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				tradView.getItems().remove(tr);
			}
		});
	}
	
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
				tradView.getItems().add(tr);
				sortTradView();
			}
		});
		try {
			json = JsonConverter.sortJson(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		jsonMap.put(key, json);
		mapTrad.put(key, tr);
	}
	
	public void onChange(File fichier) {
		String key = fichier.getName().substring(0, fichier.getName().lastIndexOf(".json"));
		jsonMap.remove(key);
		JsonObject json = null;
		boolean hasPb = false;
		try {
			json = JsonConverter.getJsonFromFile(fichier.toPath());
			try {
				json = JsonConverter.sortJson(json);
			}
			catch(IOException e) {
				
			}
			jsonMap.put(key, json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(JsonSyntaxException e) {
			jsonMap.put(key, null);
			hasPb = true;
		}
		mapTrad.get(key).setEditable(!hasPb);
		final JsonObject jsonFinal = json;
		tradView.getItems().forEach(item -> {
			if(item.getTitre().startsWith(key)) {
				try {
					item.setDescription(jsonFinal.get(jsonValue).getAsString());									
				}
				catch(NullPointerException e) {
					item.setDescription("");
				}
			}
		});
		try {
			setArborescence();
		} catch (NomVideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sortTradView();
	}
	
	public JsonObject getDefaultJson() {
		return jsonMap.get(defaultLanguage);
	}

	public Path getTraductions() {
		return traductions;
	}

	public void setTraductions(Path traductions) {
		this.traductions = traductions;
	}

	public Path getPage() {
		return page;
	}

	public void setPage(Path page) {
		this.page = page;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setdefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	
	public void sortTradView() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Collections.sort(tradView.getItems(), new java.util.Comparator<TradBox>() {
				    @Override
				    public int compare(TradBox o1, TradBox o2) {
				        return o1.getTitre().compareTo(o2.getTitre());
				    }
				});
			}
		});
	}
	
	public void modifyJson(TreeItem<Dossier> newValue) {
		String path = null;
		try{
			path = newValue.getValue().getPathWithoutRoot();
		}
		catch(NullPointerException e) {
			
		}
		if(jsonMap.get(defaultLanguage).get(path) != null) {
			jsonValue = path;
			for(String key : jsonMap.keySet()) {
				String value = "";
				try {
					value = jsonMap.get(key).get(path).getAsString();
				}
				catch(NullPointerException e) {
					
				}
				mapTrad.get(key).setDescription(value);
			}
		}
	}
	
	public void addItemEvent(TreeItem<Dossier> racine) {
		ChangeListener<Boolean> lis = null;
		for(TreeItem<Dossier> item : racine.getChildren()) {

			lis = new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if(newValue)
						itemList.add(item);
					else
						itemList.remove(item);
				}
			};
			item.expandedProperty().removeListener(lis);
			item.expandedProperty().addListener(lis);
			addItemEvent(item);
		}
	}
}
