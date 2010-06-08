package server;
import java.net.InetAddress;
import java.util.ArrayList;


public class ConnectionManager {

	private ArrayList<ConnectionInstance> clients_ = new ArrayList<ConnectionInstance>();
	private int connectionId_;
	
	public ConnectionManager() {
		connectionId_ = 0;
	}
	
	public ConnectionInstance RegisterNewClientConnection(InetAddress clientAddress, int clientPort) {
		
		ConnectionInstance instance = new ConnectionInstance();
		
		instance.setClientAddress(clientAddress);
		instance.setClientPort(clientPort);
		instance.setConnectionId(getNextConnectionId());
		
		clients_.add(instance);
		
		return instance;		
	}
	
	private synchronized int getNextConnectionId() {
		return ++connectionId_;
	}
}
