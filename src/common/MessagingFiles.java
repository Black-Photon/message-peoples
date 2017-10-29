package common;

import messageBoxes.sourceFiles.Error;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Contains all the methods needed to specifically work with the messaging_config.txt file
 */
public class MessagingFiles extends Files {
	//METHODS ----------------------------------------------------------------------------------------------------------

	//Technical Methods
	/**
	 * Reads the existing file data, or adds empty data to a new one
	 * @param location To read from
	 * @return Data from file
	 */
	public ArrayList<Connection_Data> readMessagingConfig(String location){
		try {

			File file = new File(location);
			if(!fileExists(file)){
				saveMessagingConfig(location, new ArrayList<>());
			}
			loadFile(file);
			String allData = readFile(file);
			ArrayList<Connection_Data> connection_data = parseMessagingConfig(allData);
			return connection_data;
		}catch(IOException e){
			new Error("Error #0017: IOException at MessagingFiles.java", 600);
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Saves given data to a given file location
	 * @param location To save in
	 * @param data To save
	 */
	public void saveMessagingConfig(String location, ArrayList<Connection_Data> data){
		//Ensure there are no duplicates
		for(int i = 0; i<data.size(); i++){
			for(int j = 0; j<data.size(); j++){
				if(data.get(i).sameAs(data.get(j)) && i!=j){
					data.remove(i);
				}
			}
		}
		try {
			createFile(location);
			writeMessagingFile(data);
		}catch(IOException e){
			new Error("Error #0018: IOException at MessagingFiles.java", 600);
			e.printStackTrace();
		}
	}
	/**
	 * Writes data in the form NAME,I.P.0.1,PORT1;
	 * @param data To write
	 */
	private void writeMessagingFile(ArrayList<Connection_Data> data){
		for(Connection_Data info: data){
			formatter.format("%s,%s,%s;", info.getName(), info.getIp(), info.getPort());
		}
		closeFile();
	}

	//Useful Methods
	/**
	 * Takes what's read, and convert's it to usable data
	 * @param data To analyse
	 * @return Data converted to ArrayList
	 */
	private ArrayList<Connection_Data> parseMessagingConfig(String data){
		ArrayList<Connection_Data> connection_data = new ArrayList<>();
		int count = 0;
		String name = "";
		String ip = "";
		int port = -1;
		StringBuilder word = new StringBuilder();
		for(char i: data.toCharArray()) {
			switch(i){
				case '\r':
				case '\n':
					break;
				case ' ': //Only allow in the name
					if(count==0){
						word.append(i);
					}
					break;
				case ',': //Save whatever has been last analysed, and reset the StringBuilder
					switch(count){
						case 0:
							name = word.toString();
							word = new StringBuilder();
							break;
						case 1:
							ip = word.toString();
							word = new StringBuilder();
							break;
					};
					if(count==2){
						System.out.println("Unexpected Variable");
					}
					count++;
					break;
				case ';': //Set the port and sets the data, adding it to the ArrayList
					if(count==2) {
						try {
							port = Integer.parseInt(word.toString());
							word = new StringBuilder();
						}catch(NumberFormatException e){
							new Error("Port from messaging_config.txt in wrong format",600);
							e.printStackTrace();
						}
					}
					if(name.equals("") || ip.equals("") || port==-1){
						System.out.println("Missing info");
						count = 0;
						break;
					}
					System.out.println("Reading connection "+name+" at "+ip+":"+port);
					Connection_Data connection = new Connection_Data(name, ip, port);
					connection_data.add(connection);
					count = 0;
					break;
				default:
					word.append(i);

			}
		}
		return connection_data;

	}
}
