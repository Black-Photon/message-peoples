package client;

import com.sun.corba.se.impl.io.InputStreamHook;
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
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static common.Special.*;
import static common.Special.INFO;
import static common.State.*;

public class Client implements Initializable{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	private static Connection_Data data;
	private static State state;
	private final String username = "CLIENT";

	//FXML Vars
	@FXML private TextField userText;
	@FXML private TextArea chatWindow;
	@FXML private Label name;

	//Connection Variables
	private static Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	private Connection_Data final_data;







	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		state = START;
		name.setText(data.getName());
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

			if(socket!=null && !socket.isClosed())
				sendMessage(USER_EXIT, null);

			//TODO Review
			System.out.println("Closing Server");
			Start.getStage().close();
			Platform.exit();
			System.exit(0);
		});
		final_data = data;
		startConnection();
	}
	private void startConnection(){
		if(isEndOrError()) return;

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
					waitForMessage();
					break;
				default:
					break ForLoop;
			}

			if(isEnd()){
				closeWithMessage(USER_EXIT);
				return;
			}
			if(isError()){
				closeWithMessage(CRASH);
				return;
			}
		}
		if(isEnd()){
			closeWithMessage(USER_EXIT);
		}
		if(isError()){
			closeWithMessage(CRASH);
		}
	}


	//Technical Methods
	private void connectSocket() throws IOException{
		socket = null;
		try {
			showMessage(INFO, "Attempting Connection...");
			socket = new Socket(InetAddress.getByName(final_data.getIp()), final_data.getPort());
			System.out.println("Connected to port: " + final_data.getPort());
		}catch (ConnectException e){
			state = END;
			showMessage(INFO, "Could not connect");
		}
	}
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
	}
	private void closeConnection() throws IOException{
		if(socket==null) return;
		if(socket.isClosed()) return;

		System.out.println("Closing everything");
		socket.close();
		input.close();
		output.flush();
		output.close();
		ableToType(false);
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
	private String nextString(){
		try {
			return (String) input.readObject();
		}catch(ClassNotFoundException | IOException e){
			if(isEndOrError()) return "";
			e.printStackTrace();
		}
		return null;
	}
	private boolean isError(){
		return state == ERROR;
	}
	private boolean isEnd(){
		return state == END;
	}
	private boolean isEndOrError(){
		return isError() || isEnd();
	}
	private void waitForConnectionClose() {
		int i = 0;
		interruptConnectionWait();
		try {
			while (socket != null && !socket.isClosed()){
				try{
					Thread.sleep(5);
				}catch (InterruptedException e){
					e.printStackTrace();
				}
				if(i > Math.pow(2, 8)){
					System.out.println("Forced to manually close connection");
					closeConnection();
					break;
				}
				i++;
			};
		} catch(IOException e){
			System.out.println("Could not close connection after it took too long to end");
		}
	}
	private void interruptConnectionWait(){
		try {
			releaseInput();
		}catch(NullPointerException e){
			System.out.println("Input already closed");
		}
	}
	private void releaseInput(){
		if(socket == null) return;
		if(socket.isClosed()) return;
		sendMessage(BOUNCE, "");
	}
	private void closeWithMessage(Special special){
		if(socket==null) return;
		if(socket.isClosed()) return;
		sendMessage(special, "");
		try {
			closeConnection();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	//Input/Output
	private void sendMessage(Special type, String message){
		try {
			if(type==null) {
				output.writeObject(Main.getSpecialCode() + type.toString());
				output.flush();
				showMessage(CLIENT, message);
			}
			switch(type) {
				case JOIN:
					output.writeObject(Main.getSpecialCode() + type.toString());
					output.flush();
					break;
				case USER:
					output.writeObject(Main.getSpecialCode() + type.toString());
					output.flush();
					if (message == null || message == "") return;
					output.writeObject(message);
					output.flush();
					break;
				default:
					output.writeObject(Main.getSpecialCode() + type.toString());
					output.flush();
					if (message == null || message == "") return;
					output.writeObject(message);
					output.flush();

					if (type != INFO && type != SERVER && type != CLIENT) type = CLIENT;
					showMessage(type, message);
			}
		}catch(IOException e) {
			System.out.println("Couldn't send message");
			e.printStackTrace();
		}
	}
	private void showMessage(Special type, String message){
		switch(type){
			case INFO:
				chatWindow.appendText(message+"\n");
				break;
			case CLIENT:
				chatWindow.appendText(username + " - " + message+"\n");
				break;
			case SERVER:
				chatWindow.appendText("SERVER - "+message+"\n");
				break;
			default:
				System.out.println("Unexpected Special type");
		}
	}
	private void waitForMessage() throws IOException{
		sendMessage(USER, username); //TODO Fix names
		sendMessage(JOIN, null);
		ableToType(true);
		String message, special;

		while (!isEndOrError()) {
			special = nextString();
			if(isEndOrError()) break;
			if(special.startsWith(Main.getSpecialCode())){
				special = special.substring(Main.getSpecialCode().length(), special.length());


				switch(specialFromString(special)){
					case USER:
						//Ignoring
						break;
					case JOIN:
						state = ERROR;
						break;
					case CLIENT_END:
						state = ERROR;
						break;
					case SERVER_END:
						showMessage(INFO, "Server ended the connection");
						state = END;
						return;
					case CRASH:
						showMessage(INFO, "Server crashed");
						state = END;
						return;
					case USER_EXIT:
						state = ERROR;
						break;
					case FORWARD:
						String user = nextString();
						String special2 = nextString();
						if(special2.startsWith(Main.getSpecialCode())){
							special2 = special2.substring(Main.getSpecialCode().length(), special2.length());
							if(specialFromString(special2)==CLIENT){
								showMessage(INFO, user + " - " + nextString());
							}
						}
						break;
					case SERVER_UP:
						showMessage(INFO, "Server Available");
						break;
					case INFO:
						message = nextString();
						if(message.equals("")) break;
						showMessage(INFO, message);
						break;
					case SERVER:
						message = nextString();
						showMessage(SERVER, message);
						break;
					case BOUNCE:
						sendMessage(INFO, null);
						break;
					default:
						System.out.println("Don't know what to do with this Special");
				}


			}else{
				message = special;
				showMessage(SERVER, message);
			}
		}

		if(isEndOrError()){
			closeConnection();
			return;
		}
		state = ERROR;
		System.out.println("Somehow exited loop without END or ERROR");
	}

	//User Input
	@FXML void onBackPressed() {
		if(socket!=null && !socket.isClosed())
		sendMessage(USER_EXIT, null);
		state = END;
		waitForConnectionClose();
		Main.createWindow("Messaging.fxml", Start.getStage(), "Messaging");
	}

	@FXML void onConnectPressed() {
		state = END;
		waitForConnectionClose();
		state = START;
		new Thread(new Task() {
			@Override
			protected Object call() throws Exception {
				return null;
			}

			public void run(){
				startConnection();
			}
		}).start();
	}

	@FXML void startSendMessage() {
		String message = userText.getText();
		for(char i: message.toCharArray()){
			if(i==' ') message = message.substring(1);
			else break;
		}
		if(message.equals("")){
			userText.setText("");
			return;
		}
		sendMessage(CLIENT, message);
		userText.setText("");
	}

	//Getters and Setters

	public static void setData(Connection_Data data){
		Client.data = data;
	}
}