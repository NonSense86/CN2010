import java.net.InetAddress;
import java.util.ArrayList;

import at.ac.tuwien.cn2010.SuperPeerInformation;

/**
 * Container class holding the startup configuration
 * Only needed in startup phase
 */
public class ConfigReaderResult {

	private ArrayList<SuperPeerInformation> peers_ = new ArrayList<SuperPeerInformation>();
	
	public ArrayList<SuperPeerInformation> getPeersListReference() {
		return peers_;
	}
	
	private int myPort_;
	
	public int getMyPort() {
		return myPort_;
	}
	
	public void setMyPort(int myPort) {
		myPort_ = myPort;
	}
	
	private InetAddress myAddress_;
	
	public InetAddress getMyAddress() {
		return myAddress_;
	}
	
	public void setMyAddress(InetAddress myAddress) {
		myAddress_ = myAddress;
	}	
}
