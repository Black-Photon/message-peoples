package src.other;

import javafx.stage.Modality;
import javafx.stage.Stage;
import src.messageBoxes.messageBoxes;

import java.util.ArrayList;

public class Connection_Settings extends messageBoxes {
	private ArrayList<Object> data;
	private static Connection_Settings currentObject;

	public Connection_Settings(){
		currentObject = this;
	}

	public ArrayList<Object> createWindow(){
		stage = new Stage();
		//TODO Main.createWindow("core/Connection_Settings.fxml", stage, "Connection Settings");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();

		if(data==null){
			data = new ArrayList<>();
			data.add(false);
		}
		Connection_Settings_Controller.setConnection_data(null);
		return data;
	}
	public ArrayList<Object> createWindow(Connection_Data connection){
		Connection_Settings_Controller.setConnection_data(connection);
		return createWindow();
	}

	public ArrayList<Object> getData() {
		return data;
	}

	public void setData(ArrayList<Object> data) {
		this.data = data;
	}

	public static Connection_Settings getCurrentObject() {
		return currentObject;
	}
}
