package common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Used by main class to create windows
 */

public class Windows {
	public void createWindow(String location, Stage window, String title, String startLocation){
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(startLocation + location));
			Scene scene = new Scene(root);
			window.setScene(scene);
			window.setTitle(title);
		}catch(IOException e){
			try {
				new Error("Error #0000: Can't create window");
				e.printStackTrace();
			}catch (Exception e1){
				System.out.println("Can't create window or error window");
				e.printStackTrace();
				System.out.println("\nError 2:\n");
				e1.printStackTrace();
			}
		}
	}
}
