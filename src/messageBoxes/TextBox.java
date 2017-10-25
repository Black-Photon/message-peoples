package messageBoxes;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class TextBox extends messageBoxes{

	private static TextBox currentObject;
	private String writtenText;

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
		super(text, width, title);
		currentObject = this;
	}

	/**
	 * Creates a new text box and returns the result
	 * @return Text entered
	 */
	public String getString(){
		writtenText = "";
		stage = new Stage();
		MessageBoxesMain.createWindow("TextBox.fxml", stage, title);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return writtenText;
	}

	public static TextBox getCurrentObject() {
		return currentObject;
	}

	/**
	 * Ignore
	 * @return
	 */
	public String getWrittenText() {
		return writtenText;
	}
	/**
	 * Ignore
	 * @param writtenText
	 */
	public void setWrittenText(String writtenText) {
		this.writtenText = writtenText;
	}

	public void exit(){
		stage.close();
	}
}
