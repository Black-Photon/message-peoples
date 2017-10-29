package messageBoxes.sourceFiles;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Superclass for all Message Box controllers<br/>
 *
 * Files in same package as it allows package-private, making some internal methods invisible
 */
public abstract class Message_Controller implements Initializable{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Main Variables
	protected messageBoxes thisObject;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Initialization
	/**
	 * Set's thisObject to the appropriate object, given by the first element in the resources variable
	 *
	 * @param location
	 * The location used to resolve relative paths for the root object, or
	 * <tt>null</tt> if the location is not known.
	 *
	 * @param resources
	 * The resources used to localize the root object, or <tt>null</tt> if
	 * the root object was not localized.
	 */
	@Override public void initialize(URL location, ResourceBundle resources) {
		String thisKey = resources.getKeys().nextElement();
		thisObject = (messageBoxes) resources.getObject(thisKey);
	}
}
