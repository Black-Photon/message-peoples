package src.messageBoxes.resources.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

import src.messageBoxes.Error;

public class Error_Controller implements Initializable{
	@FXML
	private BorderPane borderPane;

	@FXML
	private Label label;

	@FXML
	private void onOkPressed(){
		Error.getCurrentObject().exit();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		borderPane.setPrefWidth(Error.getCurrentObject().getWidth());
		label.setText(Error.getCurrentObject().getText());
	}
}
