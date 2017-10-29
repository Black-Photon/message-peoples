package messageBoxes.sourceFiles;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmBox_Controller extends Message_Controller{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//FXML Variables
	@FXML private BorderPane borderPane;
	@FXML private Label title;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	@Override public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		ConfirmBox confirmBox = (ConfirmBox) thisObject;
		title.setText(confirmBox.getText());
		borderPane.setPrefWidth(thisObject.getWidth());
	}

	//FXML Methods
	@FXML public void onPressNo() {
		ConfirmBox confirmBox = (ConfirmBox) thisObject;
		confirmBox.setResponse(false);
		confirmBox.exit();
	}
	@FXML public void onPressYes() {
		ConfirmBox confirmBox = (ConfirmBox) thisObject;
		confirmBox.setResponse(true);
		confirmBox.exit();
	}

}
