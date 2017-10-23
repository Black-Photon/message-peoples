package common;

/**
 * Holds information about whether the application is working server-side or client-side, so both can coexist
 */
public enum Sides {
	/**
	 * If the application is running the client
	 */
	CLIENT,
	/**
	 * If the application is running the server
	 */
	SERVER
}
