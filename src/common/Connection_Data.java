package common;

import java.util.ArrayList;

/**
 * Holds info about connection data for the table
 */
public class Connection_Data {
	//VARIABLES --------------------------------------------------------------------------------------------------------

	//Data
	private String name;
	/**
	 * Contains the IP in String format
	 */
	private String ip;
	/**
	 * Holds the IP as an ArrayList of int's
	 */
	private ArrayList<Integer> ipArray;
	private int port;

	//Global Variables
	private StringBuilder temp;



	//METHODS ----------------------------------------------------------------------------------------------------------

	//Constructors
	public Connection_Data(String name, String ip, int port){
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.ipArray = ipToArray(ip);
	}
	public Connection_Data(String name, ArrayList ip, int port){
		this.name = name;
		this.ipArray = ip;
		this.port = port;
		this.ip = arrayToIp(ipArray);
		System.out.println("Created Connection "+name+" at "+this.ip+":"+port);
	}

	//Useful
	/**
	 * Checks if the data is the same
	 * @param data Data to compare to
	 * @return True if they are the same
	 */
	public boolean sameAs(Connection_Data data){
		if(name.equals(data.name) && ip.equals(data.ip) && port == data.port){
			return true;
		}
		return false;
	}
	/**
	 * Ip in String form, eg a.b.c.d
	 * @return String ip
	 */
	public String arrayToIp(){
		return arrayToIp(ipArray);
	}
	/**
	 * Ip in String form, eg a.b.c.d
	 * @return String ip
	 */
	private String arrayToIp(ArrayList array){
		StringBuilder sb = new StringBuilder();
		sb.append(array.get(0));
		sb.append(".");
		sb.append(array.get(1));
		sb.append(".");
		sb.append(array.get(2));
		sb.append(".");
		sb.append(array.get(3));
		return sb.toString();
	}
	/**
	 * Ip in ArrayList form, eg [a,b,c,d]
	 * @return ArrrayList ip
	 */
	public ArrayList<Integer> ipToArray(){
		return ipToArray(ip);
	}
	/**
	 * Ip in ArrayList form, eg [a,b,c,d]
	 * @return ArrayList ip
	 */
	private ArrayList<Integer> ipToArray(String text){
		temp = new StringBuilder();
		ipArray = new ArrayList<>();
		for(char i:text.toCharArray()){
			if(i=='.'){
				addToArray();
			}else{
				temp.append(i);
			}
		}
		addToArray();

		return ipArray;
	}
	/**
	 * Add's a number to the ipArray
	 * ONLY use from ipToArray method
	 */
	private void addToArray(){
		try {
			ipArray.add(Integer.parseInt(temp.toString()));
			temp = new StringBuilder();
		}catch (NumberFormatException e){
			new Error("IP not a number");
			e.printStackTrace();
		}
	}

	//Getters and Setters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public ArrayList<Integer> getIpArray() {
		return ipArray;
	}
	public void setIpArray(ArrayList<Integer> ipArray) {
		this.ipArray = ipArray;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
