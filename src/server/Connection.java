package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Object holds all data needed for a connection. Used serverside
 *
 * <h3>Variables:</h3>
 * ObjectOutputStream output;<br/>
 * ObjectInputStream input;<br/>
 * Socket socket;<br/>
 * final int port = 6666;<br/>
 */

class Connection {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket socket;
	private static int port = 50000;
	private String name;

	//Getters and setters

	ObjectOutputStream getOutput() {
		return output;
	}

	void setOutput(ObjectOutputStream output) {
		this.output = output;
	}

	ObjectInputStream getInput() {
		return input;
	}

	void setInput(ObjectInputStream input) {
		this.input = input;
	}

	Socket getSocket() {
		return socket;
	}

	void setSocket(Socket socket) {
		this.socket = socket;
	}

	public static void setPort(int port) {
		Connection.port = port;
	}

	static int getPort(){
		return port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
