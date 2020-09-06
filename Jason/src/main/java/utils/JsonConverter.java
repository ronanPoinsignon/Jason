package utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fichier.FileManager;

/**
 * Classe permettant la manipulation d'objets json.
 * @author ronan
 *
 */
public class JsonConverter {

	public static JsonObject StringToJsonObject(String str) {
		return JsonParser.parseString(str).getAsJsonObject().getAsJsonObject();
	}
	
	public static String JsonObjectToString(JsonObject object) {
		return new Gson().fromJson(object, String.class);
	}
	
	public static Object JsonObjectToObject(JsonObject jObject, Class<?> classe){
		return new Gson().fromJson(jObject, classe);
	}
	
	public static void saveJson(JsonObject json, Path path) throws IOException {
		String str = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(json);
		FileManager.writeFile(path.toFile(), str);
	}
	
	public static JsonObject getJsonFromFile(Path file) throws IOException {
		String fic = null;
		JsonObject json = null;
		fic = FileManager.readFile(file.toFile());
		json = JsonConverter.StringToJsonObject(new String(fic));
		return json;
	}
	
	public static JsonObject sortJson(JsonObject json) throws JsonParseException, JsonMappingException, IOException {
		List<String> liste = new ArrayList<>();
		for(String key : json.keySet()) {
			liste.add(key);
		}
		Collections.sort(liste);
		JsonObject newJson = new JsonObject();
		for(String key : liste) {
			newJson.add(key, json.get(key));
		}
		return newJson;
	}
	
}
