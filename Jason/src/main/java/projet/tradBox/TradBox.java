package projet.tradBox;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Classe permettant l'affichage des différentes traductions.
 * @author ronan
 *
 */
public class TradBox extends HBox implements Comparable<TradBox> {

	private Label titre = new Label("");
	private TextField description = new TextField("");
	
	public TradBox() {
		this.addComponent();
	}
	
	public TradBox(String titre, String description) {
		this.titre.setText(titre);
		this.description.setText(description);
		this.addComponent();
	}
	
	public void addComponent() {
		titre.setPrefWidth(35);
		titre.setMinWidth(35);
		titre.setOnMouseClicked(event -> {
			description.requestFocus();
		});
		this.getChildren().add(titre);
		this.getChildren().add(description);
		description.getStyleClass().add("double-underline");
		this.getStylesheets().add("/resources/css/stylesheet.css");
		this.setAlignment(Pos.CENTER_LEFT);
		TradBox.getHgrow(description);
		TradBox.setHgrow(description, Priority.ALWAYS);
	}
	
	public String getTitre(){
		return this.titre.getText();
	}
	
	public void setTitre(String titre) {
		this.titre.setText(titre);
	}
	
	public void setDescription(String description) {
		this.description.setText(description);
	}
	
	public String getDescription() {
		return this.description.getText();
	}
	
	public TextField getDescriptionLabel(){
		return this.description;
	}
	
	public void setEditable(boolean canEdit) {
		this.description.setDisable(!canEdit);
	}

	@Override
	public int compareTo(TradBox o) {
		return this.getTitre().compareTo(o.getTitre());
	}
}
