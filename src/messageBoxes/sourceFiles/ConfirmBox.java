package messageBoxes.sourceFiles;

/**
 * Creates a response/no response box
 */
public class ConfirmBox extends messageBoxes {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Global
	/**
	 * User response
	 * True for Yes
	 * False for No
	 */
	private boolean response;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Constructors
	public ConfirmBox() {
		this("Please choose an option");
	}
	public ConfirmBox(String text){
		this(text, 400);
	}
	public ConfirmBox(String text, int width){
		this(text, width, "Confirm");
	}
	public ConfirmBox(String text, int width, String title){
		super(text, width, title,"Confirm Box","ConfirmBox.fxml");
	}

	//Technical Methods
	/**
	 *
	 * @return True if response pressed, False is no pressed
	 */
	public boolean createResponseBox() {
		showModalWindow();
		return response;
	}

	//Getters and Setters
	boolean getResponse() {
		return response;
	}
	void setResponse(boolean response) {
		this.response = response;
	}
}
