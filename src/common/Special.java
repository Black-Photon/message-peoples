package common;

/**
 * Codes to communicate special messages between the client and server
 */
public enum Special {
	/**
	 * Used to give the receiver the one time username of the sender
	 */
	USER,
	/**
	 * Signals to the server the client has successfully connected
	 */
	JOIN,
	/**
	 * Tells the server the client has requested the server end the connection
	 */
	CLIENT_END,
	/**
	 * Informs the client to shut down as the server has ended the connection
	 */
	SERVER_END,
	/**
	 * Signals a crash, shutting down the server
	 */
	CRASH,
	/**
	 * Tells the server the user has left
	 */
	USER_EXIT,
	/**
	 * Tells the client the server is up
	 */
	SERVER_UP,
	/**
	 * Used to communicate the next message (Should be CLIENT) is forwarded, and should display the appropriate username, send alongside this Special
	 */
	FORWARD,
	/**
	 * Used to signal a message sent by the server
	 */
	SERVER,
	/**
	 * Used to signal a message sent by the client
	 */
	CLIENT,
	/**
	 * Used to send messages without 'USERNAME - ' displayed before it
	 */
	INFO,
	/**
	 * Used to request the receiver send an empty message to release the message buffer, allowing it to detect a change in state
	 */
	BOUNCE
}