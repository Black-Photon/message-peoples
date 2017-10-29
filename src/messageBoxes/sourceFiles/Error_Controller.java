package messageBoxes.sourceFiles;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Error_Controller extends Message_Controller{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//FXML Variables
	@FXML private BorderPane borderPane;
	@FXML private Label label;

	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	@Override public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);

		borderPane.setPrefWidth(thisObject.getWidth());
		label.setText(thisObject.getText());
	}

	//FXML Methods
	@FXML public void onOkPressed(){
		thisObject.exit();
	}
}
