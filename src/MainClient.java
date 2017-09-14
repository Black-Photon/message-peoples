package src;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainClient extends Application{
	public static void main(String[] args){
		Application.launch();
	}

	/**
	 * Creates a ClientSide window and messaging application
	 * @param primaryStage Stage to display in
	 * @throws IOException To account for loading the .fxml file
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		new Main().start(primaryStage, "Client.fxml");
	}
}
