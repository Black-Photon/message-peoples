package common;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Side;
import javafx.stage.Stage;
import messageBoxes.ConfirmBox;
import messageBoxes.Error;
import server.Connection;

import java.io.IOException;
import java.net.Socket;

import static common.Special.*;
import static common.Special.BOUNCE;
import static common.Special.INFO;

/**
 * Used to hold methods that can be analogous or similar in both Server.java and Client.java
 */
public class Common {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	protected static Socket socket;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Main Methods
	/**
	 * Set's the close request for a given stage to that for the application
	 * @param stage Stage to set close request of
	 * @param side How to process close request
	 */
	protected void closeRequest(Stage stage, Sides side){
		stage.setOnCloseRequest(e->{
			//Checks you actually intend to exit
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
			if(side==Sides.SERVER)
				System.out.println("Closing Server");
			if(side==Sides.CLIENT) {
				System.out.println("Closing Client");
				if(socket!=null && !socket.isClosed())
					sendMessage(USER_EXIT, null);
			}
			Start.getStage().close();
			Platform.exit();
			System.exit(0);
		});
	}
	/**
	 * Start's an overridden runtime() method in a new Thread
	 */
	protected void runInThread(){
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
	/**
	 * Convert's a String to a type SPECIAL
	 * @param text Text to convert
	 * @return SPECIAL translation, or null if it is not a special
	 */
	protected Special specialFromString(String text){
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
	/**
	 * Wait's a while see if setting State to END or ERROR takes effect, and if not, force closes it
	 *
	 * @param socket To check if closed
	 * @param side Determines how it proceeds
	 * @param connection If SERVER, must have a connection to close
	 */
	protected void waitForConnectionClose(Socket socket, Sides side, Connection connection) {
		int i = 0;

		//When to stop - If it is x, It will wait 0.005*2^x seconds
		final int stopping_point = 8;

		//Must interrupt the waitForMessage method, which can't check for END until a message is received
		interruptConnectionWait(connection);
		try {
			while (socket != null && !socket.isClosed()){
				try{
					Thread.sleep(5);
				}catch (InterruptedException e){
					e.printStackTrace();
				}

				if(i > Math.pow(2, stopping_point)){
					System.out.println("Forced to manually close connection");
					if(side==Sides.CLIENT) closeConnection();
					if(side==Sides.SERVER) closeConnection(connection);
					break;
				}
				i++;
			}
		} catch(IOException e){
			System.out.println("Could not close connection after it took too long to end");
		}
	}
	/**
	 * Attempts to interrupt the waitForMessage method
	 */
	private void interruptConnectionWait(Connection connection){
		try {
			releaseInput(connection);
		}catch(NullPointerException e){
			System.out.println("Input already closed");
		}
	}
	/**
	 * Sends a message to the server requesting it to send a blank message, in order to release the attempt to get the next string
	 */
	private void releaseInput(Connection connection){
		if(connection==null) {
			if (socket == null) return;
			if (socket.isClosed()) return;
			sendMessage(BOUNCE, "");
		}else{
			if (connection.getSocket() == null) return;
			if (connection.getSocket().isClosed()) return;
			sendMessage(BOUNCE, null, connection);
		}
	}

	//Empty Methods for Override
	protected void closeConnection() throws IOException{}
	protected void closeConnection(Connection connection){}
	protected void runtime() throws IOException{}
	protected void sendMessage(Special type, String message){}
	protected void sendMessage(Special type, String message, Connection connection){}
}
