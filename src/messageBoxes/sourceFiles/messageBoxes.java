package messageBoxes.sourceFiles;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.Error;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Abstract class, which contains all the basic info for message boxes
 */
public abstract class messageBoxes {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Sub-Class Variables
	protected Stage stage;
	/**
	 * Width of the window
	 */
	protected double width;
	/**
	 * Main text shown in the window
	 */
	protected String text;
	/**
	 * Title of the window
	 */
	protected String title;

	//Sub-Class Constants
	/**
	 * Name of the message type
	 */
	protected String name;
	/**
	 * FXML file name
	 */
	protected String fileName;

	//Object Related
	/**
	 * This object
	 */
	protected messageBoxes thisObject;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Constructors

	/**
	 * Set's variables
	 * @param text Text to show
	 * @param width Width of the window
	 * @param title Window title
	 * @param name Name of the type of message box
	 * @param fileName Name of the FXML file
	 */
	public messageBoxes(String text, int width, String title, String name, String fileName){
		this.width = width;
		this.text = text;
		this.title = title;
		thisObject = this;
		stage = new Stage();
		this.name = name;
		this.fileName = fileName;
	}

	//Technical Methods
	/**
	 * Creates a window and shows it
	 */
	public void showWindow(){
		createWindow();
		stage.show();
	}
	/**
	 * Creates a window which doesn't allow clicking to main application and shows it
	 */
	public void showModalWindow(){
		createWindow();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
	/**
	 * Set's up the window without showing it
	 * Passes the current object as parameters
	 */
	protected void createWindow(){
		ResourceBundle resources = new ResourceBundle() {
			/**
			 * Should be "this" for this object
			 * @param key What data to retrieve
			 * @return The Object relating to the key
			 */
			@Override
			protected Object handleGetObject(String key) {
				if(key.equals("this")){
					return thisObject;
				}
				return null;
			}

			@Override
			public Enumeration<String> getKeys() {
				Enumeration<String> enumeration = new Enumeration<String>() {
					private String[] elements = {"this"};
					private int count = 0;

					@Override
					public boolean hasMoreElements() {
						if(count<elements.length) return true;
						return false;
					}

					@Override
					public String nextElement() {
						count++;
						return elements[count-1];
					}
				};
				return enumeration;
			}
		};      //Basically contains this object in a convoluted way. Supports extra info
		createWindow(resources);
	}
	/**
	 * Set's up the window without showing it
	 * @param resources Resources to pass
	 */
	protected void createWindow(ResourceBundle resources){
		try {
			//Set's scene to that given by a FXML file and set's the title
			URL classLocation = getClass().getClassLoader().getResource("messageBoxes/resources/fxml/"+fileName);
			if(classLocation==null) throw new NullPointerException("FXML file could not be found");
			Parent root = FXMLLoader.load(classLocation, resources);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle(title);
		}catch(IOException e){ //Thrown by FXMLLoader.load();
			new Error("sourceFiles.Error #0000: Can't create window");
			e.printStackTrace();
		}
	}
	/**
	 * Closes the message box, whatever the state of it
	 */
	public void exit(){
		stage.close();
	}

	//Getters and Setters
	double getWidth() {
		return width;
	}
	void setWidth(double width) {
		this.width = width;
	}
	String getText() {
		return text;
	}
	void setText(String text) {
		this.text = text;
	}
	String getTitle() {
		return title;
	}
	void setTitle(String title) {
		this.title = title;
	}
	Stage getStage() {
		return stage;
	}
	void setStage(Stage stage) {
		this.stage = stage;
	}
}
