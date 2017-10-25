package common;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import messageBoxes.TextBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Program start point, containing the Stage and Side<br/>
 * Also acts as a controller for the main start page
 */
public class Start extends Application implements Initializable {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	private static Stage stage;
	private static Sides side;
	private static String username;
	private final NameFiles files = new NameFiles();

	//FXML
	@FXML private Label name;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	/**
	 * Main method - starts the application<br/>
	 * Don't call from anywhere, unless intending to completely start the whole application
	 * @param args Arguments
	 */
	public static void main(String[] args) {
		launch();
	}
	/**
	 * Starts the application - Don't call from anywhere: Auto-Calls
	 * @param primaryStage Stage to work with
	 */
	@Override public void start(Stage primaryStage) {
		stage = new Stage();
		Main.createWindow("Main.fxml", stage, "Messaging");
		stage.show();
	}
	@Override public void initialize(URL location, ResourceBundle resources) {
		//Get existing name. If no existing name, make one called CLIENT
		username = files.loadName();
		if(username==null){
			files.saveName("CLIENT");
			username = files.loadName();
		}
		name.setText(username);
	}

	//User Input
	@FXML void onClientPressed() {
		side = Sides.CLIENT;
		Main.createWindow("Messaging.fxml", stage, "Messaging");
	}
	@FXML void onServerPressed() {
		side = Sides.SERVER;
		Main.createWindow("Messaging.fxml", stage, "Messaging");
	}
	@FXML void onClickChangeName() {
		TextBox textBox = new TextBox("What would you like the username to become", 500, "New Username");
		username = textBox.getString();
		files.saveName(username);
		name.setText(username);
	}

	//Getters and Setters
	public static Stage getStage() {
		return stage;
	}
	public static void setStage(Stage stage) {
		Start.stage = stage;
	}
	public static Sides getSide() {
		return side;
	}
	public static void setSide(Sides side) {
		Start.side = side;
	}
	public static String getUsername() {
		return username;
	}
	public static void setUsername(String username) {
		Start.username = username;
	}
}
