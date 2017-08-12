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

public class Client {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP = "127.0.0.1";
	private Socket socket;

	@FXML
	public TextField userText;
	@FXML
	public TextArea chatWindow;

	public void startRunning() {
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
	private void connectToServer() throws IOException {
		try {
			showMessage("Attempting Connection...");
			socket = new Socket(InetAddress.getByName(serverIP), 6666);
			System.out.println("Connected to port: " + 6666);
			//showMessage("Now connected to " + socket.getInetAddress().getHostName());
		}catch (ConnectException e){
			showMessage("Could not connect");
		}
	}
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
	}
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
	private void showMessage(String message){
		chatWindow.setText(chatWindow.getText()+message+"\n");
	}
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
	private void ableToType(boolean canType){
		userText.setEditable(canType);
	}



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
	@FXML
	public void startSendMessage(){
		new Thread(
				new Task() {
					@Override
					protected Object call() throws Exception {
						return null;
					}
					@Override
					public void run(){
						sendMessage(userText.getText());
						userText.setText("");
					}
				}
		).start();
	}
}
