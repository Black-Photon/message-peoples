package common;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import messageBoxes.Error;

public class Connection_Settings_Controller implements Initializable{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//FXML Vars
	@FXML private TextField nameBox;
	@FXML private TextField ip1Box;
	@FXML private TextField ip2Box;
	@FXML private TextField ip3Box;
	@FXML private TextField ip4Box;
	@FXML private TextField ipPortBox;

	//Data
	private ArrayList<TextField> ipArray;
	private ArrayList<Integer> limits;
	private static Connection_Data connection_data;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	@Override public void initialize(URL location, ResourceBundle resources) {
		//Set's up all data using previously set connection_data variable
		ipArray = new ArrayList<>();
		limits = new ArrayList<>();
		if(serverside()){
			ip1Box = new TextField();
			ip2Box = new TextField();
			ip3Box = new TextField();
			ip4Box = new TextField();
		}
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

	//FXML Methods
	@FXML void onBackPressed() {
		ArrayList<Object> data = new ArrayList<>();
		data.add(false);
		Connection_Settings.getCurrentObject().setData(data);
		Connection_Settings.getCurrentObject().exit();
	}
	@FXML void onConfirmPressed() {
		if(!confirmTextSize()){
			return;
		}
		if(clientside())/* then */for(TextField i: ipArray){
			if(i.getText().equals("")){
				new Error("Please Enter Info");
				return;
			}
		}else
		if(serverside())/* then */for(TextField i: ipArray){
			if(i.getText().equals("")){
				if(i.equals(ipPortBox)) {
					new Error("Please Enter Info");
					return;
				}else{
					i.setText("0");
				}
			}
		}

		returnInfo(true);
	}
	/**
	 * Confirms each part with the confirm() method
	 */
	@FXML void confirmText(){
		if(clientside())
			for(int i = 0; i<ipArray.size(); i++){
				confirm(ipArray.get(i), limits.get(i));
			}else
		if(serverside()) confirm(ipPortBox, limits.get(4));
	}

	//Useful Methods
	/**
	 * Set's the data in a static global variable which be accessed after the program is closed
	 * @param success True if the procedure was successful
	 */
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
	/**
	 * Confirms the field does not contain more characters than allowed, removing one if it does, and confirms there are no letters, also removing
	 * @param textField To confirm
	 * @param limit Limit to test against
	 */
	private void confirm(TextField textField, int limit){
		if(textField.getText().length()>limit){
			textField.setText(textField.getText(0, limit));
		}

		try{
			int x = Integer.parseInt(textField.getText());
		}catch (NumberFormatException e){
			int end = textField.getLength()-1;
			if(end>=0){
				textField.setText(textField.getText(0, end));
			}
			confirm(textField, limit);
		}
	}
	/**
	 * Ensures all text boxes are fully completed, giving an error message otherwise
	 * @return True if all text boxes are completed
	 */
	private boolean confirmTextSize(){
		for(int i = 0; i<ipArray.size();i++){
			if(clientside() || i==4)
//				then
				if(ipArray.get(i).getText().length()>limits.get(i)){
					new Error("Please fully complete all boxes",500);
					return false;
				}
		}
		return true;
	}
	private static boolean serverside(){
		return Start.getSide()==Sides.SERVER;
	}
	private static boolean clientside(){
		return Start.getSide()==Sides.CLIENT;
	}

	//Getters and Setters
	public static void setConnection_data(Connection_Data connection_data) {
		Connection_Settings_Controller.connection_data = connection_data;
	}
}
