package at.ac.tuwien.cn2010;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import at.ac.tuwien.cn2010.client.SuperPeerInformationTransferObject;
import at.ac.tuwien.cn2010.client.SuperPeerListener;
import at.ac.tuwien.cn2010.client.SuperPeerListenerService;

/**
 * Asks the neighbors for their known neighbor clients Apply the neighbor to the
 * storage
 */
public class SuperPeerNeighborRequestorScheduler implements Runnable {

	private SuperPeerNeighborStorage storage_;

	public SuperPeerNeighborRequestorScheduler(SuperPeerNeighborStorage storage) {
		this.storage_ = storage;
	}

	@Override
	public void run() {
		System.out.println("[SuperPeerNeighborRequestorScheduler] Running ...");

		Timer timer = new Timer();

		timer.schedule(new Task(storage_), 1000, 1000);
	}
}

/**
 * called by the Time scheduler
 */
class Task extends TimerTask {

	private SuperPeerNeighborStorage storage_;

	public Task(SuperPeerNeighborStorage storage) {
		this.storage_ = storage;
	}

	private ArrayList<SuperPeerInformation> tmpNewPeers_ = new ArrayList<SuperPeerInformation>();

	@Override
	public void run() {
		System.out
				.println("[SuperPeerNeighborRequestorScheduler] Scheduling ...");
		
		storage_.acquireLock();

		try {

			tmpNewPeers_.clear();

			Iterator<SuperPeerInformation> it = storage_
					.getNeighborPeersListReference().iterator();

			while (it.hasNext()) {

				SuperPeerInformation peer = it.next();

				try {
					RequestNeighborPeers(peer);
					
					/**
					 * Request worked without exception. So we expect
					 * that the client is alive. Remember the time it
					 * happened
					 */
					storage_.updateSuperPeerNeighborLastSeen(peer);
				} catch (WebServiceException ex) {
					
					System.out.println("[SuperPeerNeighborRequestorScheduler] Request failed. Is super peer alive?");
					
					/**
					 * The neighbour doesnt respond - remove him if the time
					 * we saw him last is far away from now (30 seconds)
					 */
					if(new Date().getTime() - peer.getPeerLastSeen().getTime() > 5000) {	
						System.out.println("[SuperPeerNeighborRequestorScheduler] Remove SuperPeer - 30 seconds no responds");
						
						it.remove();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			for (SuperPeerInformation peer : tmpNewPeers_) {
				storage_.addSuperPeerNeighbor(peer);
			}
		} finally {
			storage_.releaseLock();			
		}
	}

	/**
	 * Request SuperPeers from a specifc peer. After the request the peer is
	 * added to a temp Array because the requestor list cant be modified
	 * 
	 * @param peer
	 * @throws MalformedURLException
	 * @throws MalformedURLException
	 */
	private void RequestNeighborPeers(SuperPeerInformation peer)
			throws MalformedURLException {

		System.out.println("[SuperPeerNeighborRequestorScheduler] Request to "
				+ peer.getPeerURL());

		SuperPeerListenerService service = new SuperPeerListenerService(peer
				.getPeerURL(), peer.getServiceName());

		SuperPeerListener listener = service.getSuperPeerListenerPort();

		/**
		 * Add all Super Peers we got from or neighbour to our local storage If
		 * the peer is already in the local storage -> this is a problem of the
		 * local storage - he has to take care of that
		 */

		for (SuperPeerInformationTransferObject info : listener
				.getAllSuperPeers()) {

			SuperPeerInformation peerInfo = new SuperPeerInformation();

			peerInfo.setPeerURL(new URL(info.getPeerUrl()));
			peerInfo.setServiceName(new QName(info.getPeerUrl(), info
					.getServiceName()));

			tmpNewPeers_.add(peerInfo);
		}
	}
}
