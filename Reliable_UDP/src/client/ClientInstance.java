package client;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;

import protokoll.Barrier;
import protokoll.IPacketTransmissionNotifications;
import protokoll.PacketTansmission;
import protokoll.PacketTansmissionInfo;
import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import server.RemoteMachine;

public class ClientInstance implements IPacketTransmissionNotifications {

	private String[] params;
	private int port;
	private RemoteMachine server;
	private PackageListenerThread packageListenerThread;
	private Barrier barrier;
	private PacketTansmission packetTansmission;
	private DatagramSocket clientSocket;	

	public ClientInstance(String[] params) {
		this.params = params;
		init();
		barrier = new Barrier();
		packetTansmission = new PacketTansmission(this);		
	}

	/*
	public ClientInstance(InetAddress host, int port) {
		this();

		this.host = host;
		this.port = port;
	}
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

	public int OpenConnection() throws IOException {
		
		clientSocket = new DatagramSocket(port);
		
		packetTansmission.setSocket(clientSocket);
		
		/*
		 * First - before we send connection request - make sure that we can
		 * receive the reply from the server. So we need a local Port where we
		 * listen on
		 */

		packageListenerThread = new PackageListenerThread(barrier, packetTansmission, clientSocket);
		packageListenerThread.start();

		/* Block until server port is started */
		barrier.block();

		/* Now send the connection request to the server */
		RUDPPacket packet = RUDPPacketFactory
				.createConnectionRequestPacket();

		packetTansmission.SendPacket(packet, server.getHost(), server.getPort());

		/* Waiting for the server reply and return the connection id */
		barrier.block();

		return packageListenerThread.getLastConnectionId();
	}

	/**
	 * Function sends a packet to the server. The function blocks until
	 * receiving an response from the server.
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	public void Send(int connectionId, byte[] payload) throws IOException {
		RUDPPacket packet = RUDPPacketFactory.createPayloadPacket(connectionId,
			payload);

		packetTansmission.SendPacket(packet, server.getHost(), server.getPort());
	}

	/**
	 * Called from the transmission class if an ACK packet is missing Reason:
	 * connection is down or packet got lost
	 */
	@Override
	public void OnPacketACKMissing(PacketTansmissionInfo info) {
		/* Unblock all who are waiting for the ACK */
		barrier.releaseAll();

		System.out.println("***** Transmission: ACK missing: SEQ="
				+ info.getSeqNumber() + " SENT ON="
				+ info.getTransmissionDate().toString());
	}

	/**
	 * Called from the transmission class in the packet order is wrong
	 */
	@Override
	public void OnPacketWrongOrder() {
		System.out.println("***** Transmission: Packet wrong order");
	}

	@Override
	public void OnDuplicatePacket() {
		System.out.println("***** Transmission: Packet duplicates");

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
