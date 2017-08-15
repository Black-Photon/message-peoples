import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Used as controller for a client.
 *
 * <h2>FXML:</h2>
 * <h3>Vars:</h3>
 * TextField userText<br/>
 * TextArea chatWindow
 *
 * <h3>Methods:</h3>
 * startSendMessage sends whatever's in the TextField and clears it
 *
 */

public class Client {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private final String serverIP = "127.0.0.1";
	private Socket socket;
	@FXML
	public TextField userText;
	@FXML
	public TextArea chatWindow;

	/**
	 * Starts the client background running. Called by initialize ONLY
	 */
	private void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		} catch (EOFException e) {
			showMessage("Client ended the connection");
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			closeSystems();
		}
	}

	/**
	 * Sends a message to server
	 * @param message Message to send
	 */
	private void sendMessage(String message){
		try {
			if(message.equals("END")){
				output.writeObject("CLIENT - END");
				output.flush();
				showMessage("CLIENT - END");
			}else
			if(!(message.equals("")||message.equals(" "))){
				output.writeObject("CLIENT - " + message);
				output.flush();
				showMessage("CLIENT - " + message);
			}
		}catch (IOException e){
			chatWindow.setText(chatWindow.getText()+"Error in sending message");
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
			socket = new Socket(InetAddress.getByName(serverIP), 6666);
			System.out.println("Connected to port: " + 6666);
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
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage(message);
			}catch (ClassNotFoundException e){
				showMessage("Unable to process received message");
			}
		}while(!message.equals("SERVER - END"));
	}

	/**
	 * Shows the message passed, and a new line
	 * @param message Message to show
	 */
	private void showMessage(String message){
		chatWindow.setText(chatWindow.getText()+message+"\n");
	}

	/**
	 * Closes connection and prevents typing
	 */
	private void closeSystems(){

		ableToType(false);
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

	/**
	 * Changes whether you are permitted to type
	 * @param canType Whether you can type
	 */
	private void ableToType(boolean canType){
		userText.setEditable(canType);
	}

	/**
	 * Start's the background processes running in a new thread
	 */
	@FXML
	public void initialize() {

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

	/**
	 * Attempt's to send a message to all connected devices
	 */
	@FXML
	public void startSendMessage(){
		sendMessage(userText.getText());
		userText.setText("");
	}
}
