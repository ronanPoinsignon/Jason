package projet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import exception.NomVideException;
import fichier.FileManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
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
	
	private String defaultLanguage;
	
	public Project(String defaultLanguage) throws IOException, NomVideException {
		this.defaultLanguage = defaultLanguage;
	}
	
	/**
	 * Crée la map de fichier json à partir du dossier de traduction.
	 * @param dossierTrad
	 * @throws IOException
	 */
	public void setJsonList(Map<String, JsonObject> jsonMap, List<String> pathList, Path dossierTrad) throws IOException {
		Iterator<File> iterator = Arrays.stream(dossierTrad.toFile().listFiles()).iterator();
	      while(iterator.hasNext()) {
	    	  File fichier = iterator.next();
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
	
	/**
	 * Crée l'arborescence du projet.
	 * @throws NomVideException
	 */
	public void setArborescence(Map<String, JsonObject> jsonMap, List<TreeItem<Dossier>> itemList, TreeTableView<Dossier> view) throws NomVideException {
		//DossierReel.removeAll(Racine.getRacine());
		Racine.resetRacine();
		JsonObject json = getDefaultJson(jsonMap);
		Iterator<String> iterator = json.keySet().iterator();
		while(iterator.hasNext()) {
			DossierReel.ajouter(iterator.next());
		}
		DossierTreeItem dos = new DossierTreeItem(Racine.getRacine());
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				view.setRoot(dos);
				Project.this.addItemEvent(itemList, dos);
				try {
					DossierTreeItem.generate(dos, Racine.getRacine());
					addItemEvent(itemList, dos);
					List<String> str = itemList.stream().map(TreeItem<Dossier>::getValue).map(Dossier::getPath).collect(Collectors.toList());
					Iterator<TreeItem<Dossier>> iterator = dos.getChildren().iterator();
				    while(iterator.hasNext()) {
				    	TreeItem<Dossier> dossier = iterator.next();
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
	
	public void modifFile(Map<String, JsonObject> jsonMap, Path traductions, TradBox tradbox, String jsonValue, String key) throws IOException {
		if(jsonValue == null)
			return;
		JsonElement je = new Gson().fromJson(new Gson().toJson(tradbox.getDescription()), JsonElement.class);
		jsonMap.get(key).add(jsonValue, je);
		JsonConverter.saveJson(jsonMap.get(key), new File(traductions.toFile().getPath() + "\\" + key + ".json").toPath());
	}

	public void modifFile(JsonObject json, File file, Timer timer) throws IOException {
		JsonConverter.saveJson(json, file.toPath());
	}

	public void modifFiles(Map<String, JsonObject> jsonMap, Path traductions) throws IOException {
		Iterator<Entry<String, JsonObject>> it = jsonMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, JsonObject> ent = it.next();
			File file = new File(traductions.toFile().getPath() + "\\" + ent.getKey() + ".json");
			if(!file.exists())
				file.mkdirs();
			JsonObject json = ent.getValue();
			JsonConverter.saveJson(json, file.toPath());
		}
	}
	
	public JsonObject onChange(Map<String, TradBox> mapTrad, Map<String, JsonObject> jsonMap, File fichier) {
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
		return json;
	}
	
	public JsonObject getDefaultJson(Map<String, JsonObject> jsonMap) {
		return jsonMap.get(defaultLanguage);
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setdefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	
	public String modifyJsonValue(Map<String, TradBox> mapTrad, Map<String, JsonObject> jsonMap, TreeItem<Dossier> newValue) {
		String path = null;
		try{
			path = newValue.getValue().getPathWithoutRoot();
		}
		catch(NullPointerException e) {

		}
		if(jsonMap.get(defaultLanguage).get(path) != null) {
			for(String key : jsonMap.keySet()) {
				String value = "";
				try {
					value = jsonMap.get(key).get(path).getAsString();
				}
				catch(NullPointerException e) {

				}
				mapTrad.get(key).setDescription(value);
			}
			return path;
		}
		else
			return null;
	}
	
	public void addItemEvent(List<TreeItem<Dossier>> itemList, TreeItem<Dossier> racine) {
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
			addItemEvent(itemList, item);
		}
	}
}
