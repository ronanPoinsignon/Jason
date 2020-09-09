package fichier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * Classe permettant de pouvoir sauvegarder et charger des fichiers. 
 * @author ronan
 *
 */
public class FileManager {

	private static final String TYPE_FICHIER = "*.conv";
	private static final String DESCRIPTION_TYPE_FICHIER = "CONVERTER FILE (.conv)";
	
	private static FileManager fileManager = null;
	
	public static FileManager getInstance() {
		if(fileManager == null)
			fileManager = new FileManager();
		return fileManager;
	}
	
	private FileManager() {

	}
	
	/**
	 * Ouvre une fenêtre de séléction de fichier.
	 * @param window
	 * @param extension
	 * @return
	 */
	public File getFile(Window window, String extension) {
		FileChooser chooser = new FileChooser();
		ExtensionFilter extFilter = new FileChooser.ExtensionFilter(DESCRIPTION_TYPE_FICHIER, TYPE_FICHIER);
		chooser.getExtensionFilters().add(extFilter);
		File fichier = chooser.showOpenDialog(window);
		if(fichier != null)
			DirectoryChooserManager.getInstance("sauvegarder").setInitialDirectory(fichier);
		return fichier;
	}
	
	/**
	 * Ouvre une fenêtre de sélection de dossier.
	 * @param window
	 * @return
	 */
	public File getFolder(Window window) {
		DirectoryChooser chooser = new DirectoryChooser();
		return chooser.showDialog(window);
	}
	
	/**
	 * Demande un endroit de sauvegarde et sauvegarde le projet donné dans un fichier.
	 * @param window
	 * @param listeVideos
	 * @throws IOException
	 */
	public void save(Window window) throws IOException {
		FileChooser chooser = new FileChooser();
		DirectoryChooser directory = DirectoryChooserManager.getInstance("sauvegarder");
		chooser.setInitialDirectory(directory.getInitialDirectory());
		ExtensionFilter extFilter = new FileChooser.ExtensionFilter(DESCRIPTION_TYPE_FICHIER, TYPE_FICHIER);
		chooser.getExtensionFilters().add(extFilter);
		File file = directory.getInitialDirectory();
		if(file == null)
			file = chooser.showSaveDialog(window);
		if(file == null)
			return;
		directory.setInitialDirectory(file);
		
	}
	
	/**
	 * Retourne une liste de vidéos depuis un fichier demandé.
	 * @param window
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object load(Window window) throws IOException, ClassNotFoundException {
		File file = fileManager.getFile(window, TYPE_FICHIER);
		if(file == null)
			return null;
		return null;
	}
	
	public static String readFile(File file) throws IOException {
		BufferedReader br = null;
		StringBuilder informations = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = "";
			while ((line = br.readLine()) != null) {
				informations.append(line);
			}
		}
		catch(IOException e) {
			throw e;
		}
		finally {
			try{
				br.close();
			}
			catch(NullPointerException e) {
				
			}
		}
		return informations.toString();
	}
	
	public static void writeFile(File file, String content) throws IOException {
		FileOutputStream output = null;
		PrintStream str = null;
		try {
			output = new FileOutputStream(file);
			str = new PrintStream(output, true, StandardCharsets.UTF_8.toString());
			str.write(content.getBytes());
		}
		catch(IOException e) {
			throw e;
		}
		finally {
			if(str != null)
				str.close();
			if(output != null)
				output.close();
		}
	}
}
