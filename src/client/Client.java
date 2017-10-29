package client;

import common.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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

/**
 * Client file, containing the controller for the actual messaging application.
 * It uses a system of states, so simply setting the state can change the behaviour of the whole program - mainly to exit with ease
 */
public class Client extends Common implements Initializable{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	private static Connection_Data data;
	private static State state;

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
	@Override public void initialize(URL location, ResourceBundle resources) {
		state = START;
		name.setText(data.getName());
		closeRequest(Start.getStage(), Sides.CLIENT);
		final_data = data;
		startConnection();
	}
	/**
	 * Runs the main program setting up connections in a new Thread
	 */
	private void startConnection(){
		if(isEndOrError()) return;

		//Starts in a thread so it can be called and not interrupt flow of the program
		//To interrupt the flow, use runtime();
		runInThread();
	}
	/**
	 * Runs the main program setting up connections
	 */
	@Override protected void runtime() throws IOException{
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

			//Close when END or ERROR
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
	/**
	 * Uses the data from the choosing menu to connect the socket to that ip and port
	 * @throws IOException if an I/O error occurs when creating the socket.
	 */
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
	/**
	 * Set's input and output streams
	 * @throws IOException if an I/O error occurs
	 */
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
	}
	/**
	 * Closes connection
	 * @throws IOException If an I/O error happens closing parts
	 */
	@Override protected void closeConnection() throws IOException{
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
	/**
	 * Set's whether the user can type
	 * @param canType True if can type, false if can't
	 */
	private void ableToType(boolean canType){
		userText.setEditable(canType);
	}
	/**
	 * Waits for the next String sent, and returns it
	 * @return The string sent
	 */
	private String nextString(){
		try {
			return (String) input.readObject();
		}catch(ClassNotFoundException | IOException e){
			if(isEndOrError()) return "";
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Test's if an the state is ERROR
	 * @return result
	 */
	private boolean isError(){
		return state == ERROR;
	}
	/**
	 * Test's if an the state is END
	 * @return result
	 */
	private boolean isEnd(){
		return state == END;
	}
	/**
	 * Test's if an the state is END or ERROR
	 * @return result
	 */
	private boolean isEndOrError(){
		return isError() || isEnd();
	}
	/**
	 * Sends a special to the server before closing the connection
	 * @param special Special to send
	 */
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
	/**
	 * Sends a Special and message to the server
	 * @param type Special type of message
	 * @param message Message to send
	 */
	@Override protected void sendMessage(Special type, String message){
		try {
			//If no type, just send the message
			if(type==null) {
				output.writeObject(message);
				output.flush();
				showMessage(CLIENT, message);
			}else
			switch(type) {
				case JOIN: //No need to send the message
					output.writeObject(Main.getSpecialCode() + type.toString());
					output.flush();
					break;
				case USER: //Doesn't show - internal
					output.writeObject(Main.getSpecialCode() + type.toString());
					output.flush();
					if (message == null || message.equals("")) return;
					output.writeObject(message);
					output.flush();
					break;
				default:
					output.writeObject(Main.getSpecialCode() + type.toString());
					output.flush();
					if (message == null || message.equals("")) return;
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
	/**
	 * Shows the message in a way denoted by the special
	 * <table>
	 *     <tr>
	 *         <td>INFO</td>
	 *         <td>Shows the message</td>
	 *     </tr>
	 *     <tr>
	 *         <td>CLIENT</td>
	 *         <td>Shows the message with "(your)USERNAME -" before</td>
	 *     </tr>
	 *
	 *     <tr>
	 *         <td>SERVER</td>
	 *         <td>Shows the message with "SERVER -" before</td>
	 *     </tr>
	 * </table>
	 * @param type Special type
	 * @param message Message to show
	 */
	private void showMessage(Special type, String message){
		switch(type){
			case INFO:
				chatWindow.appendText(message+"\n");
				break;
			case CLIENT:
				chatWindow.appendText(Start.getUsername() + " - " + message+"\n");
				break;
			case SERVER:
				chatWindow.appendText("SERVER - "+message+"\n");
				break;
			default:
				System.out.println("Unexpected Special type");
		}
	}
	/**
	 * Get's messages from connection until END or ERROR
	 * @throws IOException when I/O error occurs
	 */
	private void waitForMessage() throws IOException{
		//Sends info to say you joined
		sendMessage(USER, Start.getUsername());
		sendMessage(JOIN, null);
		ableToType(true);
		String message, special;

		//Continues until END or ERROR
		while (!isEndOrError()) {
			special = nextString();
			if(isEndOrError()) break;
			if(special==null){
				state = ERROR;
				System.out.println("No string read");
			}else
				//If it's a special
			if(special.startsWith(Main.getSpecialCode())){
				special = special.substring(Main.getSpecialCode().length(), special.length());

				//Split just in case of null
				Special special1 = specialFromString(special);
				if(special1==null){
					System.out.println("No such special");
				}else
					switch(special1){
						case CLIENT_END: case JOIN: case USER_EXIT: //Should never happen
							state = ERROR;
							break;
						case SERVER_END: //Close everything
							showMessage(INFO, "Server ended the connection");
							state = END;
							return;
						case CRASH: //Must close - can't work without server
							showMessage(INFO, "Server crashed");
							state = END;
							return;
						case FORWARD: //Display using next string as Username, following as Special (Should be CLIENT) and final as actual message
							String user = nextString();
							String special2 = nextString();
							if(user==null||special2==null){
								state=ERROR;
								System.out.println("Received null from forwarding");
							}else
							if(special2.startsWith(Main.getSpecialCode())){
								special2 = special2.substring(Main.getSpecialCode().length(), special2.length());
								if(specialFromString(special2)==CLIENT){
									showMessage(INFO, user + " - " + nextString());
								}else{
									showMessage(INFO, nextString());
								}
							}
							break;
						case SERVER_UP: //Informs user the server is up
							showMessage(INFO, "Server Available");
							break;
						case INFO: //Displays
							message = nextString();
							if(message==null||message.equals("")) break;
							showMessage(INFO, message);
							break;
						case SERVER: //Displays as SERVER
							message = nextString();
							showMessage(SERVER, message);
							break;
						case BOUNCE: //Sends empty message for Server's releaseInput method
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
		waitForConnectionClose(socket, Sides.CLIENT, null);
		Main.createWindow("Messaging.fxml", Start.getStage(), "Messaging");
	}
	@FXML void onConnectPressed() {
		if(!socket.isClosed()) return;
		state = END;
		waitForConnectionClose(socket, Sides.CLIENT, null);
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
		//Removes useless spaces at start of message
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