package common;

import javafx.stage.Stage;

/**
 * Main class, holding universal variables and methods
 */
public class Main {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global Variables
	private static final Windows windows = new Windows();
	/**
	 * This is the static code used to work out if a message is a special or not<br/>
	 * It is intended to be complex enough it's unlikely a user would use it as the start of a message
	 */
	private static final String specialCode = "//#@vfh7ahvgf//";



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Useful Methods
	/**
	 * Set's the given Stage's scene to one located at resources/fxml/<b>location</b><br/>
	 * Does NOT show the stage afterwards - use a shown stage or call stage.show() after
	 * @param location Location of FXML file relative to resources/fxml
	 * @param window Stage to set the scene of
	 * @param title Title for the stage
	 */
	public static void createWindow(String location, Stage window, String title){
		windows.createWindow(location, window, title,"resources/fxml/");
	}

	//Getters and Setters
	public static String getSpecialCode() {
		return specialCode;
	}
}
