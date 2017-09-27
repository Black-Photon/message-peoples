package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import common.Connection_Data;

import java.net.URL;
import java.util.ResourceBundle;

public class Client implements Initializable{

	private static Connection_Data data;

	@FXML
	private TextField userText;

	@FXML
	private TextArea chatWindow;

	@FXML
	private Label name;

	//Initialization

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	void onBackPressed() {

	}

	@FXML
	void onConnectPressed() {

	}

	@FXML
	void startSendMessage() {

	}

	//Getters and Setters

	public static void setData(Connection_Data data){
		Client.data = data;
	}
}