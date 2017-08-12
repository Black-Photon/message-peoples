import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * @author Crunchify.com
 * https://crunchify.com/in-java-jow-to-check-if-socket-is-alive-and-connection-is-active-on-specific-port-issocketalive-utility/
 *
 */

public class CrunchifyIsSocketAliveUtility {
	/**
	 * Crunchify's isAlive Utility
	 *
	 * @param hostName
	 * @param port
	 * @return boolean - true/false
	 */
	public static boolean isSocketAliveUitlitybyCrunchify(String hostName, int port) {
		boolean isAlive = false;

		// Creates a socket address from a hostname and a port number
		SocketAddress socketAddress = new InetSocketAddress(hostName, port);
		Socket socket = new Socket();

		// Timeout required - it's in milliseconds
		int timeout = 2000;

		log("hostName: " + hostName + ", port: " + port);
		try {
			socket.connect(socketAddress, timeout);
			socket.close();
			isAlive = true;

		} catch (SocketTimeoutException exception) {
			System.out.println("SocketTimeoutException " + hostName + ":" + port + ". " + exception.getMessage());
		} catch (IOException exception) {
			System.out.println(
					"IOException - Unable to connect to " + hostName + ":" + port + ". " + exception.getMessage());
		}
		return isAlive;
	}

	// Simple log utility
	private static void log(String string) {
		System.out.println(string);
	}

	// Simple log utility returns boolean result
	private static void log(boolean isAlive) {
		System.out.println("isAlive result: " + isAlive + "\n");
	}

}