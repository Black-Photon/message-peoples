import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainServer extends Application{
	public static void main(String[] args){
		Application.launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new Main().start(primaryStage, "Server.fxml");
	}
}
