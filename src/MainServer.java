import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainServer extends Application{
	public static void main(String[] args){
		Application.launch();
	}

	public static Stage primaryStage;
	public static Scene primaryScene;

	@Override
	public void start(Stage primaryStage) throws Exception {
		new Main().start(primaryStage, "Server.fxml");
	}
}
