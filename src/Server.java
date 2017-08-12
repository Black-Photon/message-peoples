import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket socket;
	private ArrayList<Connection> connections;
	@FXML
	public TextField userText;
	@FXML
	public TextArea chatWindow;

	private static String message;
	private static boolean end = false;

	public void startRunning() {
		connections = new ArrayList<Connection>();
		try {
			server = new ServerSocket(Connection.getPort(), 100);
		}catch(IOException e){
			e.printStackTrace();
		}
		startConnecting();
	}

	private void sendMessage(String message, Connection connection){
		if(message.equals("END")){
			end = true;
		}

		try {
			if(!(message.equals("")||message.equals(" "))){
				if(isClient(message)) {
					connection.getOutput().writeObject(message);
					connection.getOutput().flush();
				}else{
					message = "SERVER - " + message;
					connection.getOutput().writeObject(message);
					connection.getOutput().flush();
				}
				//showMessage("SERVER - " + message);
			}
		}catch (IOException e) {
			chatWindow.setText(chatWindow.getText() + "Error in sending message\n");
		}
	}
	private boolean isClient(String message){
		try {
			if(message.substring(0,8).equals("CLIENT -")){
				return true;
			}
			return false;
		}catch(StringIndexOutOfBoundsException e){
			return false;
		}
	}
	private void sendAllMessage(String message){
		for(Connection i: connections) {
			sendMessage(message, i);
		}
	}
	private void waitForConnection(Connection connection) throws IOException {
		if(connections.size()==0) {
			showMessage("Waiting for Connection...");
		}
		socket = server.accept();
		//showMessage("Now connected to " + socket.getInetAddress().getHostName());
	}
	private void setupStreams() throws IOException{
		//System.out.println("Setting up streams");
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
		//showMessage("Streams setup\n");
	}
	private void whileChatting(Connection current) throws  IOException{
		//System.out.println("Chatting");
		message = "Someone connected";
		showMessage("SERVER - "+message);
		sendAllMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) current.getInput().readObject();
				showMessage(message);
				for(Connection connection: connections){
					if(!connection.equals(current)){
						sendMessage(message, connection);
					}
				}
			}catch (EOFException e){
				break;
			}catch (ClassNotFoundException e) {
				showMessage("Unable to process received message");
			} catch (IOException e){
				e.printStackTrace();
				break;
			}
		}while(!message.equals("CLIENT - END"));
	}
	private void showMessage(String message){
		chatWindow.setText(chatWindow.getText()+message+"\n");
	}
	private void closeConnection(Connection connection) {
		try {
			connection.getSocket().close();
			connection.getInput().close();
			connection.getOutput().close();
			connections.remove(connection);
		}catch (IOException e) {
			e.printStackTrace();
		}
		if(end){
			showMessage("Server Ended the Connection");
			ableToType(false);
		}else {
			showMessage("SERVER - Someone Left");
			sendAllMessage("Someone Left");
			if (connections.size() == 0) {
				ableToType(false);
				startConnecting();
			}
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
		showMessage("SERVER - "+userText.getText());
		new Thread(
				new Task() {
					@Override
					protected Object call() throws Exception {
						return null;
					}
					@Override
					public void run(){
						sendAllMessage(userText.getText());
						userText.setText("");
					}
				}
		).start();
	}

	private void startConnecting(){
		new Thread(
				new Task() {
					@Override
					protected Object call() throws Exception {
						return null;
					}
					@Override
					public void run() {
						Connection connection = new Connection();
						try {
							waitForConnection(connection);
							connection.setSocket(socket);
							setupStreams();
							connection.setInput(input);
							connection.setOutput(output);
							connections.add(connection);
							startConnecting();
							whileChatting(connection);
						} catch (EOFException e) {
							showMessage("Server ended the socket");
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							closeConnection(connection);
							return;
						}
					}
				}
		).start();
	}
}
