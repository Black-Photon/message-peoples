import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main {

	public static Stage primaryStage;
	public static Scene primaryScene;

	public void start(Stage primaryStage, String name) throws Exception {
		Main.primaryStage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader();
		Pane root = fxmlLoader.load(getClass().getResource(name).openStream());
		primaryScene = new Scene(root);
		primaryStage.setTitle("Chat Window");
		primaryStage.setScene(primaryScene);
		primaryStage.setOnCloseRequest(e -> {primaryStage.close(); Platform.exit(); System.exit(0);});
		primaryStage.show();

		//Server server = (Server) fxmlLoader.getController();
		//server.ping("Hello World!");




	}
}
