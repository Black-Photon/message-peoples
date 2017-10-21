package common;

import javafx.stage.Stage;

public class Main {

	private static final Windows windows = new Windows();
	private static final String specialCode = "//#@vfh7ahvgf//";

	public static void createWindow(String location, Stage window, String title){
		windows.createWindow(location, window, title,"resources/fxml/");
	}

	public static String getSpecialCode() {
		return specialCode;
	}
}
