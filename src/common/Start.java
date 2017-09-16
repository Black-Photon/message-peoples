package src.common;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class Start extends Application {
	private static Stage stage;
	private static Sides side;

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = new Stage();
		Main.createWindow("Main.fxml", stage, "Messaging");
		stage.show();
	}

	@FXML
	void onClientPressed() {
		side = Sides.CLIENT;
		Main.createWindow("Messaging.fxml", stage, "Messaging");
	}

	@FXML
	void onServerPressed() {
		side = Sides.SERVER;
		Main.createWindow("Messaging.fxml", stage, "Messaging");
	}

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
