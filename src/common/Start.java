package common;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Program start point, containing the Stage and Side<br/>
 * Also acts as a controller for the main start page
 */
public class Start extends Application {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	private static Stage stage;
	private static Sides side;



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

	//User Input
	@FXML void onClientPressed() {
		side = Sides.CLIENT;
		Main.createWindow("Messaging.fxml", stage, "Messaging");
	}
	@FXML void onServerPressed() {
		side = Sides.SERVER;
		Main.createWindow("Messaging.fxml", stage, "Messaging");
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
}
