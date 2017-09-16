package src.messageBoxes;

import javafx.stage.Stage;
import src.common.Windows;

public class MessageBoxesMain {
	/**
	 * Call to create a window, with given FXML file, window, and title
	 * @param location Location of FXML file from the jOSeph_4/resources/fxml folder (eg. Load.fxml, quiz/Feedback.fxml)
	 * @param window Window to display to
	 * @param title Title of window
	 */
	public static void createWindow(String location, Stage window, String title){
		 new Windows().createWindow(location, window, title, "src/messageBoxes/resources/fxml/");
	}
}
