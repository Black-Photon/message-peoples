package server;

import client.Client;
import common.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	private static Connection_Data data;

	//FXML Vars
	@FXML private TextField userText;
	@FXML private TextArea chatWindow;
	@FXML private Label name;






	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		name.setText(data.getName());
		globalState = RUNNING;
		state = START;
		connections = new ArrayList<>();
		try {
			server = new ServerSocket(data.getPort(), 100);
		}catch (IOException e){
			state = ERROR;
			System.out.println("Could not establish Server");
			showMessage(INFO, "Can't create server");
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

			//TODO Review
			System.out.println("Closing Client");
			Start.getStage().close();
			Platform.exit();
			System.exit(0);
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
		connection.setName("USER");
		Connection thisConnection = connection;

		int index = -1;

		//Does each event before checking the state for an END or ERROR to exit, ensuring wherever it is, it will exit out when needed
		ForLoop:
		for(int i = 0; i<255; i++){
			switch(i){
				case 0:
					connectSocket();
					break;
				case 1:
					setupStreams();
					connections.add(thisConnection);
					state = RUNNING;
					break;
				case 2:
					if(!isEndOrError()) startConnection();
					index = connections.indexOf(thisConnection);
					break;
				case 3:
					waitForMessage(index);
					break;
				default:
					break ForLoop;
			}

			if(isEndOrError()){
				break;
			}
		}
		if(isEndOrError()){
			if(isEndOrError()){
				if(index==-1){
					sendMessage(SERVER_END, null, connections.get(index));
					closeConnection(connections.get(index));
				}else{
					sendMessage(SERVER_END, null, thisConnection);
					closeConnection(thisConnection);
				}
				if(connections.size()==0) showMessage(INFO,"Server ended the connection");
			}
		}
		if((globalState==END||globalState==ERROR) && connections.size()==0){
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
			System.out.println("Closed connection");
		}catch (IOException e){
			e.printStackTrace();
		}
		connections.remove(connection);
		if(connections.size()==0) ableToType(false);
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
			case "BOUNCE":
				return BOUNCE;
			default:
				System.out.println("Can't recognise special character");
		}
		return null;
	}
	private String nextString(int index){
		try {
			String out = (String) connections.get(index).getInput().readObject();
			return out;
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
	private void waitForConnectionClose(Connection connection){
		int i = 0;
		interruptConnectionWait(connection);
		while (connection.getSocket() != null && !connection.getSocket().isClosed()){
			try{
				Thread.sleep(5);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
			if(i > Math.pow(2, 8)){
				System.out.println("Forced to manually close connection");
				closeConnection(connection);
				break;
			}
			i++;
		};
	}
	private void interruptConnectionWait(Connection connection){
		try {
			releaseInput(connection);
		}catch(NullPointerException e){
			System.out.println("Input already closed");
		}
	}
	private void releaseInput(Connection connection){
		if(connection.getSocket() == null) return;
		if(connection.getSocket().isClosed()) return;
		sendMessage(BOUNCE, null, connection);
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
			if(message==null) return;
			if(type==SERVER_UP) return;
			connection.getOutput().writeObject(message);
			connection.getOutput().flush();

			if(type!=INFO && type!=SERVER && type!=CLIENT) type = SERVER;
			if(!chatWindow.getText().endsWith(message)) showMessage(type, message);
		}catch(IOException e){
			System.out.println("Couldn't send message");
			e.printStackTrace();
		}
	}
	private void showMessage(Special type, String message){
		showMessage(type, message, connections.indexOf(connection));
	}
	private void showMessage(Special type, String message, int index){
		switch(type){
			case INFO:
				chatWindow.appendText(message+"\n");
				break;
			case SERVER:
				chatWindow.appendText("SERVER - "+message+"\n");
				break;
			case CLIENT:
				chatWindow.appendText(connections.get(index).getName()+" - "+message+"\n");
			default:
				System.out.println("Unexpected Special type");
		}
	}
	private void waitForMessage(int index) throws IOException{
		sendAllMessage(SERVER_UP, null);
		ableToType(true);
		String message, special;
		
		while (!isEndOrError()) {
			special = nextString(index);
			if(isEndOrError()) break;
			if(special.startsWith(Main.getSpecialCode())){
				special = special.substring(Main.getSpecialCode().length(), special.length());

				switch(specialFromString(special)){
					case USER:
						String name = nextString(index);
						connections.get(index).setName(name);
						break;
					case JOIN:
						sendAllMessage(INFO, connections.get(index).getName()+" has joined");
						break;
					case CLIENT_END:
						sendAllMessage(CLIENT_END, connections.get(index).getName()+" ended the connection");
						globalState = END;
						state = END;
						return;
					case CRASH:
						sendAllMessage(INFO, connections.get(index).getName()+" crashed");
						state = END;
						return;
					case USER_EXIT:
						sendAllMessage(INFO, connections.get(index).getName()+" has left");
						break;
					case INFO:
						message = nextString(index);
						sendOthersMessage(INFO, message, connections.get(index));
						break;
					case CLIENT:
						message = nextString(index);
						sendOthersMessage(FORWARD, connections.get(index).getName(), connections.get(index));
						sendOthersMessage(CLIENT, message, connections.get(index));
						showMessage(CLIENT, message, 0);
						break;
					case BOUNCE:
						sendMessage(INFO, "", connection);
					default:
						System.out.println("Don't know what to do with this Special");
				}
				
				
			}else{
				message = special;
				showMessage(CLIENT, message);
			}
		}
		if(isEnd()) return;
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
		try {
			if (connections.size() == 0) server.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		Main.createWindow("Messaging.fxml", Start.getStage(), "Server");
	}
	@FXML void onEndPressed() {
		if(connections.size()==0) return;
		globalState=END;
		int size = connections.size();
		for(int i = 0; i<size; i++){
			waitForConnectionClose(connections.get(0));
		}
		ableToType(false);
		//TODO Close all threads required???
	}
	@FXML void startSendMessage() {
		String message = userText.getText();
		for(char i: message.toCharArray()){
			if(i==' ') message = message.substring(1);
		}
		if(message.equals("")){
			userText.setText("");
			return;
		}
		sendAllMessage(SERVER, message);
		userText.setText("");
	}

	//Getters and Setters
	public static void setData(Connection_Data data){
		Server.data = data;
	}
}