package server;

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
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static common.State.*;
import static common.Special.*;

public class Server extends Common implements Initializable{
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
	@Override public void initialize(URL location, ResourceBundle resources) {
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
		closeRequest(Start.getStage(), Sides.SERVER);
		startConnection();
	}
	/**
	 * Runs the main program setting up connections in a new Thread
	 */
	private void startConnection(){
		if(isEndOrError()) return;

		if(connections.size()==0) showMessage(INFO, "Waiting for connection...");

		//Starts in a thread so it can be called and not interrupt flow of the program
		//To interrupt the flow, use runtime();
		runInThread();
	}
	/**
	 * Runs the main program setting up connections
	 */
	@Override protected void runtime() throws IOException{
		//Must keep data about current connection, as global variables change with threads
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
					waitForMessage(connections.get(index));
					break;
				default:
					break ForLoop;
			}

			if(isEndOrError()){
				break;
			}
		}//For when it must end and some connections to close
		if(isEndOrError() && connections.size()!=0){
			//If the connection is not yet initialized, instead use the first initialized connection (if there is one)
			if(thisConnection.getSocket()==null){
				for (Connection c : connections) {
					if (c.getSocket() != null) {
						thisConnection = c;
						break;
					}
				}
			}
			//Not else in case the connection did change
			if (thisConnection.getSocket() != null) {
				//Tries to use the index if possible
				if (index == -1) {
					sendMessage(SERVER_END, null, thisConnection);
					closeConnection(thisConnection);
				} else {
					sendMessage(SERVER_END, null, connections.get(index));
					closeConnection(connections.get(index));
				}
			}
			if (connections.size() == 0) showMessage(INFO, "Server ended the connection");
		}
		if((globalState==END||globalState==ERROR) && connections.size()==0){
			server.close();
		}
	}

	//Technical Methods
	/**
	 * Uses the data from the choosing menu to connect the socket to the server on a given port
	 */
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
	/**
	 * Set's input and output streams
	 * @throws IOException if an I/O error occurs
	 */
	private void setupStreams() throws IOException{
		ObjectInputStream input = new ObjectInputStream(connection.getSocket().getInputStream());
		ObjectOutputStream output = new ObjectOutputStream(connection.getSocket().getOutputStream());
		output.flush();

		connection.setInput(input);
		connection.setOutput(output);
	}
	/**
	 * Closes connection
	 */
	@Override protected void closeConnection(Connection connection){
		try {
			connection.getInput().close();
			connection.getOutput().close();
			connection.getSocket().close();
			System.out.println("Closed connection");
		}catch (IOException e){
			e.printStackTrace();
		}
		connections.remove(connection);
		if(connections.size()==0){
			ableToType(false);
		}
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
	private String nextString(Connection connection){
		try {
			return (String) connection.getInput().readObject();
		}catch(SocketException e){ //Should only happen if socket is closed, indicating the user left
			sendAllMessage(INFO, connection.getName()+" has left");
			closeConnection(connection);
			state = END;
		}catch(ClassNotFoundException | IOException e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Test's if an the state is ERROR
	 * @return result
	 */
	private boolean isError(){
		return globalState== ERROR || state== ERROR;
	}
	/**
	 * Test's if an the state is END
	 * @return result
	 */
	private boolean isEnd(){
		return globalState== END || state== END;
	}
	/**
	 * Test's if an the state is END or ERROR
	 * @return result
	 */
	private boolean isEndOrError(){
		return isError() || isEnd();
	}

	//Input/Output
	/**
	 * Sends a given message of given type to all connections<br/>
	 * Shows the message once
	 * @param type Special to send in addition to message
	 * @param message Message to send
	 */
	private void sendAllMessage(Special type, String message){
		for(Connection i: connections){
			silentSend(type, message, i);
		}
		//How to show the message locally
		if(message==null) return;
		if(type==SERVER_UP) return;
		if(type==FORWARD) return;
		if(type!=INFO && type!=SERVER && type!=CLIENT) type = SERVER;
		if(!chatWindow.getText().endsWith(message)) showMessage(type, message);
	}
	/**
	 * Sends a given message of given type to all connections except the one given<br/>
	 * Shows the message once
	 * @param type Special to send in addition to message
	 * @param message Message to send
	 * @param connection Connection to exclude
	 */
	private void sendOthersMessage(Special type, String message, Connection connection){
		for(Connection i: connections){
			if(!i.equals(connection)) silentSend(type, message, i);
		}
		//How to show the message locally
		if(message==null) return;
		if(type==SERVER_UP) return;
		if(type==FORWARD) return;
		if(type!=INFO && type!=SERVER && type!=CLIENT) type = SERVER;
		if(!chatWindow.getText().endsWith(message)) showMessage(type, message, connection);
	}
	/**
	 * Sends a message to the given connection<br/>
	 * Shows the message after
	 * @param type Special to send in addition to the message
	 * @param message Message to send
	 * @param connection Connection to send to
	 */
	@Override protected void sendMessage(Special type, String message, Connection connection){
		silentSend(type, message, connection);
		if(type!=INFO && type!=SERVER && type!=CLIENT) type = SERVER;
		if(message==null||message.equals("")) return;
		if(!chatWindow.getText().endsWith(message)) showMessage(type, message);
	}
	/**
	 * Sends a message to the given connection without showing the result
	 * @param type Special to send in addition to the message
	 * @param message Message to send
	 * @param connection Connection to send to
	 */
	private void silentSend(Special type, String message, Connection connection){
		if(connection.getSocket()==null){
			System.out.println("Empty connection");
			return;
		}
		try {
			if(type!=null)
				//Sends Special
				connection.getOutput().writeObject(Main.getSpecialCode()+type.toString());
			connection.getOutput().flush();
			if(message==null) return;
			if(type==SERVER_UP) return;
			//Sends message
			connection.getOutput().writeObject(message);
			connection.getOutput().flush();
		}catch(IOException e){
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
	 *         <td>Shows the message with "(connection)USERNAME -" before</td>
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
		showMessage(type, message, connection);
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
	 *         <td>Shows the message with "(connection)USERNAME -" before</td>
	 *     </tr>
	 *
	 *     <tr>
	 *         <td>SERVER</td>
	 *         <td>Shows the message with "SERVER -" before</td>
	 *     </tr>
	 * </table>
	 * @param type Special type
	 * @param message Message to show
	 * @param connection Connection to use the username of in case of CLIENT
	 */
	private void showMessage(Special type, String message, Connection connection){
		switch(type){
			case INFO:
				chatWindow.appendText(message+"\n");
				break;
			case SERVER:
				chatWindow.appendText("SERVER - "+message+"\n");
				break;
			case CLIENT:
				chatWindow.appendText(connection.getName()+" - "+message+"\n");
			default:
				System.out.println("Unexpected Special type");
		}
	}
	/**
	 * Get's messages from connection until END or ERROR
	 * @param connection Connection to receive messages from
	 * @throws IOException when I/O error occurs
	 */
	private void waitForMessage(Connection connection) throws IOException{
		ableToType(true);
		String message, special;
		
		while (!isEndOrError()) {
			special = nextString(connection);

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
						case USER: //Set's the connection username
							String name = nextString(connection);
							connection.setName(name);
							break;
						case JOIN: //Displays join message
							sendAllMessage(INFO, connection.getName()+" has joined");
							break;
						case CLIENT_END: //Sends end message, and ends
							sendAllMessage(CLIENT_END, connection.getName()+" ended the connection");
							globalState = END;
							state = END;
							return;
						case CRASH: //Sends crash message, and ends
							sendAllMessage(INFO, connection.getName()+" crashed");
							state = END;
							return;
						case USER_EXIT: //Sends leaving message, closing the connection
							sendAllMessage(INFO, connection.getName()+" has left");
							closeConnection(connection);
							return;
						case INFO: //Sends message to others
							message = nextString(connection);
							sendOthersMessage(INFO, message, connection);
							break;
						case CLIENT: //Sends message to others
							message = nextString(connection);
							sendOthersMessage(FORWARD, connection.getName(), connection); //Used to identify the user which sent the messaeg
							sendOthersMessage(CLIENT, message, connection);
							break;
						case BOUNCE: //Sends empty message for Client's releaseInput method
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
			waitForConnectionClose(connections.get(0).getSocket(), Sides.SERVER, connections.get(0));
		}
		ableToType(false);
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
		sendAllMessage(SERVER, message);
		userText.setText("");
	}

	//Getters and Setters
	public static void setData(Connection_Data data){
		Server.data = data;
	}
}