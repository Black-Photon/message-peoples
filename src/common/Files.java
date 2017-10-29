package common;

import messageBoxes.sourceFiles.Error;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Scanner;

/**
 * File from jOSeph 3 optimised for jOSeph 4 and implemented into Messaging<br/><br/>
 *
 * Imagine the formatter to be the file
 */
public class Files {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//File Variables
	Scanner scanner;
	Formatter formatter;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Technical Methods
	/**
	 * Creates the file by creating a formatter at the given location
	 * @param location Of the file
	 * @throws FileNotFoundException If the file can't be found or created
	 */
	protected void createFile(String location) throws FileNotFoundException{
		formatter = new Formatter(location);
	}
	/**
	 * Creates a scanner to read the file
	 * @param file File to load
	 * @throws FileNotFoundException If the file does not exist
	 */
	protected void loadFile(File file) throws FileNotFoundException{
		scanner = new Scanner(file);
	}
	/**
	 * Closes the file. The scanner is not needed to be closed
	 */
	protected void closeFile(){
		formatter.close();
	}
	/**
	 * Returns an array of all files in a given folder
	 * @param folderName Folder Name
	 * @return Array of Files
	 */
	protected File[] getAllFiles(String folderName){
		File folder = new File(folderName);
		return folder.listFiles();
	}
	/**
	 * Returns the entire contents of a file in a single string
	 * @param file File to use
	 * @return File Contents
	 */
	protected String readFile(File file){
		scanner.useDelimiter("\\Z"); //???
		if(scanner.hasNext()) {
			return scanner.next();
		}
		return "";
	}
	/**
	 * Writes the text to the file
	 * @param text Text to write
	 */
	protected void writeToFile(String text){
		formatter.format(text);
	}

	//Useful Methods
	/**
	 * Checks if the file exists
	 * @param file File to check
	 * @return True if the file exists
	 */
	protected boolean fileExists(File file){
		return file.isFile();
	}

	//Public methods
	/**
	 * Returns an ArrayList of all text files in a given folder (End with .txt)
	 * @param folderName Folder to check
	 * @return List of text files
	 */
	public ArrayList<File> getAllTextFiles(String folderName){
		File[] filesArray = getAllFiles(folderName);
		ArrayList<File> files = new ArrayList<>(Arrays.asList(filesArray));
		for(File i: files){
			if(!(i.isFile() && i.getName().endsWith(".txt"))){
				files.remove(i);
			}
		}
		return files;
	}
	/**
	 * Simply creates an empty file (Well, filled with a single space)
	 * @param path Path to create file at (Include filename)
	 */
	public void createAndCloseFile(String path){
		try {
			createFile(path);
			formatter.format(" ");
			closeFile();
		}catch(IOException e){
			e.printStackTrace();
			new Error("Error #0014: IOException at Files.java", 600).showWindow();
		}
	}
}



















