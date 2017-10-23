package messageBoxes;

import javafx.stage.Stage;

public abstract class messageBoxes {
	public Stage stage;
	private double width;
	private String text;
	String title;

	public messageBoxes() {
		this("Text");
	}

	public messageBoxes(String text){
		this(text, 400);
	}

	public messageBoxes(String text, int width){
		this(text, width, "Title");
	}

	public messageBoxes(String text, int width, String title){
		this.width = width;
		this.text = text;
		this.title = title;
	}

	public double getWidth() {
		return width;
	}

	public String getText() {
		return text;
	}
	public void exit(){
		stage.close();
	}
}
