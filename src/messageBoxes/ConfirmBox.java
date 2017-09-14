package src.messageBoxes;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox extends messageBoxes {
	private static ConfirmBox currentObject;
	private boolean yes;

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
		super(text, width, title);
		currentObject = this;
	}
	public boolean getAnswer() {
		yes = false;
		stage = new Stage();
		MessageBoxesMain.createWindow("ConfirmBox.fxml", stage, title);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return yes;
	}

	public static ConfirmBox getCurrentObject() {
		return currentObject;
	}

	public boolean isYes() {
		return yes;
	}

	public void setYes(boolean yes) {
		this.yes = yes;
	}

	public void exit(){
		stage.close();
	}
}
