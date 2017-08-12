import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket socket;

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

	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getPort() {
		return server.getLocalPort();
	}
}
