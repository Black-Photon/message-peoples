package src;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

class Main {
	/**
	 * Contains all the common info so similar client and server functions can be called when creating the window. Call as first function.
	 *
	 * @param primaryStage The main stage that is passed for the application
	 * @param name The location of the .fxml file
	 * @throws IOException To account for loading the .fxml file
	 *
	 */
	void start(Stage primaryStage, String name) throws IOException {
		//TODO change this
		FXMLLoader fxmlLoader = new FXMLLoader();
		Pane root = fxmlLoader.load(getClass().getResource(name).openStream());
		Scene primaryScene = new Scene(root);
		primaryStage.setTitle("Chat Window");
		primaryStage.setScene(primaryScene);
		primaryStage.setOnCloseRequest(e -> {primaryStage.close(); Platform.exit(); System.exit(0);});
		primaryStage.show();

	}
}
