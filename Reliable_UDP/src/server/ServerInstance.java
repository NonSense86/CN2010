package server;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import common.IKeepAlive;
import common.KeepAliveThread;

import protokoll.IPacketTransmissionNotifications;
import protokoll.PacketTransmission;
import protokoll.PacketTransmissionInfo;
import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import protokoll.RemoteMachine;


public class ServerInstance implements IPacketTransmissionNotifications, IKeepAlive {

	private static Logger logger = Logger.getLogger("ServerInstance");
	private int port;
	private String serverList;
	private List<RemoteMachine> servers;
	
	private PacketTransmission packetTansmission;
	private DatagramSocket socket;
	private String[] params;
	private MessageBroker broker;
	private Thread keepAliveThread;	
	private float probability;

	public ServerInstance(String[] params) {
		this.params = params;
		init();
	}
	
	private void startListening() throws SocketException, IOException {
		System.out.println("Server is now listing on port " + port);

		socket = new DatagramSocket(port);	
		packetTansmission = new PacketTransmission(this, socket, probability);
		
		broker = new MessageBroker(this);
		connectToServers();
		keepAliveThread = new Thread(new KeepAliveThread(this));
		keepAliveThread.start();
		
		while (true) {
			DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
			socket.receive(packet);	
			broker.processPacket(packet);
			//pool.execute(new MsgProcessor(broker, packet));
		}
	}
	
	private void init() {
		servers = new ArrayList<RemoteMachine>();
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
			startListening();
		} catch (Exception e) {
			System.out.println("Initialization failed");
			e.printStackTrace();
			System.exit(0);
		}
		
	}
	
	private void connectToServers() {
		try {
			for(RemoteMachine rm : servers) {
				// Exclude self
				if(!rm.toString().equals("127.0.0.1" + port)) {
					RUDPPacket packet = RUDPPacketFactory.createConnectionRequestPacket(rm);
					packetTansmission.sendPacket(packet);
					logger.info("Connecting to " + rm.toString());
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				probability = Float.parseFloat(properties.getProperty("probability"));
								
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
	
	@Override
	public void onDuplicatePacket() {
		System.out.println("***** Transmission: Packet duplicates");
		
	}

	@Override
	public void onPacketWrongOrder() {
		System.out.println("***** Transmission: Packet wrong order");
		
	}

	public List<RemoteMachine> getServers() {
		return servers;
	}

	@Override
	public List<RemoteMachine> getActiveConnections() {
		return new ArrayList<RemoteMachine>(broker.getCm().getServers().values());
	}

	@Override
	public PacketTransmission getPacketTransmission() {
		return packetTansmission;
	}

	public MessageBroker getBroker() {
		return broker;
	}	
	
	
	
}
