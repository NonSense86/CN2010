package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import protokoll.IPacketTransmissionNotifications;
import protokoll.PacketTransmission;
import protokoll.PacketTransmissionInfo;
import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import protokoll.RemoteMachine;

import common.IKeepAlive;
import common.KeepAliveThread;
import common.Msg;
import common.MsgFactory;

public class ClientInstance implements IPacketTransmissionNotifications, IKeepAlive {

	private String[] params;
	private int port;
	private List<RemoteMachine> servers;
	private RemoteMachine server;
	private PackageListenerThread packageListenerThread;
	private Thread pollerThread;
	private PacketTransmission packetTransmission;
	private DatagramSocket clientSocket;	
	private boolean connected;
	private Thread keepAliveThread;
	private String name;
	BufferedReader in;

	public ClientInstance(String[] params) {
		this.params = params;
		connected = false;
		servers = new ArrayList<RemoteMachine>();
		init();
				
	}
	
	private void init() {
		try {
			if (params.length == 1) {
				readProperties(params[0]);
			} else if (params.length == 2) {
				port = Integer.parseInt(params[0]);
				server = new RemoteMachine(params[1]);
				servers.add(server);
			} else {
				System.out.println("Invalid argument count");
				System.out.println("USAGE: <myPort> <serverHost:serverPort>");
				System.exit(0);
			}		
			openConnection();
			
		} catch (Exception e) {
			System.out.println("Initialization failed");
			e.printStackTrace();
			System.exit(0);
		}

	}

	private int openConnection() throws IOException {
		
		clientSocket = new DatagramSocket(port);
		packetTransmission = new PacketTransmission(this, clientSocket);
		
		
		/*
		 * First - before we send connection request - make sure that we can
		 * receive the reply from the server. So we need a local Port where we
		 * listen on
		 */

		packageListenerThread = new PackageListenerThread(this);
		packageListenerThread.start();


		/* Now send the connection request to the server */
		RUDPPacket packet = RUDPPacketFactory.createConnectionRequestPacket(server);
		packetTransmission.sendPacket(packet);
		while(!connected) {
			System.out.println("Connecting...");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Connected!");
		keepAliveThread = new Thread(new KeepAliveThread(this));
		keepAliveThread.start();
		in = new BufferedReader(new InputStreamReader(System.in));
		setName();
		pollerThread = new Thread(new Poller(this));
		pollerThread.start();
		
		return packageListenerThread.getLastConnectionId();
	}
	
	private void setName() {
		System.out.println("Set your nickname:");
		while(name == null) {
			try {
				String input = "";
				input = in.readLine();
				input = input.trim();
				if(input.length() == 0) {
					System.out.println("The name can not be empty");
				} else {
					Msg msg = MsgFactory.createRenameMsg(input, null);
					RUDPPacket packet = RUDPPacketFactory.createPayloadPacket(server, msg);
					packetTransmission.sendPacket(packet);
					System.out.println("Checking name...");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Called from the transmission class if an ACK packet is missing Reason:
	 * connection is down or packet got lost
	 */
	@Override
	public void OnPacketACKMissing(PacketTransmissionInfo info) {
		/* Unblock all who are waiting for the ACK */
		//barrier.releaseAll();

		if(info.getSeqNumber() == 0) {
			System.out.println("Connecting to server ...");
		} else {
			System.out.println("***** Transmission: ACK missing: SEQ="
				+ info.getSeqNumber() + " SENT ON="
				+ info.getTransmissionDate().toString());
		}
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
				servers.add(server);
								
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

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public DatagramSocket getClientSocket() {
		return clientSocket;
	}

	@Override
	public PacketTransmission getPacketTransmission() {
		return packetTransmission;
	}

	public List<RemoteMachine> getServers() {
		return servers;
	}

	@Override
	public List<RemoteMachine> getActiveConnections() {
		return servers;
	}


	
	
	
}
