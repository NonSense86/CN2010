package core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;


public class Server {

	private String[] params;
	private ArrayList<RemoteMachine> servers = new ArrayList<RemoteMachine>();
	private int port;
	private String serverList;
	
	public Server(String[] params) {
		this.params = params;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Server s = new Server(args);
		s.init();
		
		// Print read values
		System.out.println(s.port);
		for (RemoteMachine rm : s.servers)
			System.out.println(rm.getHost() + ":" + rm.getPort());
	}
	
	/**
	 * Initializes the server.
	 */
	private void init() {
		
		try {
			if (params.length == 0) {
				System.out.println("Invalid argument count");
				System.out.println("USAGE: <myPort> [<serverHost:serverPort>]");
			} else if (params.length == 1) {
				readProperties(params[0]);
				readServers(serverList);
			} else {
				port = Integer.parseInt(params[0]);
				for (int i = 1; i < params.length; i++) {
					servers.add(new RemoteMachine(params[i]));
				}	
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
				serverList = properties.getProperty("serverList");
								
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
	
	/**
	 * Reads servers from a specified file.
	 * @param serverList Name of the file where the server list is stored
	 */
	private void readServers(String serverList) {
		
		try {
			FileInputStream fstream = new FileInputStream(serverList);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				servers.add(new RemoteMachine(strLine));
		    }
			//Close the input stream
			in.close();
		} catch (Exception e) {
		   	System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	

}
