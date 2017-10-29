package messageBoxes.sourceFiles;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class TextBox_Controller extends Message_Controller{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//FXML Variables
	@FXML private BorderPane borderPane;
	@FXML private TextField textBar;
	@FXML private Label title;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	@Override public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		TextBox textBox = (TextBox) thisObject;
		textBar.setText(textBox.getDefaultText());

		borderPane.setPrefWidth(thisObject.getWidth());
		title.setText(thisObject.getText());
	}

	//FXML Methods
	@FXML public void onOkPressed(){
		TextBox textBox = (TextBox) thisObject;
		textBox.setUserInput(textBar.getText());
		textBox.exit();
	}
}
