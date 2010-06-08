package server;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Properties;

import protokoll.IPacketTransmissionNotifications;
import protokoll.PacketTansmission;
import protokoll.PacketTansmissionInfo;


public class ServerInstance implements IPacketTransmissionNotifications {

	private int port;
	private String serverList;
	private ArrayList<RemoteMachine> servers = new ArrayList<RemoteMachine>();
	private PacketTansmission packetTansmission;
	private DatagramSocket socket;
	private String[] params;

	public ServerInstance(String[] params) {
		this.params = params;
		init();
	}
	
	/*
	public ServerInstance(int port) {
		this.port = port;
		packetTansmission = new PacketTansmission(this);
	}
	*/

	public void StartListening() throws SocketException, IOException {
		System.out.println("Server is now listing on port " + port);

		socket = new DatagramSocket(port);	
		
		packetTansmission.setSocket(socket);
		
		MessageBroker broker = new MessageBroker(this);

		while (true) {
			DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
			socket.receive(packet);			

			broker.ProceedPacket(packet);
		}
	}
	
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
		packetTansmission = new PacketTansmission(this);
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
	
	public PacketTansmission getPacketTansmission() {
		return this.packetTansmission;
	}

	@Override
	public void OnDuplicatePacket() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnPacketACKMissing(PacketTansmissionInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnPacketWrongOrder() {
		// TODO Auto-generated method stub
		
	}	
}
