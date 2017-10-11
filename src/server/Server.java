package server;

import common.Main;
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
import messageBoxes.Error;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static common.State.*;
import static common.Special.*;

public class Server implements Initializable{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	private static ServerSocket server;
	private static ArrayList<Connection> connections;
	private Connection connection;
	private State state;
	private static State globalState;

	//FXML Vars
	@FXML private TextField userText;
	@FXML private TextArea chatWindow;
	@FXML private Label name;






	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		globalState = RUNNING;
		state = START;
		try {
			server = new ServerSocket();
		}catch (IOException e){
			state = ERROR;
			System.out.println("Could not establish Server");
			return;
		}
		Start.getStage().setOnCloseRequest(e->{
			try {
				boolean answer = !new ConfirmBox("Are you sure you want to exit").getAnswer();
				if (answer){
					e.consume();
					return;
				}
			}catch(Exception exception){
				System.out.println("Error");
				exception.printStackTrace();
			}
		});
		startConnection();
	}
	private void startConnection(){
		if(isEndOrError()) return;

		if(connections.size()==0) showMessage(INFO, "Waiting for connection...");

		new Thread(new Task() {
			@Override
			protected Object call() throws Exception {
				return null;
			}

			public void run(){
				try {
					runtime();
				}catch (IOException e){
					new Error("Error During Runtime");
					e.printStackTrace();
				}
			}
		}).start();
	}
	private void runtime() throws IOException{
		connection = new Connection();
		connections.add(connection);
		connection.setName("USER");

		//Does each event before checking the state for an END or ERROR to exit, ensuring wherever it is, it will exit out when needed
		ForLoop:
		for(int i = 0; i<255; i++){
			switch(i){
				case 0:
					connectSocket();
					break;
				case 1:
					setupStreams();
					state = RUNNING;
					break;
				case 2:
					if(!isEndOrError()) startConnection();
					break;
				case 3:
					waitForMessage();
					break;
				default:
					break ForLoop;
			}

			if(isEndOrError()) return;
		}
		if(isEndOrError()){
			closeConnection(connection);
		}
		if(globalState==END||globalState==ERROR && connections.size()==0){
			server.close();
		}
	}

	//Technical Methods
	private void connectSocket(){
		try {
			Socket socket = server.accept();
			connection.setSocket(socket);
		}catch(IOException e){
			if(isEnd()) return;
			state = ERROR;
			System.out.println("Could not establish connection");
		}
	}
	private void setupStreams() throws IOException{
		ObjectInputStream input = new ObjectInputStream(connection.getSocket().getInputStream());
		ObjectOutputStream output = new ObjectOutputStream(connection.getSocket().getOutputStream());
		output.flush();

		connection.setInput(input);
		connection.setOutput(output);
	}
	private void closeConnection(Connection connection){
		try {
			connection.getInput().close();
			connection.getOutput().close();
			connection.getSocket().close();
		}catch (IOException e){
			e.printStackTrace();
		}
		connections.remove(connection);
	}
	
	//Useful Methods
	private void ableToType(boolean canType){
		userText.setEditable(canType);
	}
	private Special specialFromString(String text){
		switch (text){
			case "USER":
				return USER;
			case "JOIN":
				return JOIN;
			case "CLIENT_END":
				return CLIENT_END;
			case "SERVER_END":
				return SERVER_END;
			case "CRASH":
				return CRASH;
			case "USER_EXIT":
				return USER_EXIT;
			case "SERVER_UP":
				return SERVER_UP;
			case "FORWARD":
				return FORWARD;
			case "SERVER":
				return SERVER;
			case "CLIENT":
				return CLIENT;
			case "INFO":
				return INFO;
			default:
				System.out.println("Can't recognise special character");
		}
		return null;
	}
	private String nextString(){
		try {
			return (String) connection.getInput().readObject();
		}catch(ClassNotFoundException | IOException e){
			e.printStackTrace();
		}
		return null;
	}
	private boolean isError(){
		return globalState== ERROR || state== ERROR;
	}
	private boolean isEnd(){
		return globalState== END || state== END;
	}
	private boolean isEndOrError(){
		return isError() || isEnd();
	}

	//Input/Output
	private void sendAllMessage(Special type, String message){
		for(Connection i: connections){
			sendMessage(type, message, i);
		}
	}
	private void sendOthersMessage(Special type, String message, Connection connection){
		for(Connection i: connections){
			if(!i.equals(connection)) sendMessage(type, message, i);
		}
	}
	private void sendMessage(Special type, String message, Connection connection){
		try {
			if(type!=null)
				connection.getOutput().writeObject(Main.getSpecialCode()+type.toString());
			connection.getOutput().flush();
			connection.getOutput().writeObject(message);
			connection.getOutput().flush();

			if(type!=INFO && type!=SERVER && type!=CLIENT) type = SERVER;
			showMessage(type, message);
		}catch(IOException e){
			System.out.println("Couldn't send message");
			e.printStackTrace();
		}
	}
	private void showMessage(Special type, String message){
		switch(type){
			case INFO:
				chatWindow.appendText(message);
				break;
			case SERVER:
				chatWindow.appendText("SERVER - "+message);
				break;
			case CLIENT:
				chatWindow.appendText(connection.getName()+" - "+message);
			default:
				System.out.println("Unexpected Special type");
		}
	}
	private void waitForMessage() throws IOException{
		sendAllMessage(SERVER_UP, null);
		ableToType(true);
		String message, special;
		
		while (!isEndOrError()) {
			special = nextString();
			if(special.startsWith(Main.getSpecialCode())){
				special = special.substring(Main.getSpecialCode().length(), special.length());
				
				
				switch(specialFromString(special)){
					case USER:
						String name = nextString();
						connection.setName(name);
						break;
					case JOIN:
						sendAllMessage(INFO, connection.getName()+" has joined");
						break;
					case CLIENT_END:
						sendAllMessage(CLIENT_END, connection.getName()+" ended the connection");
						globalState = END;
						state = END;
						return;
					case CRASH:
						sendAllMessage(INFO, connection.getName()+" crashed");
						state = END;
						return;
					case USER_EXIT:
						sendAllMessage(INFO, connection.getName()+" has left");
						break;
					case INFO:
						message = nextString();
						sendOthersMessage(INFO, message, connection);
						break;
					case CLIENT:
						message = nextString();
						sendOthersMessage(FORWARD, connection.getName(), connection);
						sendOthersMessage(CLIENT, message, connection);
						break;
					default:
						System.out.println("Don't know what to do with this Special");
				}
				
				
			}else{
				message = special;
				showMessage(CLIENT, message);
			}
		}
		
		if(isError()){
			globalState = ERROR;
		}else{
			state = ERROR;
			System.out.println("Somehow exited loop without END or ERROR");
		}
	}

	//User Input
	@FXML void onBackPressed() {
		onEndPressed();
		Main.createWindow("Messaging.fxml", Start.getStage(), "Server");
	}
	@FXML void onEndPressed() {
		globalState=END;
		sendAllMessage(INFO, "Server ended the connection");
		//TODO Close all threads required???
	}
	@FXML void startSendMessage() {
		sendAllMessage(SERVER, userText.getText());
	}

}