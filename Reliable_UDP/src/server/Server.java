package server;
import java.io.IOException;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		ServerInstance instance = new ServerInstance(args);
		//ServerInstance instance = new ServerInstance(4711);
		
		instance.StartListening();		
	}
}
