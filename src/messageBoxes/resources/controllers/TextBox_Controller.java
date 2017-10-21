package messageBoxes.resources.controllers;

import messageBoxes.TextBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class TextBox_Controller implements Initializable{
	@FXML
	private Label title;

	@FXML
	private TextField textBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		title.setText(TextBox.getCurrentObject().getText());
	}

	@FXML
	void onPressOk() {
		TextBox.getCurrentObject().setWrittenText(textBar.getText());
		TextBox.getCurrentObject().exit();
	}
}
