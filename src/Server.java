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
import java.util.ArrayList;

public class Server {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket socket;
	private int port = 6789;
	private ArrayList<Connection> connections;
	private Connection connection;
	@FXML
	public TextField userText;
	@FXML
	public TextArea chatWindow;

	private static Connection tempConnection;
	private static String message;

	public void startRunning() {
		/*
		try {
			while (true) {
				try {//*/




		connections = new ArrayList<Connection>();
		startConnecting();



					/*
				} catch (EOFException e) {
					showMessage("Server ended the socket");
				} finally {
					closeSystems();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}//*/

	}

	private void sendMessage(String message){
		for(Connection i: connections) {
			try {
				if(message.equals("END")){
					i.getOutput().writeObject("SERVER - END");
					i.getOutput().flush();
					showMessage("SERVER - END");
				}else
				if(!(message.equals("")||message.equals(" "))){
					i.getOutput().writeObject("SERVER - " + message);
					i.getOutput().flush();
					showMessage("SERVER - " + message);
				}
			}catch (IOException e){
				chatWindow.setText(chatWindow.getText()+"Error in sending message\n");
			}
		}
	}
	private void waitForConnection(Connection connection) throws IOException {
		showMessage("Waiting for someone to connect...\n");
		socket = server.accept();
		showMessage("Now connected to " + socket.getInetAddress().getHostName());
	}
	private void setupStreams() throws IOException{
		System.out.println("Setting up streams");
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
		showMessage("Streams setup");
	}
	private void whileChatting(Connection connection) throws  IOException{
		System.out.println("Chatting");
		message = "Successfully connected";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) connection.getInput().readObject();
				showMessage(message);
			}catch (ClassNotFoundException e){
				showMessage("Unable to process received message");
			}catch (IOException e){
				e.printStackTrace();
			}
		}while(!message.equals("CLIENT - END"));
	}
	private void showMessage(String message){
		chatWindow.setText(chatWindow.getText()+message+"\n");
	}
	private void closeSystems(){
		showMessage("Closing Systems...");
		ableToType(false);
		try{
			output.close();
			input.close();
			socket.close();
		}catch (IOException e){
			e.printStackTrace();
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

	private void startConnecting(){
		new Thread(
				new Task() {
					@Override
					protected Object call() throws Exception {
						return null;
					}
					@Override
					public void run(){
						try {
							while (true) {
								try {
									port = findPort();
									System.out.println("Port: "+port);
									connection = new Connection();
									if(port!=-1) {
										server = new ServerSocket(port,100);
										connection.setServer(server);
									}else{
										System.out.println("No ports available");
										break;
									}
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
								} finally {
									closeSystems();
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		).start();
	}
	/*private void whileChattingAll(Connection connection){
		tempConnection = connection;
		new Thread(
				new Task() {
					@Override
					protected Object call() throws Exception {
						return null;
					}
					@Override
					public void run(){
						Connection thisConnection = tempConnection;
						do{
							try{
								message = (String) tempConnection.getInput().readObject();
								showMessage(message);
							}catch (ClassNotFoundException e){
								showMessage("Unable to process received message");
							}catch (IOException e){
								e.printStackTrace();
							}
						}while(!message.equals("CLIENT - END"));
					}
				}
		).start();
	}//*/
	/*private void sendMessageAll(Connection connection){
		tempConnection = connection;
		new Thread(
				new Task() {
					@Override
					protected Object call() throws Exception {
						return null;
					}
					@Override
					public void run() {
						Connection thisConnection = tempConnection;
						try {
							if(message.equals("END")){
								output.writeObject("SERVER - END");
								output.flush();
								showMessage("SERVER - END");
							}else
							if(!(message.equals("")||message.equals(" "))){
								output.writeObject("SERVER - " + message);
								output.flush();
								showMessage("SERVER - " + message);
							}
						}catch (IOException e){
							chatWindow.setText(chatWindow.getText()+"Error in sending message\n");
						}
					}
				}
		).start();
	};//*/
	private int findPort(){
		boolean temp;

		for(int i = 6666; i<6683; i++){
			temp = true;
			for(int j = 0; j<connections.size(); j++){
				try {
					if (i == connections.get(j).getPort()) {
						temp = false;
						break;
					}
				}catch (Exception e){
					System.out.println("No port");
				}
			}
			if(temp) {
				return i;
			}
		}
		return -1;
	}
}
