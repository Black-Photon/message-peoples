package common;

import javafx.stage.Modality;
import javafx.stage.Stage;
import messageBoxes.messageBoxes;

import java.util.ArrayList;

public class Connection_Settings extends messageBoxes {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	private ArrayList<Object> data;
	private static Connection_Settings currentObject;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Constructors
	public Connection_Settings(){
		currentObject = this;
	}

	//Useful Methods
	/**
	 * Creates a window and returns it's data
	 * @return Data given by the message
	 */
	public ArrayList<Object> createWindow(){
		stage = new Stage();

		//Get's data
		if(Start.getSide()==Sides.CLIENT)
			Main.createWindow("Connection_Settings.fxml", stage, "Connection Settings");
		else
		if(Start.getSide()==Sides.SERVER)
			Main.createWindow("Connection_Server_Settings.fxml", stage, "Connection Settings");

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();

		//Analyses data
		if(data==null){
			data = new ArrayList<>();
			data.add(false);
		}
		Connection_Settings_Controller.setConnection_data(null);
		return data;
	}
	/**
	 * Creates a window with given data and returns it's data
	 * @param connection Data to use in the message
	 * @return Data given by the message
	 */
	public ArrayList<Object> createWindow(Connection_Data connection){
		Connection_Settings_Controller.setConnection_data(connection);
		return createWindow();
	}

	//Getters and Setters
	public ArrayList<Object> getData() {
		return data;
	}
	public void setData(ArrayList<Object> data) {
		this.data = data;
	}
	/**
	 * @return Current settings in use
	 */
	public static Connection_Settings getCurrentObject() {
		return currentObject;
	}
}
