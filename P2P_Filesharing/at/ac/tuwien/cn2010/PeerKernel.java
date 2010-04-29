package at.ac.tuwien.cn2010;

import java.net.InetAddress;

import at.ac.tuwien.cn2010.services.SuperPeerServiceInstance;

public class PeerKernel {

	private static final int START_AS_PEER = 0x0;
	private static final int START_AS_SUPERPEER = 0x1;

	private InetAddress address_;
	private int port_;
	private Thread superPeerNeighborRequestorThread_;
	private SuperPeerNeighborStorage storage_ = new SuperPeerNeighborStorage();

	public PeerKernel(InetAddress address, int port) {
		startUpAs_ = START_AS_PEER;

		this.address_ = address;
		this.port_ = port;
	}

	private int startUpAs_;

	/**
	 * Starts the peer as a SuperPeer
	 * 
	 * @return the PeerKernel instance
	 */
	public PeerKernel StartUpAsPeer() {
		startUpAs_ = START_AS_PEER;

		return this;
	}

	/**
	 * Starts the peer as a Peer
	 * 
	 * @return the PeerKernel instance
	 */
	public PeerKernel StartUpAsSuperPeer() {
		startUpAs_ = START_AS_SUPERPEER;

		return this;
	}

	private IPeer currentPeer_;

	/**
	 * Starts the boot proccess. First the Peer instance is created, then other
	 * initial tasks are done
	 */
	public void StartBoot() {

		System.out.println("Booting Peer ...");

		/**
		 * Start the services to listen to other requests
		 */
		if (startUpAs_ == START_AS_PEER) {

		}

		if (startUpAs_ == START_AS_SUPERPEER) {
			currentPeer_ = new SuperPeerServiceInstance(address_, port_, storage_);
		}

		currentPeer_.Run();

		/**
		 * Start neighbor requestor
		 */

		superPeerNeighborRequestorThread_ = new Thread(
				new SuperPeerNeighborRequestorScheduler(storage_));
		
		superPeerNeighborRequestorThread_.start();
	}

	/**
	 * Reboots the peer. Startsup the same peer type as provided before
	 */
	public void Reboot() {
		this.Stop();

		/**
		 * Restart
		 */
		System.out.println("Starting Peer ...");
		currentPeer_.Run();
		
		System.out.println("Starting SuperPeerNeighborRequestorThread ...");
		superPeerNeighborRequestorThread_.start();
	}

	/**
	 * Stops the peer
	 */
	public void Stop() {
		System.out.println("Stopping Peer ...");
		currentPeer_.Stop();
		
		System.out.println("Stopping SuperPeerNeighborRequestorThread ...");
		superPeerNeighborRequestorThread_.stop();
	}
	
	/**
	 * Returns a reference to the peer storage
	 * @return
	 */
	public SuperPeerNeighborStorage getSuperPeerNeighborStorageReference() {
		return storage_;
	}

}
