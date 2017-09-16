package src.client;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import src.common.Connection_Data;
import src.common.Main;
import src.common.Start;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Used as controller for a client.
 *
 * <h2>FXML:</h2>
 * <h3>Vars:</h3>
 * TextField userText<br/>
 * TextArea chatWindow<br/>
 * Label name
 *
 * <h3>Methods:</h3>
 * startSendMessage sends whatever's in the TextField and clears it
 *
 */

public class Client implements Initializable{
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP = "127.0.0.1";
	private int port = 50000;
	private Socket socket;
	@FXML
	public TextField userText;
	@FXML
	public TextArea chatWindow;
	@FXML
	public Label name;
	private String user;

	private static Client currentObject;
	public static Connection_Data data;

	/**
	 * Starts the client background running. Called by initialize ONLY
	 */
	private void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		} catch (EOFException e) {
			showMessage("Server ended the connection");
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			closeSystems();
		}
	}

	public void onBackPressed(){
		closeSystems();
		Main.createWindow("Messaging.fxml", Start.getStage(), "Messaging");
	}

	/**
	 * Sends a message to server
	 * @param message Message to send
	 */
	private void sendMessage(String message){
		if(message.equals("")||message.equals(" ")) return;
		message = user + " - "+message;
		silentSend(message);
		showMessage(message);
	}
	private void silentSend(String message){
		if(message.equals("")||message.equals(" ")) return;
		try {
			output.writeObject(message);
			output.flush();
		}catch (IOException e){
			chatWindow.appendText("Error in sending message\n");
		}
	}

	/**
	 * Attempts to connect to server
	 * Shows "Could not connect" on failure (Server inaccessible/down)
	 * @throws IOException Thrown in connection
	 */
	private void connectToServer() throws IOException {
		try {
			showMessage("Attempting Connection...");
			socket = new Socket(InetAddress.getByName(serverIP), port);
			System.out.println("Connected to port: " + port);
		}catch (ConnectException e){
			showMessage("Could not connect");
		}
	}

	/**
	 * Creates input and output streams
	 * @throws IOException Due to creation
	 */
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
	}

	/**
	 * Waits for a message, and displays it
	 * @throws IOException When reading message sent
	 */
	private void whileChatting() throws  IOException{
		silentSend(user+"/u");
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage(message);
			}catch (ClassNotFoundException e){
				showMessage("Unable to process received message");
			}
		}while(!message.endsWith("END"));
	}

	/**
	 * Shows the message passed, and a new line
	 * @param message Message to show
	 */
	private void showMessage(String message){
		chatWindow.appendText(message+"\n");
	}

	/**
	 * Closes connection and prevents typing
	 */
	private void closeSystems(){

		ableToType(false);
		if(!socket.isClosed())
		try{
			output.close();
			input.close();
			socket.close();
			showMessage("Connection Closed");
		}catch (IOException e){
			e.printStackTrace();
		}catch (NullPointerException e) {
			System.out.println("No connection to close");
		}


	}
	public static void close(){
		if(currentObject==null) return;
		currentObject.closeSystems();
	}

	/**
	 * Changes whether you are permitted to type
	 * @param canType Whether you can type
	 */
	private void ableToType(boolean canType){
		userText.setEditable(canType);
	}

	/**
	 * Attempt's to send a message to all connected devices
	 */
	@FXML
	public void startSendMessage(){
		sendMessage(userText.getText());
		userText.setText("");
	}

	/**
	 * Start's the background processes running in a new thread
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Start.getStage().setOnCloseRequest(e->{
			System.out.println("Closing Client");
			closeSystems();
			Start.getStage().close();
			Platform.exit();
			System.exit(0);
		});

		user = "USER";

		currentObject = this;
		if(data!=null) {
			serverIP = data.getIp();
			port = data.getPort();
			name.setText(data.getName());
		}


		chatWindow.textProperty().addListener(e->{
			chatWindow.setScrollTop(Double.MAX_VALUE);
		});

		startEverything();
	}

	public static void setData(Connection_Data data){
		Client.data = data;
	}

	@FXML
	public void onConnectPressed(){
		if(socket!=null) if(!socket.isClosed()) closeSystems();
		startEverything();
	}

	private void startEverything(){
		Task task = new Task() {
			@Override
			protected Object call() throws Exception {
				return null;
			}

			@Override
			public void run() {
				startRunning();
			}
		};

		Thread thread = new Thread(task);
		thread.start();
	}
}
