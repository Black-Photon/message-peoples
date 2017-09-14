package src.other;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import src.messageBoxes.Error;

public class Connection_Settings_Controller implements Initializable{

	@FXML
	private TextField nameBox;

	@FXML
	private TextField ip1Box;

	@FXML
	private TextField ip2Box;

	@FXML
	private TextField ip3Box;

	@FXML
	private TextField ip4Box;

	@FXML
	private TextField ipPortBox;

	private ArrayList<TextField> ipArray;
	private ArrayList<Integer> limits;

	private static Connection_Data connection_data;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ipArray = new ArrayList<>();
		limits = new ArrayList<>();
		ipArray.add(ip1Box);
		limits.add(3);
		ipArray.add(ip2Box);
		limits.add(3);
		ipArray.add(ip3Box);
		limits.add(3);
		ipArray.add(ip4Box);
		limits.add(3);
		ipArray.add(ipPortBox);
		limits.add(5);
		if(connection_data==null) return;
		nameBox.setText(connection_data.getName());
		ip1Box.setText(connection_data.getIpArray().get(0).toString());
		ip2Box.setText(connection_data.getIpArray().get(1).toString());
		ip3Box.setText(connection_data.getIpArray().get(2).toString());
		ip4Box.setText(connection_data.getIpArray().get(3).toString());
		ipPortBox.setText(Integer.toString(connection_data.getPort()));
	}

	@FXML
	void onBackPressed() {
		ArrayList<Object> data = new ArrayList<>();
		data.add(false);
		Connection_Settings.getCurrentObject().setData(data);
		Connection_Settings.getCurrentObject().exit();
	}

	@FXML
	void onConfirmPressed() {
		if(!confirmTextSize()){
			return;
		}
		for(TextField i: ipArray){
			if(i.getText().equals("")){
				new Error("Please Enter Info");
				return;
			}
		}

		returnInfo(true);
	}

	private void returnInfo(boolean success){
		confirmText();
		ArrayList<Object> data = new ArrayList<>();
		data.add(success);
		data.add(nameBox.getText());
		ArrayList<Integer> ipNameArray = new ArrayList();
		for(TextField i: ipArray){
			ipNameArray.add(Integer.parseInt(i.getText()));
		}
		data.add(ipNameArray);
		Connection_Settings.getCurrentObject().setData(data);
		Connection_Settings.getCurrentObject().exit();
	}

	@FXML
	void confirmText(){
		for(int i = 0; i<ipArray.size(); i++){
			TextField thisField = ipArray.get(i);

			if(thisField.getText().length()>limits.get(i)){
				thisField.setText(thisField.getText(0, limits.get(i)));
			}

			try{
				int x = Integer.parseInt(thisField.getText());
			}catch (NumberFormatException e){
				int end = thisField.getLength()-1;
				if(end>=0){
					thisField.setText(thisField.getText(0, end));
				}
			}
		}
	}

	private boolean confirmTextSize(){
		for(int i = 0; i<ipArray.size();i++){
			if(ipArray.get(i).getText().length()>limits.get(i)){
				new Error("Please fully complete all boxes",500);
				return false;
			}
		}
		return true;
	}

	public static void setConnection_data(Connection_Data connection_data) {
		Connection_Settings_Controller.connection_data = connection_data;
	}
}
