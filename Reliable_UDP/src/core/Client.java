package core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Client {

	private String[] params;
	private int port;
	private RemoteMachine server;
	
	
	public Client(String[] params) {
		this.params = params;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Client c = new Client(args);
		c.init();
		
		// Print read values
		System.out.println(c.port);
		System.out.println(c.server.getHost() + ":" + c.server.getPort());
		
	}
	
	/**
	 * Initializes the client.
	 */
	private void init() {
		try {
			if (params.length == 1) {
				readProperties(params[0]);
			} else if (params.length == 2) {
				port = Integer.parseInt(params[0]);
				server = new RemoteMachine(params[1]);
			} else {
				System.out.println("Invalid argument count");
				System.out.println("USAGE: <myPort> <serverHost:serverPort>");
				System.exit(0);
			}
			
			
		} catch (Exception e) {
			System.out.println("Initialization failed");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Reads properties form specified property file.
	 * @param fileName Name of the file where properties are stored
	 * @throws Exception
	 */
	private void readProperties(String fileName) throws Exception {
		InputStream is = ClassLoader.getSystemResourceAsStream(fileName);

		if (is != null) {
			Properties properties = new Properties();
			try {
				properties.load(is);
				port = Integer.parseInt(properties.getProperty("port"));
				server = new RemoteMachine(properties.getProperty("hostPort"));
								
			} catch (Exception e) {
				throw new Exception();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Properties file not found.");
			System.exit(0);
		}
	}

}
