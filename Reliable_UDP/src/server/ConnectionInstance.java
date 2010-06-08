package server;
import java.net.InetAddress;


public class ConnectionInstance {

	private InetAddress clientAddress_;
	private int clientPort_;
	private int connectionId_;
	
	public InetAddress getClientAddress() {
		return clientAddress_;
	}
	
	public void setClientAddress(InetAddress clientAddress) {
		clientAddress_ = clientAddress;
	}
	
	public int getClientPort() {
		return clientPort_;
	}
	
	public void setClientPort(int clientPort) {
		clientPort_ = clientPort;
	}	
	
	public int getConnectionId() {
		return connectionId_;
	}
	
	public void setConnectionId(int connectionId) {
		connectionId_ = connectionId;
	}	
}
