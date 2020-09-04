package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
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
	
	public static void saveJson(JsonObject json, Path path) throws FileNotFoundException {
		String str = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(json);
		PrintWriter pw = new PrintWriter(path.toFile());
		pw.write(str);
		pw.close();
	}
	
	public static JsonObject getJsonFromFile(Path file) throws IOException {
		byte[] fic = null;
		JsonObject json = null;
		fic = Files.readAllBytes(file);
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
