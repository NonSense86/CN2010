package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import protokoll.IPacketTransmissionNotifications;
import protokoll.PacketTransmission;
import protokoll.PacketTransmissionInfo;
import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import protokoll.RemoteMachine;
import cmd.Command;
import cmd.MulticastCmd;
import cmd.RenameCmd;
import cmd.UnicastCmd;

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
	private boolean nameChecked;
	private Thread keepAliveThread;
	private String name;
	private BufferedReader in;
	private Map<String, Command> commands;
	private float probability;

	public ClientInstance(String[] params) {
		this.params = params;
		connected = false;
		servers = new ArrayList<RemoteMachine>();
		commands = new Hashtable<String, Command>();
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
			insertCommands();
			openConnection();
			
		} catch (Exception e) {
			System.out.println("Initialization failed");
			e.printStackTrace();
			System.exit(0);
		}

	}
	
	private void insertCommands() {
		commands.put("rename", new RenameCmd());
		commands.put("unicast", new UnicastCmd());
		commands.put("multicast", new MulticastCmd());
	}

	private int openConnection() throws IOException {
		
		clientSocket = new DatagramSocket(port);
		packetTransmission = new PacketTransmission(this, clientSocket, probability);
		
		
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
		
		System.out.println("Type exit to end");
		readInput();
		System.out.println("Bye!");
		System.exit(0);
		
		return packageListenerThread.getLastConnectionId();
	}
	
	private void readInput() {
		String[] args;
		Command command;
		
		while (true) {
			try {
				String input = "";
				input = in.readLine();
				input = input.trim();
				if (input.length() > 0) {
					if(input.equalsIgnoreCase("exit"))
						break;
					args = generateArgs(input);
					command = commands.get(args[0]);
					if (command == null)
						System.out.println("Invalid command: " + args[0]);
					else
						command.execute(this, args);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setName() {
		while(name == null) {
			System.out.println("Set your nickname:");
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
					while(!nameChecked) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (name == null) {
						System.out.println("This name is unavailable");
						nameChecked = false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Name set to: " + name);
		nameChecked = false;
		
	}

	

	/**
	 * Called from the transmission class in the packet order is wrong
	 */
	@Override
	public void onPacketWrongOrder() {
		System.out.println("***** Transmission: Packet wrong order");
	}

	@Override
	public void onDuplicatePacket() {
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
	 * Generates arguments from the user input.
	 * @param input String read from user
	 * @return String array of arguments
	 */
	public static String[] generateArgs(String input) {
		String[] split1 = input.split(" ");
		List<String> list = new ArrayList<String>();
		boolean flag = false;
		String temp = "";
		for (String s : split1) {
			if (s.length() == 1) {
				if (s.equals("\"")) {
					flag = true;
					temp += s;
					continue;
				}
				list.add(s);					
			} else if ((s.charAt(0) == '\"') && (s.charAt(s.length() - 1) == '\"')) {
				s = s.substring(0, s.length());
				list.add(s);
			} else if ((s.charAt(0) == '\"') && !flag) {
				flag = true;
				s = s.substring(0, s.length());
				temp += s;
				temp += " ";
				continue;
			} else if ((s.charAt(s.length() - 1) == '\"') && flag) {
				s = s.substring(0, s.length());
				temp += s;
				flag = false;
				list.add(temp);
				temp = "";
				continue;
			} else if (flag) {
				temp += s;
				temp += " ";
				continue;
			} else
				list.add(s);
		}
		String[] args = list.toArray(new String[0]);
		return args;
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

	public void setName(String name) {
		if(name == null)
			return;
		this.name = name;
	}

	public void setNameChecked(boolean nameOK) {
		this.nameChecked = nameOK;
	}

	public String getName() {
		return name;
	}

	public boolean isNameChecked() {
		return nameChecked;
	}


	
	
	
}
