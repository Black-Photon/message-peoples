package common;

import java.io.File;
import java.io.IOException;

/**
 * Used to control the access of the name_config.txt file
 */
public class NameFiles extends Files {

	//VARIABLES --------------------------------------------------------------------------------------------------------

	//File Variables
	private String location = "name_config.txt";
	private File file;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Constructor
	public NameFiles(){
		file = new File(location);
	}

	//Technical
	/**
	 * Get's the name for the name_config.txt file, returning null if the file is not there
	 * @return The contents of the file, or null if there is no file already there
	 */
	public String loadName(){
		try {
			if(fileExists(file)){
				loadFile(file);
				return readFile(file);
			}else{
				return null;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Create's the file, filling the contents with the name
	 * @param name Name to save to the file
	 */
	public void saveName(String name){
		try {
			createFile(location);
			loadFile(file);
			writeToFile(name);
			closeFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
