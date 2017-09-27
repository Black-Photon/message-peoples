package server;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import common.Special;
import common.Start;
import common.State;
import messageBoxes.ConfirmBox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Server implements Initializable{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	private static ServerSocket server;
	private static ArrayList<Connection> connections;
	private Connection connection;
	private State state;

	//FXML Vars
	@FXML private TextField userText;
	@FXML private TextArea chatWindow;
	@FXML private Label name;






	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		state = State.START;
		try {
			server = new ServerSocket();
		}catch (IOException e){
			state = State.ERROR;
			System.out.println("Could not establish Server");
			return;
		}
		Start.getStage().setOnCloseRequest(e->{
			if(!new ConfirmBox("Are you sure you want to exit").getAnswer()) return;

			//TODO Review
			System.out.println("Closing Server");
			Start.getStage().close();
			Platform.exit();
			System.exit(0);
		});

	}
	private void startConnection(){
		if(state==State.END || state==State.ERROR) return;

		if(connections.size()==0) showMessage(Special.INFO, "Waiting for connection...");

		new Thread(new Task() {
			@Override
			protected Object call() throws Exception {
				return null;
			}

			public void run(){
				connection = new Connection();
				connections.add(connection);

				//Does each event before checking the state for an END or ERROR to exit, ensuring wherever it is, it will exit out when needed
				for(int i = 0; i<4; i++){
					switch(i){
						case 0:
							connectSocket();
							break;
						case 1:

							break;
						case 2:

							break;
						case 3:

							break;
						default:

					}

					if(state == common.State.END || state == common.State.ERROR) return;
				}
			}
		}).start();
	}

	//Technical Methods
	private void connectSocket(){
		try {
			Socket socket = server.accept();
			connection.setSocket(socket);
		}catch(IOException e){
			state = State.ERROR;
			System.out.println("Could not establish connection");
		}
	}

	//Input/Output
	private void sendAllMessage(Special type, String message){

	}
	private void sendMessage(Special type, String message, Connection connection){

	}
	private void showMessage(Special type, String message){
		switch(type){
			case INFO:
				chatWindow.appendText(message);
				break;
			case SERVER:
				chatWindow.appendText("SERVER - "+message);
				break;
			default:
				System.out.println("Unexpected Special type");
		}
	}

	//User Input
	@FXML void onBackPressed() {

	}
	@FXML void onEndPressed() {

	}
	@FXML void startSendMessage() {

	}

}