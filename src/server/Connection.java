package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Object holds all data needed for a connection. Used server-side
 *
 * <h3>Variables:</h3>
 * ObjectOutputStream output;<br/>
 * ObjectInputStream input;<br/>
 * Socket socket;<br/>
 * static int port = 6666;<br/>
 * String name
 */
public class Connection {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Data
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket socket;
	/**
	 * Port the connections joined to
	 */
	private static int port = 50000;
	/**
	 * Username of connection
	 */
	private String name;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Getters and setters
	public ObjectOutputStream getOutput() {
		return output;
	}
	public void setOutput(ObjectOutputStream output) {
		this.output = output;
	}
	public ObjectInputStream getInput() {
		return input;
	}
	public void setInput(ObjectInputStream input) {
		this.input = input;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public static void setPort(int port) {
		Connection.port = port;
	}
	public static int getPort(){
		return port;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
