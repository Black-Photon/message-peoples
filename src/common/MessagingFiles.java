package src.common;

import src.messageBoxes.Error;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MessagingFiles extends Files {

	public ArrayList<Connection_Data> readMessagingConfig(String location){
		try {
			File file = new File(location);
			if(!file.isFile()){
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
	public void saveMessagingConfig(String location, ArrayList<Connection_Data> data){
		for(int i = 0; i<data.size(); i++){
			for(int j = 0; j<data.size(); j++){
				if(data.get(i).sameAs(data.get(j)) && i!=j){
					data.remove(i);
				}
			}
		}
		try {
			createFile(location);
			writeToFile(";");
			writeMessagingFile(data);
		}catch(IOException e){
			new Error("Error #0018: IOException at MessagingFiles.java", 600);
			e.printStackTrace();
		}
	}
	ArrayList<Connection_Data> parseMessagingConfig(String data){
		ArrayList<Connection_Data> connection_data = new ArrayList<>();
		int count = 0;
		String name = "";
		String ip = "";
		int port = -1;
		StringBuilder word = new StringBuilder();
		for(char i: data.substring(1).toCharArray()) {
			switch(i){
				//case '.':
				//
				//
				//	break;
				case '\r':
				case '\n':
					break;
				case ' ':
					if(count==0){
						word.append(i);
					}
					break;
				case ',':
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
				case ';':
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
	//Writes the database to the file
	void writeMessagingFile(ArrayList<Connection_Data> data){
		for(Connection_Data info: data){
			formatter.format("%s,%s,%s;", info.getName(), info.getIp(), info.getPort());
		}
		closeFile();
	}
}
