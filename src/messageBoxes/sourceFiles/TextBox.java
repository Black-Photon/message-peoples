package messageBoxes.sourceFiles;

import javafx.stage.Stage;

/**
 * Creates a box allowing the user to input text
 */
public class TextBox extends messageBoxes{
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global
	/**
	 * What the user input to the box
	 */
	private String userInput;
	private String defaultText;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Constructors
	public TextBox() {
		this("Enter:");
	}
	public TextBox(String text){
		this(text, 600);
	}
	public TextBox(String text, int width){
		this(text, width, "Text Box");
	}
	public TextBox(String text, int width, String title){
		this(text, width, title, "");
	}
	public TextBox(String text, int width, String title, String defaultText){
		super(text, width, title,"Text Box","TextBox.fxml");
		this.defaultText = defaultText;
	}

	//Technical Methods
	/**
	 * Creates a new text box and returns the result
	 * @return Text entered, or null if unsuccessful
	 */
	public String createResponseBox(){
		showModalWindow();
		return userInput;
	}

	//Getters and Setters
	String getUserInput() {
		return userInput;
	}
	void setUserInput(String userInput) {
		this.userInput = userInput;
	}
	String getDefaultText() {
		return defaultText;
	}
	void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}
}
