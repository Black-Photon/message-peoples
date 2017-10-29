package messageBoxes.sourceFiles;

/**
 * Creates and shows an error message of chosen Message, width and title
 */
public class Error extends messageBoxes{
	//METHODS ----------------------------------------------------------------------------------------------------------

	//Constructors
	public Error() {
		this("Error");
	}
	public Error(String text){
		this(text, 400);
	}
	public Error(String text, int width){
		this(text, width, "Error");
	}
	public Error(String text, int width, String title){
		super(text, width, title, "Error", "Error.fxml");
	}
}
