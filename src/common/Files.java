package src.common;

import src.messageBoxes.Error;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Scanner;

/**
 * File from jOSeph 3 optimised for jOSeph 4
 *
 * Imagine the formatter to be the file
 */

public class Files {

	//Variables
	Scanner scanner;

	Formatter formatter;


	//Creating a formatter creates a file
	void createFile(String location) throws IOException{
		formatter = new Formatter(location);
	}
	//Creating a scanner allows the file to be read
	void loadFile(File file) throws IOException{
		scanner = new Scanner(file);
	}

	//The formatter is the only thing need be closed
	void closeFile(){
		formatter.close();
	}
	boolean fileExists(File file){
		return file.isFile();
	}

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
	File[] getAllFiles(String folderName){
		File folder = new File(folderName);
		return folder.listFiles();
	}
	String readFile(File file){
		scanner.useDelimiter("\\Z"); //???
		if(scanner.hasNext()) {
			return scanner.next();
		}
		return "";
	}
	public void createAndCloseFile(String path){
		try {
			createFile(path);
			formatter.format(" ");
			closeFile();
		}catch(IOException e){
			e.printStackTrace();
			new Error("Error #0014: IOException at Files.java", 600);
		}
	}
	void writeToFile(String text){
		formatter.format(text);
	}
}



















