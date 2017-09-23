package src.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import src.client.Client;
import src.server.Server;
import src.server.tempServer;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Messaging_Controller implements Initializable{

	@FXML
	private TableView<Connection_Data> tableView;

	@FXML
	private TableColumn<Connection_Data, String> nameColumn;

	@FXML
	private TableColumn<Connection_Data, Integer> portColumn;

	@FXML
	private TableColumn<Connection_Data, String> ipColumn;

	private ObservableList tableData;
	private ArrayList<Connection_Data> arrayList;
	private MessagingFiles files;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
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

		arrayList = files.readMessagingConfig("messaging_config.txt");
		refresh();

	}

	@FXML
	void onPressCreate() {
		ArrayList<Object> data = new Connection_Settings().createWindow();
		if(data==null || data.get(0).equals(false)) {
			return;
		}
		addEntry(data);
	}

	@FXML
	void onPressJoin() {
		Connection_Data data = tableView.getSelectionModel().getSelectedItem();
		if(data==null) return;
		if(clientside()){
			Client.setData(data);
			Main.createWindow("Client.fxml" ,Start.getStage(), "Messaging");
		}else
		if(serverside()){
			tempServer.setData(data);
			Main.createWindow("Server.fxml" ,Start.getStage(), "Messaging");
		}

	}
	@FXML
	void onPressEdit() {
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

	@FXML
	void onDeletePressed(){
		arrayList.remove(tableView.getSelectionModel().getSelectedItem());
		refresh();
	}

	@FXML
	void onPressBack(){
		Main.createWindow("Main.fxml", Start.getStage(), "Messaging");
	}

	private void refresh(){
		tableData = FXCollections.observableArrayList(arrayList);
		tableView.setItems(tableData);
		files.saveMessagingConfig("messaging_config.txt", arrayList);
	}

	private void addEntry(ArrayList data){
		String name = (String) data.get(1);
		ArrayList ip = (ArrayList) data.get(2);
		int port = (int) ((ArrayList) data.get(2)).get(4);
		Connection_Data connection = new Connection_Data(name, ip, port);
		arrayList.add(connection);
		refresh();
	}

	private static boolean serverside(){
		return Start.getSide()==Sides.SERVER;
	}
	private static boolean clientside(){
		return Start.getSide()==Sides.CLIENT;
	}
}
