import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainClient extends Application{
	public static void main(String[] args){
		Application.launch();
	}

	public static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainClient.primaryStage = primaryStage;
		new Main().start(MainClient.primaryStage, "Client.fxml");
	}
}
