package common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Used by main class to create windows
 */
public class Windows {
	//METHODS ----------------------------------------------------------------------------------------------------------

	/**
	 * Only called by Main in window creating methods<br/>
	 * Set's the given Stage's scene to one given by the file <b>location</b> in folder <b>startLocation</b><br/>
	 * Does NOT show the stage afterwards - use a shown stage or call stage.show() after
	 * @param location Location of FXML file relative to resources/fxml
	 * @param window Stage to set the scene of
	 * @param title Title for the stage
	 * @param startLocation Folder to find the location relative to
	 * @throws NullPointerException If location is not valid
	 */ //Not static as getClass() can't be called by a static method
	public void createWindow(String location, Stage window, String title, String startLocation){
		try {
			//Set's scene to that given by a FXML file and set's the title
			URL classLocation = getClass().getClassLoader().getResource(startLocation + location);
			if(classLocation==null) throw new NullPointerException("FXML file could not be found");
			Parent root = FXMLLoader.load(classLocation);
			Scene scene = new Scene(root);
			window.setScene(scene);
			window.setTitle(title);
		}catch(IOException e){ //Thrown by FXMLLoader.load();
			new Error("Error #0000: Can't create window");
			e.printStackTrace();
		}
	}
}
