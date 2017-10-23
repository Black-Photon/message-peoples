package common;

/**
 * The state of the application allows it to react to other threads in different conditions
 */
public enum State {
	/**
	 * Indicates it has begun to load, but is not yet ready for any messages
	 */
	START,
	/**
	 * Indicates it is currently attempting to connect
	 */
	CONNECTING,
	/**
	 * Indicates the application has been asked to end, and stops thread activity
	 */
	END,
	/**
	 * Indicates a crash has occurred, suggesting the application should close, stopping thread activity
	 */
	ERROR,
	/**
	 * Indicates the application is running normally, and should continue
	 */
	RUNNING
}
