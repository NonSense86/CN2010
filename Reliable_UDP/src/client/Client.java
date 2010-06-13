package client;
import java.net.*; 
 
public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception 
	{	
		ClientInstance instance = new ClientInstance(args);
		//ClientInstance instance = new ClientInstance(InetAddress.getByName("localhost"), 4711);
		
		//int connId = instance.OpenConnection();

		//System.out.println("*****" + connId);
		
		//instance.Send(connId, new String("Hallo Welt").getBytes("UTF-8"));
	}

}
