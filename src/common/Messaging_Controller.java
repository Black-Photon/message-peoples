package common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import client.Client;
import server.Server;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Messaging_Controller implements Initializable{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	private ArrayList<Connection_Data> arrayList;
	private MessagingFiles files;

	//FXML Vars
	@FXML private TableView<Connection_Data> tableView;
	@FXML private TableColumn<Connection_Data, String> nameColumn;
	@FXML private TableColumn<Connection_Data, Integer> portColumn;
	@FXML private TableColumn<Connection_Data, String> ipColumn;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	@Override public void initialize(URL location, ResourceBundle resources) {
		//Set's up how the table looks for both sides
		if(clientside()) {
			nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.4));
			portColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
			ipColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.4));
		}else

		if(serverside()) {
			nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.7));
			portColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
			ipColumn.setMaxWidth(0);
		}

		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		portColumn.setCellValueFactory(new PropertyValueFactory<>("port"));
		ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

		files = new MessagingFiles();

		//Get's info form messaging_config.txt
		arrayList = files.readMessagingConfig("messaging_config.txt");
		refresh();

	}

	//User Input
	@FXML void onPressCreate() {
		//Brings up menu for new connection, and adds it
		ArrayList<Object> data = new Connection_Settings().createWindow();
		//In case it was unsuccessful
		if(data==null || data.get(0).equals(false)) {
			return;
		}
		addEntry(data);
	}
	@FXML void onPressJoin() {
		//Joins selected connection
		Connection_Data data = tableView.getSelectionModel().getSelectedItem();
		if(data==null) return;
		if(clientside()){
			Client.setData(data);
			Main.createWindow("Client.fxml" ,Start.getStage(), "Messaging");
		}else
		if(serverside()){
			Server.setData(data);
			Main.createWindow("Server.fxml" ,Start.getStage(), "Messaging");
		}

	}
	@FXML void onPressEdit() {
		//Brings up edit screen, and replaces the old data with the new
		Connection_Data originalData = tableView.getSelectionModel().getSelectedItem();
		if(originalData==null) return;
		ArrayList<Object> data = new Connection_Settings().createWindow(originalData);
		if(data==null || data.get(0).equals(false)) {
			return;
		}
		addEntry(data);
		arrayList.remove(originalData);
		refresh();
	}
	@FXML void onDeletePressed(){
		arrayList.remove(tableView.getSelectionModel().getSelectedItem());
		refresh();
	}
	@FXML void onPressBack(){
		Main.createWindow("Main.fxml", Start.getStage(), "Messaging");
	}


	//Useful Methods
	/**
	 * Refresh's so the table is up to date, and saves current data
	 */
	private void refresh(){
		ObservableList<Connection_Data> tableData = FXCollections.observableArrayList(arrayList);
		tableView.setItems(tableData);
		files.saveMessagingConfig("messaging_config.txt", arrayList);
	}
	/**
	 * Adds entry to table before refreshing
	 * @param data Data to add to the table
	 */
	private void addEntry(ArrayList data){
		String name = (String) data.get(1);
		ArrayList ip = (ArrayList) data.get(2);
		int port = (int) ((ArrayList) data.get(2)).get(4);
		Connection_Data connection = new Connection_Data(name, ip, port);
		arrayList.add(connection);
		refresh();
	}
	/**
	 * Returns true if the side is SERVER
	 */
	private static boolean serverside(){
		return Start.getSide()==Sides.SERVER;
	}
	/**
	 * Returns true if the side is CLIENT
	 */
	private static boolean clientside(){
		return Start.getSide()==Sides.CLIENT;
	}
}
