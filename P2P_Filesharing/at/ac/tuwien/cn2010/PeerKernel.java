package at.ac.tuwien.cn2010;

public class PeerKernel {
	
	private static final int START_AS_PEER = 0x0;
	private static final int START_AS_SUPERPEER = 0x1;
	
	public PeerKernel() {
		startUpAs_ = START_AS_PEER;
	}
	
	private int startUpAs_;
	
	/**
	 * Starts the peer as a SuperPeer
	 * @return the PeerKernel instance
	 */
	public PeerKernel StartUpAsPeer() {
		startUpAs_ = START_AS_PEER;
		
		return this;
	}
	
	/**
	 * Starts the peer as a Peer
	 * @return the PeerKernel instance
	 */
	public PeerKernel StartUpAsSuperPeer() {
		startUpAs_ = START_AS_SUPERPEER;
		
		return this;
	}
	
	private IPeer currentPeer_;
	
	/**
	 * Starts the boot proccess. First the Peer instance is created,
	 * then other initial tasks are done
	 */
	public void StartBoot() {
		
		System.out.println("Booting Peer ...");
		
		if(startUpAs_ == START_AS_PEER) {
			
		}
		
		if(startUpAs_ == START_AS_SUPERPEER) {
			currentPeer_ = new SuperPeerInstance();
		}
		
		currentPeer_.Run();
	}
	
	/**
	 * Reboots the peer. Startsup the same peer type as provided before
	 */
	public void Reboot() {
		System.out.println("Stopping Peer ...");		
		currentPeer_.Stop();
		
		System.out.println("Starting Peer ...");
		currentPeer_.Run();
	}
	
	/**
	 * Stops the peer
	 */
	public void Stop() {
		System.out.println("Stopping Peer ...");
		currentPeer_.Stop();
	}

}
